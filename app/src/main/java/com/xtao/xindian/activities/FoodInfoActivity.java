package com.xtao.xindian.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.MainActivity;
import com.xtao.xindian.R;
import com.xtao.xindian.common.CommonResultType;
import com.xtao.xindian.common.FoodResultType;
import com.xtao.xindian.common.task.BitmapTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.fragment.BuycarFragment;
import com.xtao.xindian.pojo.TbFood;
import com.xtao.xindian.pojo.TbUser;
import com.xtao.xindian.utils.UserUtils;
import com.xtao.xindian.view.CircleImageView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FoodInfoActivity extends AppCompatActivity {

//    数据源
    private TbFood food;
//    网络数据源
    private final String URL = HttpURL.IP_ADDRESS + "/food/getFood.json";
    private final String ADD_TO_BAYCAR_URL = HttpURL.IP_ADDRESS + "/order/addBuyCar.json";
    private int fId;
//    消息窗
    private ProgressDialog progressDialog;

    private ImageView ivFoodInfoBk;     // 标题栏
    private TextView tvTitleFoodName;

    private ImageView ivFoodInfoPic;    // 食物图片
    private TextView tvFoodInfoDprice;  // 折扣价
    private TextView tvFoodInfoPrice;   // 原价
    private ImageView ivFoodInfoFollow; // 收藏食物
    private TextView tvFoodInfoName;    // 食物名称
    private TextView tvFoodInfoType;

    private CircleImageView ivFoodMerPic;   // 商家头像
    private TextView tvFoodMerName;
    private TextView tvFoodMerPhone;
    private TextView tvFoodMerAddress;
    private TextView tvFoodMerIntro;

    private EditText etFoodAddBuycar;   // 加入购物车
    private EditText etFoodInfoBuy;     // 购买

    private TbUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_info);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            fId = bundle.getInt("fId");
        }


        initView();
        initData();
        initListener();
    }

    private void initView() {
        ivFoodInfoBk = findViewById(R.id.iv_food_info_bk);
        tvTitleFoodName = findViewById(R.id.tv_title_food_name);

        ivFoodInfoPic = findViewById(R.id.iv_food_info_pic);
        tvFoodInfoDprice = findViewById(R.id.tv_food_info_dprice);
        tvFoodInfoPrice = findViewById(R.id.tv_food_info_price);
        ivFoodInfoFollow = findViewById(R.id.iv_food_info_follow);
        tvFoodInfoName = findViewById(R.id.tv_food_info_name);
        tvFoodInfoType = findViewById(R.id.tv_food_info_type);

        ivFoodMerPic = findViewById(R.id.iv_food_mer_pic);
        tvFoodMerName = findViewById(R.id.tv_food_mer_name);
        tvFoodMerPhone = findViewById(R.id.tv_food_mer_phone);
        tvFoodMerAddress = findViewById(R.id.tv_food_mer_address);
        tvFoodMerIntro = findViewById(R.id.tv_food_mer_intro);

        etFoodAddBuycar = findViewById(R.id.et_food_add_buycar);
        etFoodInfoBuy = findViewById(R.id.et_food_info_buy);



        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在加载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void initData() {
        new FoodAsyncTask().execute(URL);
        user = UserUtils.readLoginInfo(getApplicationContext());
    }

    private void initListener() {
        etFoodAddBuycar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建新的订单
                new AddFoodToBayCarAsyncTask().execute(ADD_TO_BAYCAR_URL);

            }
        });

        etFoodInfoBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到结算页面
                Intent intent = new Intent(getApplicationContext(), BuycarSettleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("fId", fId);
                bundle.putInt("uId", user.getuId());
                bundle.putInt("mId", food.getMer().getmId());
                bundle.putString("fName", food.getfName());
                bundle.putFloat("fDPrice", food.getfDPrice());
                bundle.putString("fUrl", food.getfUrl());

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private class AddFoodToBayCarAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), "添加购物车成功", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                java.net.URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.connect();

                String body = "fId=" + fId + "&mId=" + food.getMer().getmId()
                        + "&uId=" + user.getuId();
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

    private class FoodAsyncTask extends AsyncTask<String, Integer, TbFood> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(TbFood tbFood) {
            super.onPostExecute(tbFood);

            food = tbFood;

            // 装配数据
            tvTitleFoodName.setText(food.getfName());

            if (food.getfUrl() == null || food.getfUrl().equals("")) {
                new BitmapTask().execute(ivFoodInfoPic, HttpURL.FOOD_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(ivFoodInfoPic, food.getfUrl());
            }
            tvFoodInfoDprice.setText(food.getfDPrice().toString());
            tvFoodInfoPrice.setText(food.getfPrice().toString());
            tvFoodInfoName.setText(food.getfName());
            tvFoodInfoType.setText(food.getFoodType().getFtName());

            if (food.getMer().getmUrl() == null || food.getMer().getmUrl().equals("")) {
                new BitmapTask().execute(ivFoodMerPic, HttpURL.MER_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(ivFoodMerPic, food.getMer().getmUrl());
            }
            tvFoodMerName.setText(food.getMer().getmName());
            tvFoodMerPhone.setText(food.getMer().getmPhone());
            tvFoodMerAddress.setText(food.getMer().getmAddress());
            tvFoodMerIntro.setText(food.getMer().getmIntro());

            progressDialog.dismiss();
        }

        @Override
        protected TbFood doInBackground(String... strings) {
            try {
                java.net.URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.connect();

                String body = "fId=" + fId;
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

                    FoodResultType foodResultType = gson.fromJson(jsonCode, FoodResultType.class);
                    if (foodResultType.getState() == 1) {   // 找寻到标题信息
                        // 将用户实体带到主界面
                        return foodResultType.getFood();

                    } else {    // 没有相关的标题
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "服务器数据丢失，请重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } else {    // 网络错误
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
