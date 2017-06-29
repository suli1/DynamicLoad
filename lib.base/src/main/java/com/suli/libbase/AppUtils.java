package com.suli.libbase;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * App信息的相关信息
 */
public final class AppUtils {

    private AppUtils() {
        throw new RuntimeException("u can't instantiate me ...");
    }

    /**
     * 获取App版本号
     *
     * @return 版本号
     */
    public static int getAppVersionCode() {
        try {
            Context context = Utils.getContext();
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取App版本名
     *
     * @return 版本名
     */
    public static String getAppVersionName() {
        try {
            Context context = Utils.getContext();
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPackageName() {
        return Utils.getContext().getPackageName();
    }

}
