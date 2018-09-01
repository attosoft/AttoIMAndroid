package cn.id0755.sdk.android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class AppUtils {
    private final static String TAG = "AppUtils";

    /**
     * 获取APP版本信息.
     */
    public static String getProgrammVersion(Context context) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//			versionCode = info.versionCode;
//			versionName = info.versionName;
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "读程序版本信息时出错," + e.getMessage(), e);
            return "N/A";
        }
    }
}
