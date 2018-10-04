package cn.id0755.im.data.source.sp;

import android.content.SharedPreferences;

import cn.id0755.sdk.android.config.ImApplication;

public class AccountSp {
    private final static AccountSp INSTANCE = new AccountSp();

    public static AccountSp getInstance() {
        return INSTANCE;
    }

    private SharedPreferences sharedPreferences;

    private AccountSp() {
        sharedPreferences = ImApplication.getInstance().getSharedPreferences("Account", 0);
    }

    private final static String KEY_ACCOUNT = "account";

    public String getAccount() {
        return sharedPreferences.getString(KEY_ACCOUNT, "10086");
    }

    public void setAccount(String account) {
        sharedPreferences.edit().putString(KEY_ACCOUNT, account).apply();
    }
}
