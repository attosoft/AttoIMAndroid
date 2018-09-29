package cn.id0755.sdk.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cn.id0755.im.IMessageService;

/**
 * 独立进程处理消息发送到服务端
 */
public class MsgRemoteService extends Service{

    private IMessageService.Stub mStub = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mStub = new MsgRemoteServiceStub(getApplicationContext());
    }
}
