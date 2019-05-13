package com.xtao.xindian.common.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.xtao.xindian.common.value.HttpURL;

public class CommonTask extends AsyncTask<Object, Integer, String> {

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Object... objects) {
        String url = (String) objects[0];
        String body = (String) objects[1];
        Context context = (Context) objects[2];
        return HttpURL.getCommonResultType(url, body, context);
    }
}
