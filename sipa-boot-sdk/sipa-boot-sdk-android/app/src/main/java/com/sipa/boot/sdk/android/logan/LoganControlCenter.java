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

import android.os.Looper;
import android.text.TextUtils;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

class LoganControlCenter {
  private final ConcurrentLinkedQueue<LoganModel> mCacheLogQueue = new ConcurrentLinkedQueue<>();
  private final long mMaxQueue; // 最大队列数

  private final String mCachePath; // 缓存文件路径
  private final String mPath; // 文件路径
  private final long mSaveTime; // 存储时间
  private final long mMaxFile; // 最大文件大小
  private final long mMinCapacity; // 最小存储
  private final String mEncryptKey16;
  private final String mEncryptIv16;

  private static volatile LoganControlCenter sLoganControlCenter;
  private LoganThread mLoganThread;

  private LoganControlCenter(LoganConfig config) {
    if (!config.isValid()) {
      throw new NullPointerException("config's param is invalid");
    }

    mCachePath = config.mCachePath;
    mPath = config.mPath;
    mSaveTime = config.mDay;
    mMaxFile = config.mFileSize;
    mMinCapacity = config.mCapacitySize;
    mMaxQueue = config.mQueueSize;
    mEncryptKey16 = new String(config.mEncryptKey16);
    mEncryptIv16 = new String(config.mEncryptIv16);

    init();
  }

  private void init() {
    if (mLoganThread == null) {
      mLoganThread = new LoganThread(mCacheLogQueue, mCachePath, mPath, mSaveTime,
        mMaxFile, mMinCapacity, mEncryptKey16, mEncryptIv16);
      mLoganThread.setName("logan-thread");
      mLoganThread.start();
    }
  }

  static LoganControlCenter instance(LoganConfig config) {
    if (sLoganControlCenter == null) {
      synchronized (LoganControlCenter.class) {
        if (sLoganControlCenter == null) {
          sLoganControlCenter = new LoganControlCenter(config);
        }
      }
    }
    return sLoganControlCenter;
  }

  void write(String log, int flag) {
    if (TextUtils.isEmpty(log)) {
      return;
    }

    LoganModel model = new LoganModel();
    model.action = LoganModel.Action.WRITE;

    WriteAction action = new WriteAction();
    action.log = log;
    action.flag = flag;
    action.localTime = System.currentTimeMillis();
    action.isMainThread = Looper.getMainLooper() == Looper.myLooper();
    action.threadId = Thread.currentThread().getId();
    action.threadName = Thread.currentThread().getName();

    model.writeAction = action;
    if (mCacheLogQueue.size() < mMaxQueue) {
      mCacheLogQueue.add(model);
      if (mLoganThread != null) {
        mLoganThread.notifyRun();
      }
    }
  }

  void send(String[] dates, SendLogRunnable runnable) {
    if (TextUtils.isEmpty(mPath) || dates == null) {
      return;
    }

    for (String date : dates) {
      if (TextUtils.isEmpty(date)) {
        continue;
      }

      long time = Util.d2ts(date);
      if (time > 0) {
        LoganModel model = new LoganModel();
        model.action = LoganModel.Action.SEND;

        SendAction action = new SendAction();
        action.date = String.valueOf(time);
        action.sendLogRunnable = runnable;

        model.sendAction = action;
        mCacheLogQueue.add(model);
        if (mLoganThread != null) {
          mLoganThread.notifyRun();
        }
      }
    }
  }

  void flush() {
    if (TextUtils.isEmpty(mPath)) {
      return;
    }

    LoganModel model = new LoganModel();
    model.action = LoganModel.Action.FLUSH;

    mCacheLogQueue.add(model);
    if (mLoganThread != null) {
      mLoganThread.notifyRun();
    }
  }

  File getDir() {
    return new File(mPath);
  }
}
