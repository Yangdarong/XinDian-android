package com.xtao.xindian.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.R;
import com.xtao.xindian.common.FoodResultType;
import com.xtao.xindian.common.FoodsResultType;
import com.xtao.xindian.common.task.BitmapTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.dialog.CommonDialog;
import com.xtao.xindian.pojo.TbFood;
import com.xtao.xindian.utils.ValueUtils;
import com.xtao.xindian.view.CircleImageView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class StrategySearchActivity extends AppCompatActivity {

    // et_strategy_search_keyword
    private EditText etStrategySearchKeyword;
    // tv_strategy_search_cancel
    private TextView tvStrategySearchCancel;
    // lv_strategy_search_result
    private ListView lvStrategySearchResult;

    // 装配的列表项目UI
    // civ_strategy_result_pic
    private CircleImageView civStrategyResultPic;
    // tv_strategy_result_name
    private TextView tvStrategyResultName;
    // tv_strategy_result_from
    private TextView tvStrategyResultFrom;
    // iv_strategy_result_add
    private ImageView ivStrategyResultAdd;


    private List<TbFood> foodList;

    private ProgressDialog progressDialog;

    // 通过食物关键字获取结果列表
    private final String QUERY_FOODS_LIST_BY_NAME = HttpURL.IP_ADDRESS + "/food/queryFoodsBykW.json";
    private final String ADD_FOOD_TO_MY_STRATEGY = HttpURL.IP_ADDRESS + "/strategy/createFoodStrategy.json";

    private int sId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy_search);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sId = bundle.getInt("sId");
        } else {
            sId = 0;
        }

        initView();
        initData();
        initListener();
    }

    private void initListener() {
        tvStrategySearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etStrategySearchKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {    // 回车点击事件搜索菜单
                    doSearchFoods();
                }
                return false;
            }
        });
    }

    /**
     * 获取关键字,检索菜单
     */
    private void doSearchFoods() {
        String keyword = etStrategySearchKeyword.getText().toString();
        if (!ValueUtils.isNull(keyword)) {  // 如果不会空发送 json 数据开始检索
            TbFood food = new TbFood();
            food.setfName(keyword);

            FoodResultType resultType = new FoodResultType();
            resultType.setFood(food);
            resultType.setState(1);
            Gson gson = new Gson();

            String json = gson.toJson(resultType);

            new KwSearchTask().execute(QUERY_FOODS_LIST_BY_NAME, json);
        }
    }

    private void initData() {

    }

    private void initView() {
        etStrategySearchKeyword = findViewById(R.id.et_strategy_search_keyword);
        tvStrategySearchCancel = findViewById(R.id.tv_strategy_search_cancel);
        lvStrategySearchResult = findViewById(R.id.lv_strategy_search_result);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在加载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            return HttpURL.getCommonResultType(strings[0], strings[1], StrategySearchActivity.this);
        }
    }

    private class KwSearchTask extends AsyncTask<String, Integer, List<TbFood>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<TbFood> foods) {
            super.onPostExecute(foods);
            progressDialog.dismiss();
            foodList = foods;

            // 装配数据
            lvStrategySearchResult.setAdapter(new KwResultAdapter());
        }

        @Override
        protected List<TbFood> doInBackground(String... strings) {
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

                    FoodsResultType resultType = gson.fromJson(jsonCode, FoodsResultType.class);
                    if (resultType.getState() == 1) {   // 找寻到标题信息

                        return resultType.getFoods();

                    } else {    // 没有相关的标题
                        Looper.prepare();
                        Toast.makeText(StrategySearchActivity.this, "服务器数据丢失，请重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } else {    // 网络错误
                    Looper.prepare();
                    Toast.makeText(StrategySearchActivity.this, "无法连接到服务器,请重试", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class KwResultAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return foodList.size();
        }

        @Override
        public Object getItem(int position) {
            return foodList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(StrategySearchActivity.this, R.layout.layout_strategy_result, null);
            civStrategyResultPic = convertView.findViewById(R.id.civ_strategy_result_pic);
            String fUrl = foodList.get(position).getfUrl();
            if (fUrl == null) {
                new BitmapTask().execute(civStrategyResultPic, HttpURL.FOOD_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(civStrategyResultPic, fUrl);
            }
            tvStrategyResultName = convertView.findViewById(R.id.tv_strategy_result_name);
            tvStrategyResultName.setText(foodList.get(position).getfName());
            tvStrategyResultFrom = convertView.findViewById(R.id.tv_strategy_result_from);
            tvStrategyResultFrom.setText("--来自".concat(foodList.get(position).getMer().getmName()));
            ivStrategyResultAdd = convertView.findViewById(R.id.iv_strategy_result_add);

            ivStrategyResultAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CommonDialog(StrategySearchActivity.this, R.style.DialogTheme, "是否添加该菜品?", new CommonDialog.OnCloseListener() {

                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                // 点击确定,添加该项菜品
                                int fId = foodList.get(position).getfId();
                                String body = "sId=" + sId + "&fId=" + fId;

                                new CommonTask().execute(ADD_FOOD_TO_MY_STRATEGY, body);
                            }
                        }
                    }).setTitle("提示").show();
                }
            });
            return convertView;
        }
    }
}
