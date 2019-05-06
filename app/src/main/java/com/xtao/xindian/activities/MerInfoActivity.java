package com.xtao.xindian.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.R;
import com.xtao.xindian.common.MerFoodsResultType;
import com.xtao.xindian.common.task.BitmapTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.pojo.TbMer;
import com.xtao.xindian.utils.ValueUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MerInfoActivity extends AppCompatActivity {

    private TbMer mer;
    private final String URL = HttpURL.IP_ADDRESS + "/mer/queryMerAndFoods.json";
    private int mId;

    private ImageView ivMerInfoBk;

    private ImageView ivMerInfoPic;
    private TextView tvMerInfoName;
    private TextView tvMerInfoAddress;
    private TextView tvMerInfoPhone;
    private TextView tvMerInfoIntro;

    private TextView tvMerInfoFollow;

    private ListView lvMerInfoFoods;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mer_info);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            mId = bundle.getInt("mId");
        }
        initView();
        initData();
        initListener();
    }

    private void initView() {
        ivMerInfoBk = findViewById(R.id.iv_mer_info_bk);
        ivMerInfoPic = findViewById(R.id.iv_mer_info_pic);
        tvMerInfoName = findViewById(R.id.tv_mer_info_name);
        tvMerInfoAddress = findViewById(R.id.tv_mer_info_address);
        tvMerInfoPhone = findViewById(R.id.tv_mer_info_phone);
        tvMerInfoIntro = findViewById(R.id.tv_mer_info_intro);

        tvMerInfoFollow = findViewById(R.id.tv_mer_info_follow);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在加载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void initData() {

    }

    private void initListener() {

    }

    private class MerAsyncTask extends AsyncTask<String, Integer, TbMer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(TbMer tbMer) {
            super.onPostExecute(tbMer);

            mer = tbMer;
            // 装配数据
            if (ValueUtils.isNull(mer.getmUrl())) {
                new BitmapTask().execute(ivMerInfoPic, HttpURL.MER_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(ivMerInfoPic, mer.getmUrl());
            }
            tvMerInfoName.setText(mer.getmName());
            tvMerInfoAddress.setText(mer.getmAddress());
            tvMerInfoPhone.setText(mer.getmPhone());
            tvMerInfoIntro.setText(mer.getmIntro());


            progressDialog.dismiss();
        }

        @Override
        protected TbMer doInBackground(String... strings) {
            try {
                java.net.URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.connect();

                String body = "mId=" + mId;
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

                    //MerResultType foodResultType = gson.fromJson(jsonCode, FoodResultType.class);
                    MerFoodsResultType merFoodsResultType = gson.fromJson(jsonCode, MerFoodsResultType.class);
                    if (merFoodsResultType.getState() == 1) {   // 找寻到标题信息
                        // 将用户实体带到主界面
                        return merFoodsResultType.getMer();

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
}
