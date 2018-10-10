package cn.id0755.sdk.android.manager.iinterface;

import cn.id0755.im.ITaskWrapper;
import cn.id0755.sdk.android.biz.BaseReq;
import cn.id0755.sdk.android.service.push.IPushObserver;

/**
 * Created by andy on 2018/10/10.
 */
public interface IMsgService {
    void bindPushMessageFilter();
    void cancel(final ITaskWrapper taskWrapper);
    void registerPushObserver(IPushObserver observer);
    void unRegisterPushObserver(IPushObserver observer);
    boolean send(ITaskWrapper taskWrapper);
    boolean send(BaseReq req);
}
