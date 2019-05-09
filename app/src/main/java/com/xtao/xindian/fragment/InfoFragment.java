package com.xtao.xindian.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtao.xindian.LoginActivity;
import com.xtao.xindian.R;
import com.xtao.xindian.activities.UserInfoActivity;
import com.xtao.xindian.common.task.BitmapTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.pojo.TbUser;
import com.xtao.xindian.utils.UserUtils;
import com.xtao.xindian.view.CircleImageView;

import java.util.Objects;

public class InfoFragment extends Fragment {
    private TextView tvTitleName;
    private TextView tvUserName;

    private CircleImageView picUserIcon;

    private LinearLayout llUserLogin;

    private Intent intent;
    private Bundle bundle;
    private TbUser user;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_info, container, false);

        intent = Objects.requireNonNull(getActivity()).getIntent();
        bundle = intent.getExtras();
        // 获取到用户信息
        if (bundle != null) {
            user = (TbUser) bundle.get("userInformation");
        } else {
            user = UserUtils.readLoginInfo(getActivity());
        }

        initView(mView);
        initData();
        initListener();
        // Inflate the layout for this fragment
        return mView;
    }

    private void initListener() {
        llUserLogin.setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (bundle == null) {
                // 进入注册界面
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

            } else {
                // 点击进入个人信息编辑
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        }
    };

    private void initData() {
        if (user != null) {
            tvUserName.setText(getResources().getString(R.string.welcome_user, user.getuSignature()));

        }

        new BitmapTask().execute(picUserIcon, user.getuHeadPortrait());
    }

    private void initView(View view) {
        tvTitleName = view.findViewById(R.id.tv_title_name);
        llUserLogin = view.findViewById(R.id.ll_user_login);
        tvTitleName.setText("我的");
        picUserIcon = view.findViewById(R.id.pic_user_icon);

        tvUserName = view.findViewById(R.id.tv_user_name);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("正在加载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  // 圆形旋转
    }

}
