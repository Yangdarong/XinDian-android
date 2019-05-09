package com.xtao.xindian;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class LaunchActivity extends AppCompatActivity implements View.OnClickListener {

    private int recLen = 5; // 跳过倒计时
    private Button btnSellerLogin;
    Timer timer = new Timer();

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        initView();
        timer.schedule(task, 1000, 1000);
        /**
         * 正常情况下不点击进入用户首页
         */

        handler = new Handler();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                // 跳转到首页面
                Toast.makeText(getApplicationContext(), "进入用户主页", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000); //延迟5S后发送handler信息
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recLen--;
                    // 设置倒数计时的时间
                    btnSellerLogin.setText("跳过 " + recLen);
                    if (recLen < 0) {
                        timer.cancel();
                        btnSellerLogin.setVisibility(View.GONE);
                    }
                }
            });
        }
    };

    private void initView() {
        btnSellerLogin = findViewById(R.id.btn_seller_login);
        btnSellerLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_seller_login :
                // 跳转到商家
                Toast.makeText(getApplicationContext(), "点击跳过", Toast.LENGTH_SHORT).show();
                finish();
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                break;
            default:
                break;
        }
    }
}
