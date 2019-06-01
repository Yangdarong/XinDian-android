package com.xtao.xindian.fragment.infoDetailPage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtao.xindian.R;

public class MyCollectionActivity extends AppCompatActivity {

    private ImageView ivSysBack;

    private TextView tvTitleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);

        ivSysBack = findViewById(R.id.iv_sys_back);
        tvTitleName = findViewById(R.id.tv_title_name);
    }
}
