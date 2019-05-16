package com.xtao.xindian.fragment.starDetailPage;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xtao.xindian.LoginActivity;
import com.xtao.xindian.R;
import com.xtao.xindian.activities.StrategyPublishActivity;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.dialog.CommonDialog;
import com.xtao.xindian.pojo.TbStrategy;
import com.xtao.xindian.pojo.TbUser;
import com.xtao.xindian.utils.UserUtils;
import com.xtao.xindian.view.HorizontalListView;

import java.util.List;
import java.util.Objects;

public class StarDelicateStrategyPageFragment extends Fragment {

    // hlv_delicate_strategy_user_list
    private HorizontalListView hlvDelicateStrategyUserList;
    // tv_delicate_strategy_publish
    private TextView tvDelicateStrategyPublish;
    // lv_delicate_strategy_detail
    private ListView lvDelicateStrategyDetail;

    private TbUser user;

    // 网络请求
    private final String QUERY_RECOMMEND_USERS = HttpURL.IP_ADDRESS + "/strategy/queryRecommendUsers.json";
    private final String QUERY_RECOMMEND_STRATEGIES = HttpURL.IP_ADDRESS + "/strategy/queryRecommendStrategies.json";

    // 数据源
    private List<TbUser> users;             // 推荐用户
    private List<TbStrategy> strategies;    // 美食攻略


    public StarDelicateStrategyPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_star_delicate_strategy_page, container, false);

        user = UserUtils.readLoginInfo(Objects.requireNonNull(getActivity()).getApplicationContext());
        //Toast.makeText(getActivity(), user.getuSignature(), Toast.LENGTH_SHORT).show();
        tvDelicateStrategyPublish = view.findViewById(R.id.tv_delicate_strategy_publish);
        hlvDelicateStrategyUserList = view.findViewById(R.id.hlv_delicate_strategy_user_list);
        lvDelicateStrategyDetail = view.findViewById(R.id.lv_delicate_strategy_detail);
        initView();
        initData();
        initListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {

    }

    private void initData() {

    }

    private void initListener() {
        tvDelicateStrategyPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到发布美食攻略页面
                if (user.getuId() != 0) {
                    Intent intent = new Intent(getActivity(), StrategyPublishActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("uId", user.getuId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    new CommonDialog(getActivity(), R.style.DialogTheme, "您还没有登录,请先登录", new CommonDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);

                                // 是否需要更换APP切换方式
                            }
                        }
                    }).setTitle("提示").show();
                }
            }
        });
    }

}
