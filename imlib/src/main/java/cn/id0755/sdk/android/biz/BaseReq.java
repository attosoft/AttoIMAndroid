package cn.id0755.sdk.android.biz;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cn.id0755.sdk.android.utils.GsonHelper;

public abstract class BaseReq {
    private transient Class responseCls;

    public <Response> BaseReq setListener(IRequestListener<Response> listener) {
        this.listener = listener;
        if (listener != null) {
            Type[] types = listener.getClass().getGenericInterfaces();
            Type[] params = ((ParameterizedType) types[0]).getActualTypeArguments();
            responseCls = (Class) params[0];
        }
        return this;
    }

    private transient IRequestListener listener = null;

    public abstract int getReqType();

    public void onResponse(String content) {
        if (listener != null) {
            listener.onSuccess(GsonHelper.INSTANCE.fromJson(content, responseCls));
        }
    }

    public void onTaskEnd(int errorType, int errorCode) {

    }
}
