package com.xtao.xindian.common.value;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURL {

     public static final String IP_ADDRESS = "http://192.168.0.104:8080/xindian";

     public static final String MER_DEFAULT_PIC = "/upload/mers/default.png";
     public static final String FOOD_DEFAULT_PIC = "/upload/foods/default.png";

     public static Bitmap getHttpBitmap(String picUrl) {
          URL pictureUrl = null;
          Bitmap bitmap = null;

          try {
               pictureUrl = new URL(IP_ADDRESS + picUrl);
               // 获取连接
               HttpURLConnection connection = (HttpURLConnection) pictureUrl.openConnection();
               connection.setConnectTimeout(6000);
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

}
