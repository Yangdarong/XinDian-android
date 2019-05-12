package com.xtao.xindian.activities;

import android.app.Dialog;
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
import com.xtao.xindian.common.OrderFoodsResultType;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.dialog.CommonDialog;
import com.xtao.xindian.pojo.TbFood;
import com.xtao.xindian.pojo.TbMer;
import com.xtao.xindian.pojo.TbOrder;
import com.xtao.xindian.pojo.TbOrderFood;
import com.xtao.xindian.utils.ValueUtils;

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
    private TextView tvSettleFoodNo;
    private TextView tvSettleFoodName;
    private TextView tvSettleAmount;

    // 数据源
    private List<TbOrderFood> orderFoods;
    private List<TbOrder> orders;

    private int fId, uId, mId, ofAmount;
    private String fName, fUrl;
    private float fDPrice;

    // 查询购物车
    private final String QUERY_BUY_CAR = HttpURL.IP_ADDRESS + "/order/queryBayCar.json";
    // 结账
    private final String QUICK_SETTLE = HttpURL.IP_ADDRESS + "/order/doSettle.json";
    // json 字符串
    private String json;


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

        if (!hasFoodId()) {
            uId = bundle.getInt("uId");
            //Toast.makeText(this, "uId=" + uId, Toast.LENGTH_SHORT).show();
            // 首先获取购物车中的OrderFoods
            new BuyCarAsyncTask().execute(QUERY_BUY_CAR);
        } else {
            //Toast.makeText(this, "不是空的，对该项商品快速结账", Toast.LENGTH_SHORT).show();
            fId = bundle.getInt("fId");
            uId = bundle.getInt("uId");
            mId = bundle.getInt("mId");
            fName = bundle.getString("fName");
            fUrl = bundle.getString("fUrl");
            fDPrice = bundle.getFloat("fDPrice");
            ofAmount = bundle.getInt("ofAmount");

            TbFood food = new TbFood();
            food.setfId(fId);
            food.setfName(fName);
            food.setfUrl(fUrl);
            food.setfDPrice(fDPrice);

            food.setmId(mId);
            TbOrderFood orderFood = new TbOrderFood();
            TbOrder order = new TbOrder();
            order.setuId(uId);

            orderFood.setOrder(order);
            orderFood.setFood(food);
            orderFood.setOfAmount(ofAmount);
            orderFoods = new ArrayList<>();
            orderFoods.add(orderFood);

            /*new QuickSettleListTask().execute(QUICK_SETTLE);*/
            lvSettleFoodList.setAdapter(mAdapter);
            updateTotal();
        }

        // 更新总金额
        //updateTotal();

    }

    private void updateTotal() {
        float cost = 0;
        for (TbOrderFood orderFood : orderFoods) {
            cost += orderFood.getOfAmount() * orderFood.getFood().getfDPrice();
        }

        tvSettleFoodCost.setText("￥" + String.format("%.2f", cost));
    }

    /**
     *
     * @return
     */
    private boolean hasFoodId() {

        intent = getIntent();
        bundle = intent.getExtras();

        if (bundle != null) {
            return bundle.containsKey("fId");
        }
        return false;
    }

    private void initListener() {
        // 返回确认
        tvSettleFoodBk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 完成支付
        tvSettleFoodPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出一个消息提示框
                new CommonDialog(BuycarSettleActivity.this, R.style.DialogTheme, "是否提交订单?", new CommonDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            // 判断是否存在order
                            OrderFoodsResultType resultType = new OrderFoodsResultType();
                            if (ValueUtils.isNull(orders) || orders.size() == 0) {  // 临时购物车结算
                                resultType.setOrders(null);
                            } else {    // 购物车结算
                                resultType.setOrders(orders);
                            }
                            resultType.setState(1);
                            resultType.setOrderFoods(orderFoods);
                            resultType.setMessage("传输成功");

                            Gson gson = new Gson();
                            json = gson.toJson(resultType);
                            new SettleAsyncTask().execute(QUICK_SETTLE, json);
                            dialog.dismiss();
                        }
                    }
                }).setTitle("交易提示").show();
            }
        });
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
            tvSettleFoodNo = convertView.findViewById(R.id.tv_settle_food_no);
            tvSettleFoodNo.setText("" + (position + 1));
            tvSettleFoodName = convertView.findViewById(R.id.tv_settle_food_name);
            tvSettleFoodName.setText(orderFoods.get(position).getFood().getfName());
            tvSettleAmount = convertView.findViewById(R.id.tv_settle_food_amount);
            tvSettleAmount.setText("￥" + orderFoods.get(position).getFood().getfDPrice() + "x"+ orderFoods.get(position).getOfAmount());


            return convertView;
        }
    }

    private class SettleAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // 页面跳转到订单确认页
            Intent intent = new Intent(BuycarSettleActivity.this, OrderConfirmActivity.class);
            startActivity(intent);
        }

        @Override
        protected String doInBackground(String... strings) {

            return HttpURL.getCommonResultType(strings[0], strings[1], BuycarSettleActivity.this);
        }
    }

    private class BuyCarAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (orderFoods.size() != 0) {
                // 消除控件
                lvSettleFoodList.setAdapter(mAdapter);
                updateTotal();

            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
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
                String body = "uId=" + uId;
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

                    OrderFoodsResultType resultType = gson.fromJson(jsonCode, OrderFoodsResultType.class);
                    if (resultType.getState() == 1) {   // 找寻到标题信息
                        // 将用户实体带到主界面
                        orders = resultType.getOrders();
                        orderFoods = resultType.getOrderFoods();


                        return resultType.getMessage();

                    } else {    // 没有相关的标题
                        Looper.prepare();
                        Toast.makeText(BuycarSettleActivity.this, "服务器数据丢失，请重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } else {    // 网络错误
                    Looper.prepare();
                    Toast.makeText(BuycarSettleActivity.this, "无法连接到服务器,请重试", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
