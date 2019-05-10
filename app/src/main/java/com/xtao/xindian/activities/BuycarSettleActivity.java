package com.xtao.xindian.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.R;
import com.xtao.xindian.common.CommonResultType;
import com.xtao.xindian.common.task.BitmapTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.pojo.TbFood;
import com.xtao.xindian.pojo.TbOrderFood;
import com.xtao.xindian.utils.ValueUtils;
import com.xtao.xindian.view.RoundRectImageView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BuycarSettleActivity extends AppCompatActivity {

    private Intent intent;
    private Bundle bundle;

    private ImageView ivTitleBk;
    private TextView tvTitleName;
    private ImageView ivTitlePic;

    private TextView tvSettleFoodCost;
    private TextView tvSettleFoodBk;
    private TextView tvSettleFoodPay;
    private ListView lvSettleFoodList;

    // List的子项目
    private RoundRectImageView picSettleFoodUrl;
    private TextView tvSettleFoodName;
    private TextView tvSettleAmount;

    // 数据源
    private List<TbOrderFood> orderFoods;

    private int fId, uId, mId;
    private String fName, fUrl;
    private float fDPrice;

    private final String QUICK_SETTLE = HttpURL.IP_ADDRESS + "/order/addSettle.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_buycar);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        ivTitleBk = findViewById(R.id.iv_title_bk);
        tvTitleName = findViewById(R.id.tv_title_name);
        ivTitlePic = findViewById(R.id.iv_title_pic);

        tvSettleFoodCost = findViewById(R.id.tv_settle_food_cost);
        tvSettleFoodBk = findViewById(R.id.tv_settle_food_bk);
        tvSettleFoodPay = findViewById(R.id.tv_settle_food_pay);
        lvSettleFoodList = findViewById(R.id.lv_settle_food_list);
    }

    private void initData() {
        ivTitleBk.setVisibility(View.INVISIBLE);
        tvTitleName.setText("结算");
        ivTitlePic.setVisibility(View.INVISIBLE);

        if (hasFoodId()) {
            Toast.makeText(this, "是空的,结算当前所有订单", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "不是空的，对该项商品快速结账", Toast.LENGTH_SHORT).show();
            fId = bundle.getInt("fId");
            uId = bundle.getInt("uId");
            mId = bundle.getInt("mId");
            fName = bundle.getString("fName");
            fUrl = bundle.getString("fUrl");
            fDPrice = bundle.getFloat("fDPrice");

            TbFood food = new TbFood();
            food.setfId(fId);
            food.setfName(fName);
            food.setfUrl(fUrl);
            food.setfDPrice(fDPrice);
            TbOrderFood orderFood = new TbOrderFood();
            orderFood.setFood(food);
            orderFood.setOfAmount(1);
            orderFoods = new ArrayList<>();
            orderFoods.add(orderFood);

            new QuickSettleListTask().execute(QUICK_SETTLE);

        }


    }

    /**
     *
     * @return
     */
    private boolean hasFoodId() {

        intent = getIntent();
        bundle = intent.getExtras();

        return ValueUtils.isNull(bundle);
    }

    private void initListener() {

    }


    private BuyCarSettleListAdapter mAdapter = new BuyCarSettleListAdapter();
    private class BuyCarSettleListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return orderFoods.size();
        }

        @Override
        public Object getItem(int position) {
            return orderFoods.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(getApplicationContext(), R.layout.layout_food_settle, null);
            picSettleFoodUrl = convertView.findViewById(R.id.pic_buycar_food_url);
            String picUrl = orderFoods.get(position).getFood().getfUrl();
            if (ValueUtils.isNull(picUrl)) {
                new BitmapTask().execute(
                        picSettleFoodUrl, HttpURL.FOOD_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(
                        picSettleFoodUrl, orderFoods.get(position).getFood().getfUrl());
            }
            tvSettleFoodName = convertView.findViewById(R.id.tv_settle_food_name);
            tvSettleFoodName.setText(orderFoods.get(position).getFood().getfName());
            tvSettleAmount = convertView.findViewById(R.id.tv_settle_food_amount);
            tvSettleAmount.setText("x"+ orderFoods.get(position).getOfAmount());

            return convertView;
        }
    }

    private class QuickSettleListTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            lvSettleFoodList.setAdapter(mAdapter);
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

                String body = "fId=" + fId + "&mId=" + mId + "&uId=" + uId ;
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        connection.getOutputStream(), "UTF-8"));
                writer.write(body);
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

                    CommonResultType resultType = gson.fromJson(jsonCode, CommonResultType.class);
                    if (resultType.getState() == 1) {   // 找寻到标题信息
                        // 将用户实体带到主界面
                        return resultType.getMessage();

                    } else {    // 没有相关的标题
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "服务器数据丢失，请重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } else {    // 网络错误
                    responseCode = connection.getResponseCode();
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "无法连接到服务器,请重试", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
