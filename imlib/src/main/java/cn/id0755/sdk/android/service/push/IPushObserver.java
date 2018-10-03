package cn.id0755.sdk.android.service.push;

import cn.id0755.im.chat.proto.Push;

public interface IPushObserver {
    void onReceive(Push.Message message);
}
