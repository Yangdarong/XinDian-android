package com.xtao.xindian;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *  用户登录的界面
 */
public class LoginActivity extends AppCompatActivity {

    private TextView tvTitleName;
    private ImageView iv_title_pic;

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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();

                UtilHelpers.hidKeyboard(ev, view, LoginActivity.this);   // 调用方法
                break;

            default:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    private void initListener() {

        llBtnLogin.setOnClickListener(mOnClickListener);

        iv_title_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String uLoginId = etUserLoginId.getText().toString();
            final String uPassword = etUserPassword.getText().toString();

            if (uLoginId.equals("") || uPassword.equals("")) {   // 用户名和密码为空
                Toast.makeText(getApplicationContext(), "请输入用户名或密码", Toast.LENGTH_LONG).show();
            } else {
                // 访问 SSM 后台 需要开启子线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://172.24.95.94:8080/xindian/user/queryUser.json");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

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
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();


                                    // 将用户实体带到主界面
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("userInformation", userResultType.getUser());
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtras(bundle);

                                    startActivity(intent);
                                    finish();
                                    Looper.loop();
                                } else {    // 账号密码不正确 或没有该用户
                                    Looper.prepare();
                                    Toast.makeText(LoginActivity.this, "请检查账号和密码", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }

                            } else {    // 网络错误
                                Looper.prepare();
                                Toast.makeText(LoginActivity.this, "网络错误,请重试", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    };



    private void initData() {
        tvTitleName.setText("登录");

    }

    private void initView() {
        etUserLoginId = findViewById(R.id.et_user_loginId);
        etUserPassword = findViewById(R.id.et_user_password);
        llBtnLogin = findViewById(R.id.ll_btn_login);

        tvTitleName = findViewById(R.id.tv_title_name);
        iv_title_pic = findViewById(R.id.iv_title_pic);

    }


}
