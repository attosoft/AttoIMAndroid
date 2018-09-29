// MessageService.aidl
package cn.id0755.im;
import cn.id0755.im.ITaskWrapper;
import cn.id0755.im.IPushMessageFilter;
// Declare any non-default types here with import statements

interface IMessageService {
    int send(ITaskWrapper taskWrapper, in Bundle taskProperties);

    void cancel(int taskID);

    void registerPushMessageFilter(IPushMessageFilter filter);

    void unregisterPushMessageFilter(IPushMessageFilter filter);

    void setAccountInfo(in long uin, in String userName);

    void setForeground(in int isForeground);
}
