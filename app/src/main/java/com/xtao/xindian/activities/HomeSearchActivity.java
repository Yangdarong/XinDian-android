package com.xtao.xindian.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
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

public class HomeSearchActivity extends AppCompatActivity {

    //tv_home_search_cancel
    private TextView tvHomeSearchCancel;

    private EditText etHomeSearchKeyword;

    // lv_strategy_search_result
    private ListView lvSearchResult;

    // 装配的列表项目UI
    // civ_strategy_result_pic
    private CircleImageView civStrategyResultPic;
    // tv_strategy_result_name
    private TextView tvStrategyResultName;
    // tv_strategy_result_from
    private TextView tvStrategyResultFrom;

    private List<TbFood> foodList;

    private ProgressDialog progressDialog;

    private final String QUERY_FOODS_LIST_BY_NAME = HttpURL.IP_ADDRESS + "/food/queryFoodsBykW.json";

    private int uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            uId = bundle.getInt("uId");
        } else {
            uId = 0;
        }

        initView();
        initData();
        initListener();
    }

    private void initView() {
        tvHomeSearchCancel = findViewById(R.id.tv_home_search_cancel);
        etHomeSearchKeyword = findViewById(R.id.et_home_search_keyword);

        lvSearchResult = findViewById(R.id.lv_search_results);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在加载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void initData() {


    }

    private void initListener() {
        tvHomeSearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etHomeSearchKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {    // 回车点击事件搜索菜单
                    doSearchFoods();
                }
                return true;
            }
        });

        lvSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int fId = foodList.get(position).getfId();
                if (fId != 0) {
                    Intent intent = new Intent(HomeSearchActivity.this, FoodInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("fId", fId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeSearchActivity.this, "菜品信息错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 获取关键字,检索菜单
     */
    private void doSearchFoods() {
        String keyword = etHomeSearchKeyword.getText().toString();
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
            lvSearchResult.setAdapter(new KwResultAdapter());
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
                        Toast.makeText(HomeSearchActivity.this, "服务器数据丢失，请重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } else {    // 网络错误
                    Looper.prepare();
                    Toast.makeText(HomeSearchActivity.this, "无法连接到服务器,请重试", Toast.LENGTH_SHORT).show();
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
            convertView = View.inflate(HomeSearchActivity.this, R.layout.layout_strategy_result, null);
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

            return convertView;
        }
    }
}
