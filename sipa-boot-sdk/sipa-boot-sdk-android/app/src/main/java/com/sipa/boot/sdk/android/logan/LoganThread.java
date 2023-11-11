/*
 * Copyright (c) 2018-present, 美团点评
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.sipa.boot.sdk.android.logan;

import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class LoganThread extends Thread {
  private static final String TAG = "LoganThread";
  private static final int ONE_KB = 1024;
  private static final long ONE_MINUTE = 60 * 1000;
  private static final long ONE_DAY = 24 * 60 * 60 * 1000;

  private final Object sync = new Object();
  private final Object sendSync = new Object();
  private final ConcurrentLinkedQueue<LoganModel> mCacheSendQueue = new ConcurrentLinkedQueue<>(); // 发送缓存队列
  private final ConcurrentLinkedQueue<LoganModel> mCacheLogQueue;
  private final String mCachePath; // 缓存文件路径
  private final String mPath; // 文件路径
  private final long mSaveTime; // 存储时间
  private final long mMaxFile; // 最大文件大小
  private final long mMinCapacity; // 最小洛里昂
  private final String mEncryptKey16;
  private final String mEncryptIv16;

  private volatile boolean mIsRun = true;
  private long mCurrentDay;
  private volatile boolean mIsWorking;
  private File mFileDirectory;
  private boolean mIsReachMinCapacity;
  private long mLastTime;
  private LoganProtocol mLoganProtocol;
  private int mSendLogStatusCode;
  private ExecutorService mSingleThreadExecutor;

  LoganThread(ConcurrentLinkedQueue<LoganModel> cacheLogQueue, String cachePath,
              String path, long saveTime, long maxFile, long minCapacity, String encryptKey16,
              String encryptIv16) {
    mCacheLogQueue = cacheLogQueue;
    mCachePath = cachePath;
    mPath = path;
    mSaveTime = saveTime;
    mMaxFile = maxFile;
    mMinCapacity = minCapacity;
    mEncryptKey16 = encryptKey16;
    mEncryptIv16 = encryptIv16;
  }

  void notifyRun() {
    if (!mIsWorking) {
      synchronized (sync) {
        sync.notify();
      }
    }
  }

  void quit() {
    mIsRun = false;
    if (!mIsWorking) {
      synchronized (sync) {
        sync.notify();
      }
    }
  }

  @Override
  public void run() {
    super.run();
    while (mIsRun) {
      synchronized (sync) {
        mIsWorking = true;
        try {
          LoganModel model = mCacheLogQueue.poll();
          if (model == null) {
            mIsWorking = false;
            sync.wait();
            mIsWorking = true;
          } else {
            action(model);
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
          mIsWorking = false;
        }
      }
    }
  }

  private void action(LoganModel model) {
    if (model == null || !model.isValid()) {
      return;
    }
    if (mLoganProtocol == null) {
      mLoganProtocol = LoganProtocol.newInstance();
      mLoganProtocol.setOnLoganProtocolStatus(Logan::onListenerLogWriteStatus);
      mLoganProtocol.logan_init(mCachePath, mPath, (int) mMaxFile, mEncryptKey16, mEncryptIv16);
      mLoganProtocol.logan_debug(Logan.sDebug);
    }

    if (model.action == LoganModel.Action.WRITE) {
      doWriteLog2File(model.writeAction);
    } else if (model.action == LoganModel.Action.SEND) {
      if (model.sendAction.sendLogRunnable != null) {
        synchronized (sendSync) {
          if (mSendLogStatusCode == SendLogRunnable.SENDING) {
            mCacheSendQueue.add(model);
          } else {
            doSendLog2Net(model.sendAction);
          }
        }
      }
    } else if (model.action == LoganModel.Action.FLUSH) {
      doFlushLog2File();
    }
  }

  private void doWriteLog2File(WriteAction action) {
    if (Logan.sDebug) {
      Log.d(TAG, "Logan write start");
    }

    if (mFileDirectory == null) {
      mFileDirectory = new File(mPath);
    }

    if (!isSameDay()) {
      long tempCurrentDay = Util.ts();

      // 删除时间
      long deleteTime = tempCurrentDay - mSaveTime;
      deleteExpiredFile(deleteTime);

      mCurrentDay = tempCurrentDay;
      mLoganProtocol.logan_open(String.valueOf(mCurrentDay));
    }

    // 每隔1分钟判断一次
    long currentTime = System.currentTimeMillis();
    if (currentTime - mLastTime > ONE_MINUTE) {
      mIsReachMinCapacity = canWrite();
    }
    mLastTime = System.currentTimeMillis();

    // 如果大于50M 不让再次写入
    if (!mIsReachMinCapacity) {
      return;
    }

    mLoganProtocol.logan_write(action.flag, action.log, action.localTime, action.threadName,
      action.threadId, action.isMainThread);
  }

  private boolean isSameDay() {
    long currentTime = System.currentTimeMillis();
    return mCurrentDay < currentTime && mCurrentDay + ONE_DAY > currentTime;
  }

  private void deleteExpiredFile(long deleteTime) {
    File dir = new File(mPath);
    if (dir.isDirectory()) {
      String[] files = dir.list();
      if (files != null) {
        for (String item : files) {
          try {
            if (TextUtils.isEmpty(item)) {
              continue;
            }
            String[] longStrArray = item.split("\\.");
            if (longStrArray.length > 0) {  // 小于时间就删除
              long longItem = Long.parseLong(longStrArray[0]);
              if (longItem <= deleteTime && longStrArray.length == 1) {
                new File(mPath, item).delete(); // 删除文件
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  private boolean canWrite() {
    boolean canWrite = false;
    try {
      StatFs stat = new StatFs(mPath);
      long blockSize = stat.getBlockSize();
      long availableBlocks = stat.getAvailableBlocks();
      long total = availableBlocks * blockSize;
      if (total > mMinCapacity) {
        canWrite = true;
      }
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
    return canWrite;
  }

  private void doSendLog2Net(SendAction action) {
    if (Logan.sDebug) {
      Log.d(TAG, "Logan send start");
    }
    if (TextUtils.isEmpty(mPath) || action == null || !action.isValid()) {
      return;
    }
    boolean success = prepareLogFile(action);
    if (!success) {
      if (Logan.sDebug) {
        Log.d(TAG, "Logan prepare log file failed, can't find log file");
      }
      return;
    }
    action.sendLogRunnable.setSendAction(action);
    action.sendLogRunnable.setCallBackListener(
      statusCode -> {
        synchronized (sendSync) {
          mSendLogStatusCode = statusCode;
          if (statusCode == SendLogRunnable.FINISH) {
            mCacheLogQueue.addAll(mCacheSendQueue);
            mCacheSendQueue.clear();
            notifyRun();
          }
        }
      });
    mSendLogStatusCode = SendLogRunnable.SENDING;
    if (mSingleThreadExecutor == null) {
      mSingleThreadExecutor = Executors.newSingleThreadExecutor(r -> {
        // Just rename Thread
        Thread t = new Thread(Thread.currentThread().getThreadGroup(), r,
          "logan-thread-send-log", 0);
        if (t.isDaemon()) {
          t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
          t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
      });
    }
    mSingleThreadExecutor.execute(action.sendLogRunnable);
  }

  private void doFlushLog2File() {
    if (Logan.sDebug) {
      Log.d(TAG, "Logan flush start");
    }
    if (mLoganProtocol != null) {
      mLoganProtocol.logan_flush();
    }
  }

  /**
   * 发送日志前的预处理操作
   */
  private boolean prepareLogFile(SendAction action) {
    if (Logan.sDebug) {
      Log.d(TAG, "prepare log file");
    }
    if (isFile(action.date)) { // 是否有日期文件
      String src = mPath + File.separator + action.date;
      if (action.date.equals(String.valueOf(Util.ts()))) {
        doFlushLog2File();
        String des = mPath + File.separator + action.date + ".copy";
        if (copyFile(src, des)) {
          action.uploadPath = des;
          return true;
        }
      } else {
        action.uploadPath = src;
        return true;
      }
    }
    action.uploadPath = "";
    return false;
  }

  private boolean isFile(String name) {
    boolean isExist = false;
    if (TextUtils.isEmpty(mPath)) {
      return false;
    }
    File file = new File(mPath + File.separator + name);
    if (file.exists() && file.isFile()) {
      isExist = true;
    }
    return isExist;
  }

  private boolean copyFile(String src, String des) {
    boolean back = false;
    FileInputStream inputStream = null;
    FileOutputStream outputStream = null;
    try {
      inputStream = new FileInputStream(src);
      outputStream = new FileOutputStream(des);
      byte[] buffer = new byte[ONE_KB];
      int i;
      while ((i = inputStream.read(buffer)) >= 0) {
        outputStream.write(buffer, 0, i);
        outputStream.flush();
      }
      back = true;
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (inputStream != null) {
          inputStream.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        if (outputStream != null) {
          outputStream.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return back;
  }
}
