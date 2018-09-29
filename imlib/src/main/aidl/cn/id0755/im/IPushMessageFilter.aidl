// PushMessageFilter.aidl
package cn.id0755.im;

// Declare any non-default types here with import statements

interface IPushMessageFilter {
    boolean onRecv(int cmdId, inout byte[] buffer);
}
