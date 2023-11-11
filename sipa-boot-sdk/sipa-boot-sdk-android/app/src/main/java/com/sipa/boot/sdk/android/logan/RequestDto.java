package com.sipa.boot.sdk.android.logan;

class RequestDto {
  private int statusCode;

  private byte[] data;

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }
}
