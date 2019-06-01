package com.xtao.xindian.fragment.infoDetailPage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtao.xindian.R;

public class MyFoodActivity extends AppCompatActivity {

    private ImageView ivSysBack;

    private TextView tvTitleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_food);

        ivSysBack = findViewById(R.id.iv_sys_back);
        tvTitleName = findViewById(R.id.tv_title_name);

        initView();
        initListener();
    }

    private void initView() {
        tvTitleName.setText("我的用餐");
    }

    private void initListener() {
        ivSysBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
