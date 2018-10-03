package cn.id0755.sdk.android.task;

public interface ITaskListener<T> {
    void onResp(T resp);

    void onTaskEnd(int errType, int errCode);
}
