package cn.id0755.im.manager;

import android.app.Application;

public class ImApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        MessageServiceManager.getInstance();
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
