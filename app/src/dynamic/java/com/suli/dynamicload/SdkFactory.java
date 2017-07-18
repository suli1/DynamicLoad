package com.suli.dynamicload;

import android.content.Context;
import android.os.Environment;
import com.suli.dynamicload.hook.AMSHookHelper;
import com.suli.dynamicload.hook.BaseDexClassLoaderHookHelper;
import com.suli.libbase.FileUtil;
import com.suli.libbase.ILocation;
import com.suli.libbase.ISayHi;
import com.suli.libbase.L;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * Created by suli690 on 2017/5/25.
 */

public class SdkFactory {

  private final static String ABI = "x86";
  private final static String FILE_SDK_APK = "sdk.apk";
  private final static String FILE_SDK_ODEX = "sdk.odex";
  //private final static String FILE_JAR_BADIDU_LBS = "BaiduLBS_Android_dex.jar";
  //private final static String FILE_ODEX_BADIDU_LBS = "BaiduLBS_Android_dex.odex";

  public static boolean init(Context context) {
    if (downloadLibs()) {
      try {
        AMSHookHelper.hookActivityManagerNative();
        File apkFile = context.getFileStreamPath(FILE_SDK_APK);
        File odexFile = context.getFileStreamPath(FILE_SDK_ODEX);

        BaseDexClassLoaderHookHelper.patchClassLoader(context.getClassLoader(), apkFile, odexFile,
            Constant.getPathLib() + File.separator + ABI);
        ServiceManager.getInstance().preLoadServices(apkFile);
        return true;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return false;
  }

  public static ISayHi createHello(Context context) {
    String libsPath = Constant.getPathFiles();
    File optDex = new File(libsPath + File.separator + FILE_SDK_APK);
    if (!optDex.exists()) {
      throw new RuntimeException(FILE_SDK_APK + " not found, init sdk first");
    }

    try {
      //// load jar
      //DexClassLoader dexClassLoader =
      //    new DexClassLoader(optDex.getAbsolutePath(), libsPath, libsPath,
      //        context.getClassLoader());

      Class libProviderClazz = context.getClassLoader().loadClass(Constant.CLASS_HELLO);
      return (ISayHi) libProviderClazz.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static ILocation createLocation(Context context) {
    String libsPath = Constant.getPathFiles();
    File optDexSdk = new File(libsPath + File.separator + FILE_SDK_APK);
    if (!optDexSdk.exists()) {
      throw new RuntimeException(FILE_SDK_APK + " not found, init sdk first");
    }

    try {
      //// load baidu LBS jar
      //DexClassLoader baiduLBSDexClassLoader =
      //    new DexClassLoader(optDexBaiduLBS.getAbsolutePath(), libsPath, libsPath,
      //        context.getClassLoader());
      //
      //// load sdk jar
      //DexClassLoader dexClassLoader =
      //    new DexClassLoader(optDexSdk.getAbsolutePath(), libsPath, libsPath,
      //        baiduLBSDexClassLoader);

      Class libProviderClazz = context.getClassLoader().loadClass(Constant.CLASS_LOACTION);
      Constructor constructor = libProviderClazz.getConstructor(Context.class);
      return (ILocation) constructor.newInstance(context);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 下载插件APK
   * copy apk to app files dir
   *
   * @return true: 下载成功， false: 下载失败
   */
  private static boolean downloadLibs() {
    boolean result = false;
    do {
      try {
        // copy apk and so
        String srcPath = Environment.getExternalStorageDirectory().getPath()
            + File.separator
            + "dynamic-res"
            + File.separator;
        File downloadLibs = new File(srcPath);
        if (downloadLibs.exists() && downloadLibs.isDirectory()) {
          File[] files = downloadLibs.listFiles();
          if (files != null) {
            for (File file : files) {
              String fileName = file.getName();
              if (fileName.endsWith(".apk")) {
                File destFile = new File(Constant.getPathFiles() + File.separator + fileName);
                FileUtil.copyFile(file, destFile);

                // unzip  and copy so
                String filePath = file.getAbsolutePath();
                String dirName =
                    filePath.substring(0, filePath.length() - ".apk".length()) + File.separator;
                FileUtil.unZip(file, dirName);
                File soDir = new File(dirName + "lib" + File.separator + ABI);
                if (soDir.exists() && soDir.isDirectory()) {
                  File[] soFiles = soDir.listFiles();
                  if (soFiles != null) {
                    for (File soFile : soFiles) {
                      String soName = soFile.getName();
                      if (soName.endsWith(".so")) {
                        FileUtil.copyFile(soFile, new File(Constant.getPathLib()
                            + File.separator
                            + ABI
                            + File.separator
                            + soName));
                      }
                    }
                  }
                }
              }
            }
            result = true;
          } else {
            L.e("Not found plugin apk!");
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
