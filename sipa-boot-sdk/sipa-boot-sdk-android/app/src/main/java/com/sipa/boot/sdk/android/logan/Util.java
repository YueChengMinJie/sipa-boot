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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
  public static long ts() {
    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    long tempTime = 0;
    try {
      String dataStr = sDateFormat.format(new Date(System.currentTimeMillis()));
      Date parse = sDateFormat.parse(dataStr);
      if (parse != null) {
        tempTime = parse.getTime();
      }
    } catch (Exception ignored) {
    }
    return tempTime;
  }

  public static long d2ts(String time) {
    SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    long tempTime = 0;
    try {
      Date parse = dataFormat.parse(time);
      if (parse != null) {
        tempTime = parse.getTime();
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return tempTime;
  }

  public static String ts2fd(long time) {
    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    return sDateFormat.format(new Date(time));
  }
}
