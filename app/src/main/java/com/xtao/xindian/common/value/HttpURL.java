package com.xtao.xindian.common.value;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.common.CommonResultType;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURL {

     //public static final String IP_ADDRESS = "http://192.168.0.117:8080/xindian";
     //public static final String IP_ADDRESS = "http://172.24.94.178:8080/xindian";
     //public static final String IP_ADDRESS = "http://120.78.218.188:8080/xindian";
     public static final String IP_ADDRESS = "http://192.168.1.103:8080/xindian";

     public static final String MER_DEFAULT_PIC = "/upload/mers/default.png";
     public static final String FOOD_DEFAULT_PIC = "/upload/foods/default.png";
     public static final String USER_DEFAULT_PIC = "/upload/users/default.png";

     public static Bitmap getHttpBitmap(String picUrl) {
          URL pictureUrl = null;
          Bitmap bitmap = null;

          try {
               pictureUrl = new URL(IP_ADDRESS + picUrl);
               // 获取连接
               HttpURLConnection connection = (HttpURLConnection) pictureUrl.openConnection();
               connection.setConnectTimeout(30000);
               // 连接设置获得数据流
               connection.setDoInput(true);
               // 不适用缓存
               connection.setUseCaches(false);

               InputStream is = connection.getInputStream();
               bitmap = BitmapFactory.decodeStream(is);

               is.close();
          } catch (Exception e) {
               e.printStackTrace();
          }

          return bitmap;
     }

     public static String getCommonResultType(String urlStr, String body, Context context) {
          try {
               URL url = new URL(urlStr);
               HttpURLConnection connection = (HttpURLConnection) url.openConnection();

               connection.setRequestMethod("POST");
               connection.setDoInput(true);
               connection.setDoOutput(true);
               connection.setUseCaches(false);
               connection.connect();

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

                         return resultType.getMessage();

                    } else {    // 没有相关的标题
                         return resultType.getMessage();
                    }
               } else {    // 网络错误
                    Looper.prepare();
                    Toast.makeText(context, "无法连接到服务器,请重试(错误:" + responseCode + ")", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return null;
               }
          } catch (Exception e) {
               e.printStackTrace();
          }

          return null;
     }
}
