package com.suli.libbase;

import android.util.Log;

/**
 * Created by suli690 on 2017/5/25.
 */

public class L {

  public final static String TAG = "dynamicload";

  public static void i(String message) {
    Log.i(TAG, message);
  }

  public static void e(String message) {
    Log.e(TAG, message);
  }
}
