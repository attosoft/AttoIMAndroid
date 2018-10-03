package cn.id0755.sdk.android.manager.iinterface;

import cn.id0755.im.chat.proto.Push;

public interface IPushMessageListener {
    void pushMessage(Push.Message message);
}
