package cn.id0755.im.biz;

import cn.id0755.sdk.android.biz.BaseReq;

public class GetPublishReq extends BaseReq {
    @Override
    public int getReqType() {
        return RequestType.GET_PUBLISH;
    }
}
