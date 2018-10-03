package cn.id0755.sdk.android.task;

import android.os.Bundle;
import android.os.RemoteException;

import com.google.protobuf.InvalidProtocolBufferException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.UUID;

import cn.id0755.im.ITaskWrapper;
import cn.id0755.im.chat.proto.Common;
import cn.id0755.im.chat.proto.Message;
import cn.id0755.sdk.android.biz.BaseReq;
import cn.id0755.sdk.android.utils.GsonHelper;
import cn.id0755.sdk.android.utils.MessageUtil;

public class CommonTask extends ITaskWrapper.Stub implements ITaskWrapper {

    private BaseReq request;

    public CommonTask(BaseReq request) {
        this.request = request;
    }

    @Override
    public Bundle getProperties() throws RemoteException {
        return null;
    }

    @Override
    public byte[] req2buf() throws RemoteException {
        UUID uuid = UUID.randomUUID();
        Common.CommonReq commonReq = Common.CommonReq
                .newBuilder()
                .setType(request.getReqType())
                .setContent(GsonHelper.INSTANCE.toJson(request))
                .build();
        return MessageUtil.wrap(Message.CMD_ID.COMMON_REQ, uuid.toString(), commonReq).toByteArray();
    }

    @Override
    public int buf2resp(byte[] buf) throws RemoteException {
        Common.CommonResp response = null;
        try {
            response = Common.CommonResp
                    .getDefaultInstance()
                    .getParserForType()
                    .parseFrom(buf, 0, buf.length);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return 0;
        }
        if (request != null && response != null){
            request.onResponse(response.getContent());
        }
        return response != null ? 1 : 0;
    }

    @Override
    public void onTaskEnd(int errType, int errCode) throws RemoteException {
        if (request != null){
            request.onTaskEnd(errType,errCode);
        }
    }
}
