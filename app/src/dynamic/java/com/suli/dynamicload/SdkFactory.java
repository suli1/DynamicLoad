package com.suli.dynamicload;

import android.content.Context;
import android.os.Environment;
import com.suli.libbase.ILocation;
import com.suli.libbase.ISayHi;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * Created by suli690 on 2017/5/25.
 */

public class SdkFactory {

  private final static String FILE_JAR_SDK = "sdk_dex.jar";
  private final static String FILE_JAR_BADIDU_LBS = "BaiduLBS_Android_dex.jar";

  public static boolean init() {
    return downloadLibs();
  }

  public static ISayHi createHello(Context context) {
    String libsPath = Constant.getPathLibs();
    File optDex = new File(libsPath + File.separator + FILE_JAR_SDK);
    if (!optDex.exists()) {
      throw new RuntimeException(FILE_JAR_SDK + " not found, init sdk first");
    }

    try {
      // load jar
      DexClassLoader dexClassLoader =
          new DexClassLoader(optDex.getAbsolutePath(), libsPath, libsPath,
              context.getClassLoader());

      Class libProviderClazz = dexClassLoader.loadClass(Constant.CLASS_HELLO);
      return (ISayHi) libProviderClazz.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static ILocation createLocation(Context context) {
    String libsPath = Constant.getPathLibs();

    File optDexBaiduLBS = new File(libsPath + File.separator + FILE_JAR_BADIDU_LBS);
    if (!optDexBaiduLBS.exists()) {
      throw new RuntimeException(FILE_JAR_BADIDU_LBS + " not found, init sdk first");
    }

    File optDexSdk = new File(libsPath + File.separator + FILE_JAR_SDK);
    if (!optDexSdk.exists()) {
      throw new RuntimeException(FILE_JAR_SDK + " not found, init sdk first");
    }

    try {
      // load baidu LBS jar
      DexClassLoader baiduLBSDexClassLoader =
          new DexClassLoader(optDexBaiduLBS.getAbsolutePath(), libsPath, libsPath,
              context.getClassLoader());

      // load sdk jar
      DexClassLoader dexClassLoader =
          new DexClassLoader(optDexSdk.getAbsolutePath(), libsPath, libsPath,
              baiduLBSDexClassLoader);

      Class libProviderClazz = dexClassLoader.loadClass(Constant.CLASS_LOACTION);
      Constructor constructor = libProviderClazz.getConstructor(Context.class);
      return (ILocation) constructor.newInstance(context);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean downloadLibs() {
    boolean result = false;
    do {
      String libsPath = Constant.getPathLibs();
      try {
        File libsPathFile = new File(libsPath);
        if (!libsPathFile.exists()) {
          if (!libsPathFile.mkdirs()) {
            L.e("mkdirs failed:" + libsPath);
            break;
          }
        }

        // copy jar, so
        String srcPath = Environment.getExternalStorageDirectory().getPath()
            + File.separator
            + "dynamic-libs"
            + File.separator;
        File downloadLibs = new File(srcPath);
        if (downloadLibs.exists() && downloadLibs.isDirectory()) {
          File[] files = downloadLibs.listFiles();
          if (files != null) {
            for (File file : files) {
              String fileName = file.getName();
              if (fileName.endsWith(".jar") || fileName.endsWith(".so")) {
                File destFile = new File(libsPath + File.separator + fileName);
                FileUtil.copyFile(file, destFile);
              }
            }
            result = true;
          } else {
            L.e("Not found jar or so!");
          }
        } else {
          L.e("Not found jar or so!");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } while (false);

    return result;
  }
}
