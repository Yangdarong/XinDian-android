package com.xtao.xindian.fragment.starDetailPage;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.LoginActivity;
import com.xtao.xindian.R;
import com.xtao.xindian.activities.StrategyPublishActivity;
import com.xtao.xindian.common.FoodStrategyResultType;
import com.xtao.xindian.common.StrategiesResultType;
import com.xtao.xindian.common.task.BitmapTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.dialog.CommonDialog;
import com.xtao.xindian.pojo.TbFood;
import com.xtao.xindian.pojo.TbFoodStrategy;
import com.xtao.xindian.pojo.TbStrategy;
import com.xtao.xindian.pojo.TbUser;
import com.xtao.xindian.utils.UserUtils;
import com.xtao.xindian.utils.ValueUtils;
import com.xtao.xindian.view.CircleImageView;
import com.xtao.xindian.view.HorizontalListView;
import com.xtao.xindian.view.RoundRectImageView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StarDelicateStrategyPageFragment extends Fragment {

    // hlv_delicate_strategy_user_list
    private HorizontalListView hlvDelicateStrategyUserList;
    // 下的UI组件
    private CircleImageView civRecommendIcon;
    private TextView tvRecommendName;

    // tv_delicate_strategy_publish
    private TextView tvDelicateStrategyPublish;
    // lv_delicate_strategy_detail
    private ListView lvDelicateStrategyDetail;
    // 下的UI组件
    private RoundRectImageView rrivRecommendUserIcon;
    private TextView tvRecommendUserName;
    private TextView tvRecommendStrategyTitle;
    private LinearLayout llRecommendPicLayout;
    private ImageView ivRecommendMore;
    private TextView tvRecommendStrategySum;

    private TbUser user;

    private ProgressDialog progressDialog;

    // 网络请求
    private final String QUERY_RECOMMEND_USERS = HttpURL.IP_ADDRESS + "/strategy/queryRecommendUsers.json";
    private final String QUERY_RECOMMEND_STRATEGIES = HttpURL.IP_ADDRESS + "/strategy/queryRecommendStrategies.json";

    // 数据源
    private List<TbStrategy> userStrategies;    //
    private List<TbUser> users;                 // 推荐用户
    private List<TbStrategy> strategies;        // 美食攻略
    private List<List<TbFood>> recommendFoodListTotal;


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
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("正在加载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void initData() {
        userStrategies = new ArrayList<>();
        users = new ArrayList<>();
        strategies = new ArrayList<>();

        // 1、获取推荐用户列表
        new RecommendUsersTask().execute(QUERY_RECOMMEND_USERS, "");
        // 2、获取美食攻略
        new RecommendListTask().execute(QUERY_RECOMMEND_STRATEGIES, "");
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

    private class RecommendListTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);

            lvDelicateStrategyDetail.setAdapter(new RecomendListAdapter());
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.connect();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        connection.getOutputStream(), "UTF-8"));
                writer.write(strings[1]);
                writer.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {    // 200
                    InputStream inputStream = connection.getInputStream();
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int len = 0;
                    while ((len = inputStream.read(data)) != -1) {
                        outStream.write(data, 0, len);
                    }
                    inputStream.close();

                    String jsonCode = new String(outStream.toByteArray());
                    Gson gson = new Gson();

                    FoodStrategyResultType resultType = gson.fromJson(jsonCode, FoodStrategyResultType.class);
                    if (resultType.getState() == 1) {   // 找寻到标题信息
                        strategies = resultType.getStrategies();
                        recommendFoodListTotal = resultType.getFoods();

                        return resultType.getMessage();
                    } else {    // 没有相关的标题
                        Looper.prepare();
                        Toast.makeText(getContext(), "数据不正确，请重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        return null;
                    }

                } else {    // 网络错误
                    Looper.prepare();
                    Toast.makeText(getContext(), "无法连接到服务器,请重试", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class RecommendUsersTask extends AsyncTask<String, Integer, List<TbStrategy>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<TbStrategy> tbStrategies) {
            super.onPostExecute(tbStrategies);
            userStrategies = tbStrategies;
            if (userStrategies.size() > 0) {
                for (TbStrategy strategy : userStrategies) {
                    users.add(strategy.getUser());
                }

                // 装配数据
                hlvDelicateStrategyUserList.setAdapter(new RecommendUsersAdapter());
            }

            progressDialog.dismiss();
        }

        @Override
        protected List<TbStrategy> doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.connect();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        connection.getOutputStream(), "UTF-8"));
                writer.write(strings[1]);
                writer.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {    // 200
                    InputStream inputStream = connection.getInputStream();
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int len = 0;
                    while ((len = inputStream.read(data)) != -1) {
                        outStream.write(data, 0, len);
                    }
                    inputStream.close();

                    String jsonCode = new String(outStream.toByteArray());
                    Gson gson = new Gson();

                    StrategiesResultType resultType = gson.fromJson(jsonCode, StrategiesResultType.class);
                    if (resultType.getState() == 1) {   // 找寻到标题信息
                        return resultType.getStrategies();
                    } else {    // 没有相关的标题
                        Looper.prepare();
                        Toast.makeText(getContext(), "数据不正确，请重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        return null;
                    }

                } else {    // 网络错误
                    Looper.prepare();
                    Toast.makeText(getContext(), "无法连接到服务器,请重试", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class RecomendListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return strategies.size();
        }

        @Override
        public Object getItem(int position) {
            return strategies.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(getContext(), R.layout.layout_recomment_list, null);
            rrivRecommendUserIcon = convertView.findViewById(R.id.rriv_recommend_user_icon);
            String uHeadPortrait = strategies.get(position).getUser().getuHeadPortrait();
            if (ValueUtils.isNull(uHeadPortrait)) {
                new BitmapTask().execute(rrivRecommendUserIcon, HttpURL.USER_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(rrivRecommendUserIcon, uHeadPortrait);
            }
            tvRecommendUserName = convertView.findViewById(R.id.tv_recommend_user_name);
            tvRecommendUserName.setText(strategies.get(position).getUser().getuSignature());

            tvRecommendStrategyTitle = convertView.findViewById(R.id.tv_recommend_strategy_title);
            tvRecommendStrategyTitle.setText(strategies.get(position).getsName());

            List<TbFood> foods = recommendFoodListTotal.get(position);
            llRecommendPicLayout = convertView.findViewById(R.id.ll_recommend_pic_layout);
            for (int i = 0; i < foods.size(); i++) {
                ImageView image = new ImageView(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(75, 75);
                lp.setMargins(0, 0, 5, 0);
                image.setLayoutParams(lp);
                String fUrl = foods.get(i).getfUrl();
                if (ValueUtils.isNull(fUrl)) {
                    new BitmapTask().execute(image, HttpURL.FOOD_DEFAULT_PIC);
                } else {
                    new BitmapTask().execute(image, fUrl);
                }

                llRecommendPicLayout.addView(image);
            }
            ivRecommendMore = convertView.findViewById(R.id.iv_recommend_more);
            if (foods.size() < 3) {
                ivRecommendMore.setVisibility(View.INVISIBLE);
            }
            tvRecommendStrategySum = convertView.findViewById(R.id.tv_recommend_strategy_sum);
            tvRecommendStrategySum.setText("共推荐了" + foods.size() + "道菜品");

            return convertView;
        }

    }

    private class RecommendUsersAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(getContext(), R.layout.layout_recommend_user, null);
            civRecommendIcon = convertView.findViewById(R.id.civ_recommend_icon);
            String uHeadPortrait = users.get(position).getuHeadPortrait();
            if (ValueUtils.isNull(uHeadPortrait)) {
                new BitmapTask().execute(civRecommendIcon, HttpURL.USER_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(civRecommendIcon, uHeadPortrait);
            }

            tvRecommendName = convertView.findViewById(R.id.tv_recommend_name);
            String uSignature = users.get(position).getuSignature();
            if (uSignature.length() > 8) {
                uSignature.replace(uSignature.substring(8), "..");
            }
            tvRecommendName.setText(uSignature);

            return convertView;
        }
    }
}
