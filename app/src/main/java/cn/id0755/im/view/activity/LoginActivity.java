package cn.id0755.im.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;


import cn.id0755.im.chat.proto.Login;
import cn.id0755.im.data.source.sp.AccountSp;
import cn.id0755.sdk.android.task.ITaskListener;
import cn.id0755.im.task.LoginTask;
import cn.id0755.sdk.android.manager.MsgServiceManager;

import java.util.concurrent.Executors;

import cn.id0755.im.R;
import cn.id0755.sdk.android.store.ConfigSp;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "LoginActivity";
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private AutoCompleteTextView mPhoneView;
    private EditText mPasswordView;

    private CheckBox mAutoLogin;
    private CheckBox mRememberPassword;
    private Button mEmailSignInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        initView();

        mPhoneView.setText(AccountSp.getInstance().getAccount());
        mPasswordView.setText("123456");
    }

    private void initView() {
        mPhoneView = findViewById(R.id.phone);
        mPasswordView = findViewById(R.id.password);
        mAutoLogin = findViewById(R.id.auto_login);
        mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mRememberPassword = findViewById(R.id.remember_password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {

                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        AccountSp.getInstance().setAccount(mPhoneView.getText().toString());
                        MsgServiceManager.getInstance().send(
                                new LoginTask(mPhoneView.getText().toString(), "123456")
                                        .setListener(new ITaskListener<Login.LoginResponse>() {
                                            @Override
                                            public void onResp(Login.LoginResponse resp) {
                                                if (resp.getAccessToken() != null) {
                                                    Intent intent = new Intent();
                                                    intent.setClass(LoginActivity.this, MainActivity.class);
                                                    startActivity(intent);
//                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onTaskEnd(int errType, int errCode) {

                                            }
                                        }));
                    }
                });
            }
        });

        mAutoLogin.setChecked(ConfigSp.getConfigSp().getAutoLogin());
        mAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigSp.getConfigSp().setAutoLogin(isChecked);
            }
        });

        mRememberPassword.setChecked(ConfigSp.getConfigSp().getRememberPsw());
        mRememberPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigSp.getConfigSp().setRememberPsw(isChecked);
            }
        });
    }

}

