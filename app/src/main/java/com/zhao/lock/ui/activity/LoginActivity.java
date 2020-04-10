package com.zhao.lock.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zhao.lock.R;
import com.zhao.lock.base.BaseActivity;
import com.zhao.lock.bean.LoginBean;
import com.zhao.lock.util.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rxhttp.wrapper.param.RxHttp;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.phoneNumber_et)
    EditText phoneNumberEt;
    @BindView(R.id.password_et)
    EditText passwordEt;
    @BindView(R.id.rememberPassword_cb)
    CheckBox rememberPasswordCb;
    @BindView(R.id.rememberPassword_ly)
    LinearLayout rememberPasswordLy;
    @BindView(R.id.login_btn)
    Button loginBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
    }

    @OnClick({R.id.rememberPassword_ly, R.id.login_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rememberPassword_ly:
                if (rememberPasswordCb.isChecked()) {
                    rememberPasswordCb.setChecked(false);
                } else {
                    rememberPasswordCb.setChecked(true);
                }
                break;
            case R.id.login_btn:
                login();
                break;
            default:
                break;
        }
    }

    private void login() {
        String phoneNumber = phoneNumberEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "请输入您的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入您的密码", Toast.LENGTH_SHORT).show();
            return;
        }
        RxHttp.postJson("/app/login")
                .add("username", phoneNumber)
                .add("password", password)
                .asClass(LoginBean.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginBean -> {
                    if (loginBean.getCode() == 200) {
                        SharedPreferencesUtils.getInstance().setToken(loginBean.getData().getToken());
                        SharedPreferencesUtils.getInstance().setUserId(loginBean.getData().getUserInfo().getUserId());
                        SharedPreferencesUtils.getInstance().setUserName(loginBean.getData().getUserInfo().getUsername());

                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, loginBean.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
