// TaskWrapper.aidl
package cn.id0755.im;

// Declare any non-default types here with import statements

interface ITaskWrapper {
    Bundle getProperties(); // called locally

    byte[] req2buf();

    int buf2resp(in byte[] buf);

    void onTaskEnd(in int errType, in int errCode);

}
