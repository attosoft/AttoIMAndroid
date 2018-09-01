/*
 * Copyright (C) 2017  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X (MobileIMSDK v3.x) Project.
 * All rights reserved.
 *
 * > Github地址: https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址: http://www.52im.net/forum-89-1.html
 * > 即时通讯技术社区：http://www.52im.net/
 * > 即时通讯技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 *
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 *
 * ClientCoreSDK.java at 2017-5-1 21:06:41, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package cn.id0755.sdk.android;

import cn.id0755.sdk.android.core.KeepAliveDaemon;
import cn.id0755.sdk.android.core.AutoReLoginDaemon;
import cn.id0755.sdk.android.core.LocalUDPDataReciever;
import cn.id0755.sdk.android.core.LocalUDPSocketProvider;
import cn.id0755.sdk.android.core.QoS4ReceiveDaemon;
import cn.id0755.sdk.android.core.QoS4SendDaemon;
import cn.id0755.sdk.android.event.ChatBaseEvent;
import cn.id0755.sdk.android.event.ChatTransDataEvent;
import cn.id0755.sdk.android.event.MessageQoSEvent;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ClientCoreSDK {
    private final static String TAG = ClientCoreSDK.class.getSimpleName();

    public static boolean DEBUG = true;
    public static boolean autoReLogin = true;

    private static ClientCoreSDK instance = null;

    private boolean _init = false;
    private boolean localDeviceNetworkOk = true;
    private boolean connectedToServer = true;
    private boolean loginHasInit = false;
    private String currentLoginUserId = null;
    private String currentLoginToken = null;
    private String currentLoginExtra = null;

    private ChatBaseEvent chatBaseEvent = null;
    private ChatTransDataEvent chatTransDataEvent = null;
    private MessageQoSEvent messageQoSEvent = null;

    private Context context = null;

    public static ClientCoreSDK getInstance() {
        if (instance == null)
            instance = new ClientCoreSDK();
        return instance;
    }

    private ClientCoreSDK() {
    }

    public void init(Context _context) {
        if (!_init) {
            if (_context == null)
                throw new IllegalArgumentException("context can't be null!");

            if (_context instanceof Application)
                this.context = _context;
            else {
                this.context = _context.getApplicationContext();
            }

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            this.context.registerReceiver(networkConnectionStatusBroadcastReceiver, intentFilter);

            _init = true;
        }
    }

    public void release() {
        LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();
        AutoReLoginDaemon.getInstance(context).stop(); // 2014-11-08 add by Jack Jiang
        QoS4SendDaemon.getInstance(context).stop();
        KeepAliveDaemon.getInstance(context).stop();
        LocalUDPDataReciever.getInstance(context).stop();
        QoS4ReceiveDaemon.getInstance(context).stop();

        //## Bug FIX: 20180103 by Jack Jiang START
        QoS4SendDaemon.getInstance(context).clear();
        QoS4ReceiveDaemon.getInstance(context).clear();
        //## Bug FIX: 20180103 by Jack Jiang END

        try {
            context.unregisterReceiver(networkConnectionStatusBroadcastReceiver);
        } catch (Exception e) {
            Log.w(TAG, e.getMessage(), e);
        }

        _init = false;

        this.setLoginHasInit(false);
        this.setConnectedToServer(false);
    }

    public String getCurrentLoginUserId() {
        return currentLoginUserId;
    }

    public ClientCoreSDK setCurrentLoginUserId(String currentLoginUserId) {
        this.currentLoginUserId = currentLoginUserId;
        return this;
    }

    public String getCurrentLoginToken() {
        return currentLoginToken;
    }

    public void setCurrentLoginToken(String currentLoginToken) {
        this.currentLoginToken = currentLoginToken;
    }

    public String getCurrentLoginExtra() {
        return currentLoginExtra;
    }

    public ClientCoreSDK setCurrentLoginExtra(String currentLoginExtra) {
        this.currentLoginExtra = currentLoginExtra;
        return this;
    }

    public boolean isLoginHasInit() {
        return loginHasInit;
    }

    public ClientCoreSDK setLoginHasInit(boolean loginHasInit) {
        this.loginHasInit = loginHasInit;
        return this;
    }

    public boolean isConnectedToServer() {
        return connectedToServer;
    }

    public void setConnectedToServer(boolean connectedToServer) {
        this.connectedToServer = connectedToServer;
    }

    public boolean isInitialed() {
        return this._init;
    }

    public boolean isLocalDeviceNetworkOk() {
        return localDeviceNetworkOk;
    }

    public void setChatBaseEvent(ChatBaseEvent chatBaseEvent) {
        this.chatBaseEvent = chatBaseEvent;
    }

    public ChatBaseEvent getChatBaseEvent() {
        return chatBaseEvent;
    }

    public void setChatTransDataEvent(ChatTransDataEvent chatTransDataEvent) {
        this.chatTransDataEvent = chatTransDataEvent;
    }

    public ChatTransDataEvent getChatTransDataEvent() {
        return chatTransDataEvent;
    }

    public void setMessageQoSEvent(MessageQoSEvent messageQoSEvent) {
        this.messageQoSEvent = messageQoSEvent;
    }

    public MessageQoSEvent getMessageQoSEvent() {
        return messageQoSEvent;
    }

    private final BroadcastReceiver networkConnectionStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo ethernetInfo = connectMgr.getNetworkInfo(9);
            if (!(mobNetInfo != null && mobNetInfo.isConnected())
                    && !(wifiNetInfo != null && wifiNetInfo.isConnected())
                    && !(ethernetInfo != null && ethernetInfo.isConnected())) {
                Log.e(TAG, "【IMCORE】【本地网络通知】检测本地网络连接断开了!");
                localDeviceNetworkOk = false;
                LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();
            } else {
                if (ClientCoreSDK.DEBUG)
                    Log.e(TAG, "【IMCORE】【本地网络通知】检测本地网络已连接上了!");

                localDeviceNetworkOk = true;
                LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();
            }
        }
    };
}
