package com.xtao.xindian.fragment;

import android.app.Dialog;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xtao.xindian.LoginActivity;
import com.xtao.xindian.R;
import com.xtao.xindian.activities.UserInfoActivity;
import com.xtao.xindian.common.task.BitmapTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.dialog.CommonDialog;
import com.xtao.xindian.fragment.infoDetailPage.ExpressInfoActivity;
import com.xtao.xindian.fragment.infoDetailPage.MyCollectionActivity;
import com.xtao.xindian.fragment.infoDetailPage.MyDelicateStrategyActivity;
import com.xtao.xindian.fragment.infoDetailPage.MyFollowActivity;
import com.xtao.xindian.fragment.infoDetailPage.MyFoodActivity;
import com.xtao.xindian.fragment.infoDetailPage.RecentlyBrowseActivity;
import com.xtao.xindian.pojo.TbUser;
import com.xtao.xindian.utils.UserUtils;
import com.xtao.xindian.view.CircleImageView;

import java.util.Objects;

public class InfoFragment extends Fragment {
    private TextView tvTitleName;
    private TextView tvUserName;

    private CircleImageView picUserIcon;

    private LinearLayout llUserLogin;

    // 开始我的信息界面管理
    private RadioButton rbWaitConfirm;
    private RadioButton rbWaitDeliver;
    private RadioButton rbWaitReceive;

    private LinearLayout llUserInfoRecentlyBrowse;
    private LinearLayout llUserInfoMyFollow;
    private LinearLayout llUserInfoMyFood;
    private LinearLayout llUserInfoCollection;
    private LinearLayout llUserInfoDelicateStrategy;

    private TextView tvUserInfoShare;

    private Intent intent;
    private Bundle bundle;
    private TbUser user;

    private ProgressDialog progressDialog;
    private CommonDialog loginDialog;

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

    private boolean isUserLogined() {
        return user.getuId() != 0;
    }

    private void initListener() {
        llUserLogin.setOnClickListener(mOnClickListener);

        final int uId = user.getuId();

        rbWaitConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserLogined()) {
                    Intent intent = new Intent(getContext(), ExpressInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("uId", uId);
                    bundle.putInt("page", 0);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    loginDialog.show();
                }

            }
        });

        rbWaitDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserLogined()) {
                    Intent intent = new Intent(getContext(), ExpressInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("uId", uId);
                    bundle.putInt("page", 1);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    loginDialog.show();
                }
            }
        });

        rbWaitReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserLogined()) {
                    Intent intent = new Intent(getContext(), ExpressInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("uId", uId);
                    bundle.putInt("page", 2);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    loginDialog.show();
                }
            }
        });

        llUserInfoRecentlyBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到最近浏览界面
                if (isUserLogined()) {
                    Intent intent = new Intent(getContext(), RecentlyBrowseActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("uId", uId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    loginDialog.show();
                }
            }
        });

        llUserInfoMyFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到我的关注界面
                if (isUserLogined()) {
                    Intent intent = new Intent(getContext(), MyFollowActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("uId", uId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    loginDialog.show();
                }
            }
        });

        llUserInfoMyFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到我的用餐记录界面
                if (isUserLogined()) {
                    Intent intent = new Intent(getContext(), MyFoodActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("uId", uId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    loginDialog.show();
                }
            }
        });

        llUserInfoCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到我的收藏界面
                if (isUserLogined()) {
                    Intent intent = new Intent(getContext(), MyCollectionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("uId", uId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    loginDialog.show();
                }
            }
        });

        llUserInfoDelicateStrategy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到美食攻略管理界面
                if (isUserLogined()) {
                    Intent intent = new Intent(getContext(), MyDelicateStrategyActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("uId", uId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    loginDialog.show();
                }
            }
        });

        tvUserInfoShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommonDialog(getContext(), R.style.DialogTheme, "感谢您的支持,祝您生活愉快", new CommonDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        dialog.dismiss();
                    }
                }).setTitle("提示").show();
            }
        });
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

        rbWaitConfirm = view.findViewById(R.id.rb_wait_confirm);
        rbWaitDeliver = view.findViewById(R.id.rb_wait_deliver);
        rbWaitReceive = view.findViewById(R.id.rb_wait_receive);

        llUserInfoRecentlyBrowse = view.findViewById(R.id.ll_user_info_recently_browse);
        llUserInfoMyFollow = view.findViewById(R.id.ll_user_info_my_follow);
        llUserInfoMyFood = view.findViewById(R.id.ll_user_info_my_food);
        llUserInfoCollection = view.findViewById(R.id.ll_user_info_collection);
        llUserInfoDelicateStrategy = view.findViewById(R.id.ll_user_info_delicate_strategy);
        tvUserInfoShare = view.findViewById(R.id.tv_user_info_share);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("正在加载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  // 圆形旋转

        loginDialog = new CommonDialog(getActivity(), R.style.DialogTheme, "您还未登录,是否确定并进行登录?", new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        }).setTitle("提示");
    }

}
