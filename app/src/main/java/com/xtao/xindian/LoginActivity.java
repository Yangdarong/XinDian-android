package com.xtao.xindian;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 *  用户登录的界面
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etUserLoginId;
    private EditText etUserPassword;
    private LinearLayout llBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initData();
        initListener();
    }

    private void initListener() {
        llBtnLogin.setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String uLoginId = etUserLoginId.getText().toString();
            String uPassword = etUserPassword.getText().toString();

            if (uLoginId.equals("") || uPassword.equals("")) {   // 用户名和密码为空
                Toast.makeText(getApplicationContext(), "请输入用户名或密码", Toast.LENGTH_LONG).show();
            } else {
                // 访问 SSM 后台

            }
        }
    };

    private void initData() {
    }

    private void initView() {
        etUserLoginId = findViewById(R.id.et_user_loginId);
        etUserPassword = findViewById(R.id.et_user_password);
        llBtnLogin = findViewById(R.id.ll_btn_login);

    }


}
