package cn.id0755.im.biz;

public class TextMsgResp {
    public int getErrorCode() {
        return errorCode;
    }

    public TextMsgResp setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public TextMsgResp setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    private int errorCode;
    private String errorMsg;
}
