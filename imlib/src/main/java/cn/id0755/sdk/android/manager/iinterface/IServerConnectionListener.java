package cn.id0755.sdk.android.manager.iinterface;

import cn.id0755.sdk.android.manager.ConnectState;

public interface IServerConnectionListener {
    /**
     * 初次注册回调连接状态
     * @param type
     */
    void onConnectState(ConnectState type);

    /**
     * 连接状态改变时回调
     * @param type
     */
    void onConnectStateChange(ConnectState type);
}
