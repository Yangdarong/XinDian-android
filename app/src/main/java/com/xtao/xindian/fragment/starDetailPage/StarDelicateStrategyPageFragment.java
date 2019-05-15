package com.xtao.xindian.fragment.starDetailPage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtao.xindian.R;
import com.xtao.xindian.activities.StrategyPublishActivity;

public class StarDelicateStrategyPageFragment extends Fragment {

    // tv_delicate_strategy_publish
    private TextView tvDelicateStrategyPublish;

    public StarDelicateStrategyPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_star_delicate_strategy_page, container, false);

        tvDelicateStrategyPublish = view.findViewById(R.id.tv_delicate_strategy_publish);

        initView();
        initData();
        initListener();
        return view;
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
                Intent intent = new Intent(getActivity(), StrategyPublishActivity.class);
                startActivity(intent);
            }
        });
    }

}
