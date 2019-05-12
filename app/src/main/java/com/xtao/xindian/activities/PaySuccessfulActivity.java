package com.xtao.xindian.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xtao.xindian.MainActivity;
import com.xtao.xindian.R;

public class PaySuccessfulActivity extends AppCompatActivity {

    private TextView tvPaySuccessfulHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_successful);
        tvPaySuccessfulHome = findViewById(R.id.tv_pay_successful_home);

        tvPaySuccessfulHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaySuccessfulActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
