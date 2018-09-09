package cn.id0755.im.entity;

public class AccountInfo {

    /**
     * 账号token
     */
    private String mToken;
    /**
     * 昵称
     */
    private String mNickName;

    public String getToken() {
        return mToken;
    }

    public AccountInfo setToken(String mToken) {
        this.mToken = mToken;
        return this;
    }

    public String getNickName() {
        return mNickName;
    }

    public AccountInfo setNickName(String mNickName) {
        this.mNickName = mNickName;
        return this;
    }

}
