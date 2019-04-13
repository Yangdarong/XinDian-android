package com.xtao.xindian.fragment;

import android.content.Intent;
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
import com.xtao.xindian.pojo.TbUser;

public class InfoFragment extends Fragment {
    private TextView tvTitleName;
    private TextView tvUserName;

    private LinearLayout llUserLogin;

    private Intent intent;
    private Bundle bundle;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_info, container, false);


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
                getActivity().finish();
            } else {
                // 点击进入个人信息编辑
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
                getActivity().finish();
            }
        }
    };

    private void initData() {

        intent = getActivity().getIntent();
        bundle = intent.getExtras();
        // 获取到用户信息
        if (bundle != null) {
            TbUser user = (TbUser) bundle.get("userInformation");

            if (user.getuSignature() != null) { // 有设置用户名
                tvUserName.setText(getResources().getString(R.string.welcome_user, user.getuSignature()));
            } else {    // 未设置用户名
                tvUserName.setText(getResources().getString(R.string.welcome_user, "默认用户名"));
            }

        }
    }

    private void initView(View view) {
        tvTitleName = view.findViewById(R.id.tv_title_name);
        llUserLogin = view.findViewById(R.id.ll_user_login);
        tvTitleName.setText("我的");

        tvUserName = view.findViewById(R.id.tv_user_name);

    }
}
