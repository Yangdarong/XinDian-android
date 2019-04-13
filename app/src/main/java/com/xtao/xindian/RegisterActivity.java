package com.xtao.xindian;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.common.UserResultType;
import com.xtao.xindian.utils.UtilHelpers;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private TextView tvTitleName;
    private ImageView ivTitlePic;

    private EditText etUserLoginIdNew;
    private EditText etUserPasswordNew;
    private EditText etUserPasswordTwice;
    private LinearLayout llBtnRegister;

    private boolean isRegister = false;

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
            // 执行注册
            if (isRegister) {   // 可以注册
                // 访问 SSM 后台 需要开启子线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://172.24.95.94:8080/xindian/user/addUser.json");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                            String uLoginId = etUserLoginIdNew.getText().toString();
                            String uPassword = etUserPasswordNew.getText().toString();

                            connection.setRequestMethod("POST");
                            connection.setDoInput(true);
                            connection.setDoOutput(true);
                            connection.setUseCaches(false);
                            connection.connect();

                            String body = "uLoginId=" + uLoginId + "&uPassword=" + uPassword;
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                                    connection.getOutputStream(), "UTF-8"));
                            writer.write(body);
                            writer.close();

                            int responseCode = connection.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {    // 200
                                InputStream inputStream = connection.getInputStream();
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                byte[] data = new byte[1024];
                                int len = 0;
                                while ((len = inputStream.read(data)) != -1) {
                                    outStream.write(data, 0, len);
                                }
                                inputStream.close();
                                //Log.d("tag", new String(outStream.toByteArray()));

                                String jsonCode = new String(outStream.toByteArray());
                                Gson gson = new Gson();

                                UserResultType userResultType = gson.fromJson(jsonCode, UserResultType.class);
                                if (userResultType.getState() == 1) {   // 登录成功
                                    Looper.prepare();
                                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                } else {    // 账号密码不正确 或没有该用户
                                    Looper.prepare();
                                    Toast.makeText(RegisterActivity.this, "该账号已经注册", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }

                            }

                        } catch (Exception e) {
                            Looper.prepare();
                            Toast.makeText(RegisterActivity.this, "无法链接到服务器,请重试", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                Toast.makeText(RegisterActivity.this, "请检查输入的账号和密码", Toast.LENGTH_LONG).show();
            }
        }
    };

    // 检测是否含有非法字符
    TextWatcher textCheckWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (str.length() > 0) {
                String subStr = str.substring(str.length() - 1, str.length());
                Pattern pattern = Pattern.compile("^[a-z\\d]+$");
                if (!pattern.matcher(subStr).matches()) {    // 不是小写数字和下划线组合
                    //Toast.makeText(RegisterActivity.this, subStr, Toast.LENGTH_LONG).show();
                    Toast.makeText(RegisterActivity.this, "只能输入小写英文字母和数字", Toast.LENGTH_LONG).show();
                    s.delete(s.length() -1, s.length());
                }
            }
        }
    };


    TextWatcher textSameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //Toast.makeText(RegisterActivity.this, etUserPasswordTwice.getText().toString(), Toast.LENGTH_LONG).show();    内容实时更改
            String pwdOne = etUserPasswordNew.getText().toString();
            String pwdTwo = etUserPasswordTwice.getText().toString();

            if(!pwdOne.equals(pwdTwo)) {    // 两者不相同
                etUserPasswordNew.setBackgroundResource(R.drawable.pwd_edittext_green);
                etUserPasswordTwice.setBackgroundResource(R.drawable.pwd_edittext_red);
            } else {    // 两次密码输入相同
                etUserPasswordNew.setBackgroundResource(R.drawable.pwd_edittext_green);
                etUserPasswordTwice.setBackgroundResource(R.drawable.pwd_edittext_green);

                etUserPasswordNew.setBackground(null);
                etUserPasswordTwice.setBackground(null);

                isRegister = true;
            }
        }
    };

    private void initListener() {
        llBtnRegister.setOnClickListener(mRegisterListener);

        etUserLoginIdNew.addTextChangedListener(textCheckWatcher);
        etUserPasswordNew.addTextChangedListener(textCheckWatcher);
        etUserPasswordTwice.addTextChangedListener(textSameWatcher);

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
