package com.xtao.xindian.common.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.xtao.xindian.common.value.HttpURL;

public class CommonTask extends AsyncTask<Object, Integer, String> {

    private Context context;

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Object... objects) {
        String url = (String) objects[0];
        String body = (String) objects[1];
        context = (Context) objects[2];
        return HttpURL.getCommonResultType(url, body, context);
    }
}
