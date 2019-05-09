package com.xtao.xindian.common.task;

import android.os.AsyncTask;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xtao.xindian.common.CommonResultType;
import com.xtao.xindian.common.value.HttpURL;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class BuycarCostUpdateTask extends AsyncTask<Object, Integer, String> {

    private final String COUNT_TOTAL = HttpURL.IP_ADDRESS + "/order/total.json";
    private TextView tv;
    private int uId;

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (!s.isEmpty()) {
            tv.setText("￥".concat(s));
        }
    }

    @Override
    protected String doInBackground(Object... objects) {
        try {
            URL url = new URL(COUNT_TOTAL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            tv = (TextView) objects[0];
            uId = (Integer) objects[1];

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
