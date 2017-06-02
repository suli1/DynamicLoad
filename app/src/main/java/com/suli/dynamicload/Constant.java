package com.suli.dynamicload;

import android.content.Context;
import java.io.File;

/**
 * Created by suli690 on 2017/5/25.
 */

public class Constant {

  public final static String CLASS_HELLO = "com.suli.libsdk.HelloWrapper";
  public final static String CLASS_LOACTION = "com.suli.libsdk.LocationWrapper";

  private static String PATH_LIBS;

  public static void init(Context context) {
    PATH_LIBS = context.getFilesDir().getParent() + File.separator + "libs";
  }

  public static String getPathLibs() {
    return PATH_LIBS;
  }
}
