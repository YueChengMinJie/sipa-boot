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

import android.text.TextUtils;

import com.sipa.boot.sdk.android.SipaAndroid;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class SendLogDefaultRunnable extends SendLogRunnable {
  private static final int DEFAULT_STATUS_CODE = 400;

  private String mUploadLogUrl;

  private Map<String, String> mRequestHeaders;

  private SendLogCallback mSendLogCallback;

  public SendLogDefaultRunnable() {
    mRequestHeaders = new HashMap<>();
  }

  @Override
  public void sendLog(File logFile) {
    boolean success = doSendFileByAction(logFile);
    finish();
    deleteCopyFile(logFile, success);
  }

  private void deleteCopyFile(File logFile, boolean success) {
    if (logFile.getName().contains(".copy")) {
      logFile.delete();
      if (success) {
        emptyFile(logFile);
      }
    }
  }

  private void emptyFile(File logFile) {
    try {
      String absolutePath = logFile.getAbsolutePath();
      int start = absolutePath.lastIndexOf(".");
      File file = new File(absolutePath.substring(0, start));
      FileWriter writer = new FileWriter(file, false);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private HashMap<String, String> getActionHeader() {
    HashMap<String, String> map = new HashMap<>();
    // 二进制上传
    map.put("Content-Type", "binary/octet-stream");
    map.put("client", "android");
    return map;
  }

  /**
   * 主动上报
   */
  private boolean doSendFileByAction(File logFile) {
    boolean isSuccess = false;
    RequestDto dto = null;
    try {
      FileInputStream fileStream = new FileInputStream(logFile);
      dto = doPostRequest(mUploadLogUrl, fileStream, getActionHeader());
      isSuccess = handleSendLogBackData(dto.getData());
    } catch (FileNotFoundException | JSONException e) {
      e.printStackTrace();
    } finally {
      if (mSendLogCallback != null && dto != null) {
        mSendLogCallback.onLogSendCompleted(dto.getStatusCode(), dto.getData());
      } else if (mSendLogCallback != null) {
        mSendLogCallback.onLogSendCompleted(DEFAULT_STATUS_CODE, null);
      }
    }
    return isSuccess;
  }

  private RequestDto doPostRequest(String url, InputStream inputData, Map<String, String> headerMap) {
    int statusCode = DEFAULT_STATUS_CODE;
    byte[] data = null;

    OutputStream outputStream = null;
    InputStream inputStream = null;
    HttpURLConnection c = null;
    ByteArrayOutputStream back;
    byte[] Buffer = new byte[2048];

    try {
      URL u = new URL(url);
      c = (HttpURLConnection) u.openConnection();
      if (c instanceof HttpsURLConnection) {
        ((HttpsURLConnection) c).setHostnameVerifier((hostname, session) -> true);
      }
      Set<Map.Entry<String, String>> entrySet = headerMap.entrySet();
      for (Map.Entry<String, String> tempEntry : entrySet) {
        c.addRequestProperty(tempEntry.getKey(), tempEntry.getValue());
      }
      c.setReadTimeout(15000);
      c.setConnectTimeout(15000);
      c.setDoInput(true);
      c.setDoOutput(true);
      c.setRequestMethod("POST");
      outputStream = c.getOutputStream();
      int i;
      while ((i = inputData.read(Buffer)) != -1) {
        outputStream.write(Buffer, 0, i);
      }
      outputStream.flush();
      statusCode = c.getResponseCode();
      if (statusCode == 200) {
        back = new ByteArrayOutputStream();
        inputStream = c.getInputStream();
        while ((i = inputStream.read(Buffer)) != -1) {
          back.write(Buffer, 0, i);
        }
        data = back.toByteArray();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (inputData != null) {
        try {
          inputData.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (c != null) {
        c.disconnect();
      }
    }

    RequestDto requestDto = new RequestDto();
    requestDto.setStatusCode(statusCode);
    requestDto.setData(data);
    return requestDto;
  }

  /**
   * 处理上传日志接口返回的数据
   */
  private boolean handleSendLogBackData(byte[] backData) throws JSONException {
    boolean isSuccess = false;
    if (backData != null) {
      String data = new String(backData);
      if (!TextUtils.isEmpty(data)) {
        isSuccess = SipaAndroid.SUCCESS_CODE.equals(new JSONObject(data).getString(SipaAndroid.STATUS_KEY));
      }
    }
    return isSuccess;
  }

  public String getUploadLogUrl() {
    return mUploadLogUrl;
  }

  public void setUploadLogUrl(String mUploadLogUrl) {
    this.mUploadLogUrl = mUploadLogUrl;
  }

  public Map<String, String> getRequestHeaders() {
    return mRequestHeaders;
  }

  public void setRequestHeaders(Map<String, String> mRequestHeaders) {
    this.mRequestHeaders = mRequestHeaders;
  }

  public SendLogCallback getSendLogCallback() {
    return mSendLogCallback;
  }

  public void setSendLogCallback(SendLogCallback mSendLogCallback) {
    this.mSendLogCallback = mSendLogCallback;
  }
}
