package com.xtao.xindian.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.LoginActivity;
import com.xtao.xindian.R;
import com.xtao.xindian.common.FoodStrategyResultType;
import com.xtao.xindian.common.FoodsResultType;
import com.xtao.xindian.common.task.BitmapTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.dialog.CommonDialog;
import com.xtao.xindian.pojo.TbFood;
import com.xtao.xindian.pojo.TbStrategy;
import com.xtao.xindian.utils.ValueUtils;
import com.xtao.xindian.view.RoundRectImageView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StrategyInfoActivity extends AppCompatActivity {

    // iv_strategy_info_bk
    private ImageView ivStrategyInfoBk;
    // iv_strategy_info_more
    private ImageView ivStrategyInfoMore;
    // tv_strategy_info_title
    private TextView tvStrategyInfoTitle;
    // tv_strategy_info_time
    private TextView tvStrategyInfoTime;

    private TextView tvStrategyInfoName;

    private TextView tvStrategyInfoContext;

    private ListView lvStrategyInfoFoods;
    // 下面的UI控件
    private RoundRectImageView rrivStrategyFoodPic;
    private TextView tvStrategyFoodName;

    private List<TbStrategy> strategies;                // 网络数据源
    private List<List<TbFood>> strategyFoods;           // 网络数据源
    private List<TbFood> foods;                         // 装配的数据源

    private final String QUERY_STRATEGY_FOODS = HttpURL.IP_ADDRESS + "/strategy/queryStrategyInfo.json";
    private final String FOLLOW_STRATEGY = HttpURL.IP_ADDRESS + "/strategy/followStrategy.json";
    private final String COLLECT_MENU = HttpURL.IP_ADDRESS + "/food/collectMenu.json";

    private ProgressDialog progressDialog;

    private int sId;
    private int uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy_info);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        ivStrategyInfoBk = findViewById(R.id.iv_strategy_info_bk);
        ivStrategyInfoMore = findViewById(R.id.iv_strategy_info_more);
        tvStrategyInfoTitle = findViewById(R.id.tv_strategy_info_title);
        tvStrategyInfoTime = findViewById(R.id.tv_strategy_info_time);
        tvStrategyInfoName = findViewById(R.id.tv_strategy_info_name);
        tvStrategyInfoContext = findViewById(R.id.tv_strategy_info_context);
        lvStrategyInfoFoods = findViewById(R.id.lv_strategy_info_foods);

        progressDialog = new ProgressDialog(StrategyInfoActivity.this);
        progressDialog.setTitle("正在加载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void initData() {
        strategies = new ArrayList<>();
        strategyFoods = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sId = bundle.getInt("sId");
            uId = bundle.getInt("uId");
            String body = "sId=" + sId;
            new QueryStrategyInfo().execute(QUERY_STRATEGY_FOODS, body);
        }
    }

    private void initListener() {
        ivStrategyInfoBk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivStrategyInfoMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(ivStrategyInfoMore);
            }
        });
    }

    /**
     * 用户执行关注攻略
     */
    private void followStrategy() {
        if (uId == 0) { // 没有登录, 返回登录
            new CommonDialog(StrategyInfoActivity.this, R.style.DialogTheme, "您还没有进行登录,点击确定将进行登录", new CommonDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        Intent intent = new Intent(StrategyInfoActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    dialog.dismiss();
                }
            }).setTitle("提示").show();
        } else if (sId != 0) {
            String body = "sId=" + sId + "&uId=" + uId;

            new CommonTask().execute(FOLLOW_STRATEGY, body);
        }
    }

    /**
     *  用户收藏菜单
     */
    private void collectMenu() {
       if (uId == 0) { // 没有登录, 返回登录
           new CommonDialog(StrategyInfoActivity.this, R.style.DialogTheme, "您还没有进行登录,点击确定将进行登录", new CommonDialog.OnCloseListener() {
               @Override
               public void onClick(Dialog dialog, boolean confirm) {
                   if (confirm) {
                       Intent intent = new Intent(StrategyInfoActivity.this, LoginActivity.class);
                       startActivity(intent);
                   }
                   dialog.dismiss();
               }
           }).setTitle("提示").show();
       } else if (foods.size() > 0) {   // 通过食物列表对象关注食物
           FoodsResultType resultType = new FoodsResultType();
           resultType.setState(uId);
           resultType.setFoods(foods);
           Gson gson = new Gson();
           String json = gson.toJson(resultType);

           new CommonTask().execute(COLLECT_MENU, json);
       }
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
                switch (item.getItemId()) {
                    case R.id.action_follow_strategy:
                        // 执行关注攻略
                        followStrategy();
                        break;
                    case R.id.action_collect_menu:
                        // 跳转到最近购买
                        collectMenu();
                        break;

                }

                //Toast.makeText(getApplicationContext(), item.get, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                // Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
            }
        });

        popupMenu.show();
    }

    private class CommonTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(StrategyInfoActivity.this, s, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            return HttpURL.getCommonResultType(strings[0], strings[1], StrategyInfoActivity.this);
        }
    }

    private class QueryStrategyInfo extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(StrategyInfoActivity.this, s, Toast.LENGTH_SHORT).show();
            // 装配数据
            TbStrategy strategy = strategies.get(0);
            foods = strategyFoods.get(0);

            if (strategy != null) {
                tvStrategyInfoTitle.setText(strategy.getsName());
                Timestamp time = strategy.getsCreateTime();
                tvStrategyInfoTime.setText(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", new Locale("zh", "ZH")).format(time));
                tvStrategyInfoName.setText("--By ".concat(strategy.getUser().getuSignature()));

                tvStrategyInfoContext.setText(strategy.getsContext());
            }

            lvStrategyInfoFoods.setAdapter(new StrategyFoodAdapter());
            lvStrategyInfoFoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 获取被点击的fId值
                    int fId = foods.get(position).getfId();
                    if (fId != 0) {
                        Intent intent = new Intent(StrategyInfoActivity.this, FoodInfoActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("fId", fId);
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                }
            });
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
                        strategyFoods = resultType.getFoods();

                        return resultType.getMessage();
                    } else {    // 没有相关的标题
                        Looper.prepare();
                        Toast.makeText(StrategyInfoActivity.this, "数据不正确，请重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        return resultType.getMessage();
                    }

                } else {    // 网络错误
                    Looper.prepare();
                    Toast.makeText(StrategyInfoActivity.this, "无法连接到服务器,请重试", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class StrategyFoodAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return foods.size();
        }

        @Override
        public Object getItem(int position) {
            return foods.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(StrategyInfoActivity.this, R.layout.layout_strategy_info, null);
            rrivStrategyFoodPic = convertView.findViewById(R.id.rriv_strategy_food_pic);
            String fUrl = foods.get(position).getfUrl();
            if (ValueUtils.isNull(fUrl)) {
                new BitmapTask().execute(rrivStrategyFoodPic, HttpURL.FOOD_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(rrivStrategyFoodPic, fUrl);
            }

            tvStrategyFoodName = convertView.findViewById(R.id.tv_strategy_food_name);
            tvStrategyFoodName.setText(foods.get(position).getfName());

            return convertView;
        }
    }
}
