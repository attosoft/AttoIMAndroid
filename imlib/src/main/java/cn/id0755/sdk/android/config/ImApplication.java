package cn.id0755.sdk.android.config;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;

import java.util.List;

import cn.id0755.sdk.android.manager.MsgServiceManager;
import cn.id0755.sdk.android.service.push.PushMsgFilterStub;

public class ImApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        String processName = getProcessName(Process.myPid());
        if (getPackageName().equals(processName)) {
            // init
            CrashCatchHandler.getInstance().init(this);
            MsgServiceManager.getInstance().bindPushMessageFilter();
        }
    }

    /**
     * 根据进程 ID 获取进程名
     * @param pid
     * @return
     */
    public  String getProcessName(int pid){
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfoList = am.getRunningAppProcesses();
        if (processInfoList == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfoList) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    private static ImApplication mInstance = null;

    public static Application getInstance() {
        ImApplication mInstance = ImApplication.mInstance;
        return mInstance;
    }
}
