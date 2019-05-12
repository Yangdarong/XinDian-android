package com.xtao.xindian.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xtao.xindian.R;
import com.xtao.xindian.dialog.CommonDialog;
import com.xtao.xindian.pojo.TbUser;
import com.xtao.xindian.utils.UserUtils;

public class OrderConfirmActivity extends AppCompatActivity {

    //et_order_confirm_tel
    private EditText etOrderConfirmTel;
    //iv_order_confirm_address
    private ImageView ivOrderConfirmAddress;
//    tv_order_confirm_bk
    private TextView tvOrderConfirmBk;
//    tv_order_confirm_ok
    private TextView tvOrderConfirmOK;
//    lv_order_confirm_lists
    private ListView lvOrderConfirmLists;

    private TbUser user;

    // 相关组件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        ivOrderConfirmAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到百度定位
                Intent intent = new Intent(OrderConfirmActivity.this, BaiduMapActivity.class);
                startActivity(intent);
            }
        });

        etOrderConfirmTel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) { // 得到焦点时处理的内容

                } else {    // 失去焦点时

                }
            }
        });

        tvOrderConfirmBk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvOrderConfirmOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initData() {
        user = UserUtils.readLoginInfo(getApplicationContext());
        etOrderConfirmTel.setText("" + user.getuPhone());
    }

    private void initView() {
        etOrderConfirmTel = findViewById(R.id.et_order_confirm_tel);
        ivOrderConfirmAddress = findViewById(R.id.iv_order_confirm_address);
        tvOrderConfirmBk = findViewById(R.id.tv_order_confirm_bk);
        tvOrderConfirmOK = findViewById(R.id.tv_order_confirm_ok);
        lvOrderConfirmLists = findViewById(R.id.lv_order_confirm_lists);
    }
}
