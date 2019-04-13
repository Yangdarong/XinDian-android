package com.xtao.xindian;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtao.xindian.utils.UtilHelpers;

public class RegisterActivity extends AppCompatActivity {

    private TextView tvTitleName;
    private ImageView ivTitlePic;

    private EditText etUserLoginIdNew;
    private EditText etUserPasswordNew;
    private EditText etUserPasswordTwice;
    private LinearLayout llBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        initData();
        initListener();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();

                UtilHelpers.hidKeyboard(ev, view, RegisterActivity.this);   // 调用方法
                break;

                default:
                    break;
        }

        return super.dispatchTouchEvent(ev);
    }

    View.OnClickListener mRegisterListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

        }
    };

    private void initListener() {
        llBtnRegister.setOnClickListener(mRegisterListener);
    }

    private void initData() {
        tvTitleName.setText("注册");
        ivTitlePic.setVisibility(View.INVISIBLE);
    }

    private void initView() {
        tvTitleName = findViewById(R.id.tv_title_name);
        ivTitlePic = findViewById(R.id.iv_title_pic);

        etUserLoginIdNew = findViewById(R.id.et_user_loginId_new);
        etUserPasswordNew = findViewById(R.id.et_user_password_new);
        etUserPasswordTwice = findViewById(R.id.et_user_password_twice);
        llBtnRegister = findViewById(R.id.ll_btn_register);
    }
}
