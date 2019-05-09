package com.xtao.xindian.common.task;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.xtao.xindian.common.CommonResultType;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.pojo.TbOrderFood;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FoodNcvSubTask extends AsyncTask<TbOrderFood, Integer, String> {

    private final String SUB_BUY_CAR_ITEM_NUM = HttpURL.IP_ADDRESS + "/order/foodNumSub.json";

    @Override
    protected String doInBackground(TbOrderFood... tbOrderFoods) {
        try {
            URL url = new URL(SUB_BUY_CAR_ITEM_NUM);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.connect();
            String body = "ofId=" + tbOrderFoods[0].getOfId() + "&ofAmount=" + tbOrderFoods[0].getOfAmount();
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
