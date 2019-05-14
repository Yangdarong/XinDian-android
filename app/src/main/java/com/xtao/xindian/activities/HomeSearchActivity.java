package com.xtao.xindian.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.TextView;

import com.xtao.xindian.R;

public class HomeSearchActivity extends AppCompatActivity {

    //tv_home_search_cancel
    private TextView tvHomeSearchCancel;
    // sv_home_search_view
    private SearchView svHomeSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tvHomeSearchCancel = findViewById(R.id.tv_home_search_cancel);
        svHomeSearchView = findViewById(R.id.sv_home_search_view);
    }

    private void initData() {


    }

    private void initListener() {
        tvHomeSearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
