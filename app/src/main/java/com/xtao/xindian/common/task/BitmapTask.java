package com.xtao.xindian.common.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.xtao.xindian.common.value.HttpURL;

public class BitmapTask extends AsyncTask<Object, Void, Bitmap> {

    private ImageView ivPicture;
    private String url;

    @Override
    protected Bitmap doInBackground(Object... objects) {
        ivPicture = (ImageView) objects[0];
        url = (String) objects[1];
        return HttpURL.getHttpBitmap(url);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            ivPicture.setImageBitmap(bitmap);
        }
    }
}
