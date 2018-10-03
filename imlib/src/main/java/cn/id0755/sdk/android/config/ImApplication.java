package cn.id0755.sdk.android.config;

import android.app.Application;

import cn.id0755.sdk.android.manager.MessageServiceManager;
import cn.id0755.sdk.android.service.push.PushMsgFilterStub;

public class ImApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        CrashCatchHandler.getInstance().init(this);
        MessageServiceManager.getInstance();
        MessageServiceManager.getInstance().registerPushFilter(new PushMsgFilterStub());
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
    public static Application getInstance(){
        ImApplication mInstance = ImApplication.mInstance;
        return mInstance;
    }
}
