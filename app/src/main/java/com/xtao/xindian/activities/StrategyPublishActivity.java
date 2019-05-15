package com.xtao.xindian.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xtao.xindian.R;

public class StrategyPublishActivity extends AppCompatActivity {

    // et_strategy_title
    private EditText etStrategyTitle;
    // et_strategy_content
    private EditText etStrategyContent;
    // iv_strategy_add
    private ImageView ivStrategyAdd;
    // lv_strategy_list
    private ListView lvStrategyList;
    // tv_strategy_ok
    private TextView tvStrategyOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy_publish);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        etStrategyTitle = findViewById(R.id.et_strategy_title);
        etStrategyContent = findViewById(R.id.et_strategy_content);
        ivStrategyAdd = findViewById(R.id.iv_strategy_add);
        lvStrategyList = findViewById(R.id.lv_strategy_list);
        tvStrategyOK = findViewById(R.id.tv_strategy_ok);
    }

    private void initData() {

    }

    private void initListener() {
        ivStrategyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(ivStrategyAdd);
            }
        });
    }

    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
            }
        });

        popupMenu.show();
    }
}
