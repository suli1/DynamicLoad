package com.suli.dynamicload;

import android.content.Context;
import java.io.File;

/**
 * Created by suli690 on 2017/5/25.
 */

public class Constant {

  public final static String CLASS_HELLO = "com.suli.libsdk.HelloWrapper";
  public final static String CLASS_LOACTION = "com.suli.libsdk.LocationWrapper";

  private static String PATH_FILES;
  private static String PATH_LIB;

  public static void init(Context context) {
    PATH_FILES = context.getFilesDir().getAbsolutePath();
    PATH_LIB = context.getFilesDir().getParent() + File.separator + "lib";
  }

  public static String getPathFiles() {
    return PATH_FILES;
  }

  public static String getPathLib() {
    return PATH_LIB;
  }
}
