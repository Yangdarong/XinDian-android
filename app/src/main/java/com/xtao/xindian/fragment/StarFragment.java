package com.xtao.xindian.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.R;
import com.xtao.xindian.common.TitleResultType;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.fragment.adapter.StarTitleAdapter;
import com.xtao.xindian.pojo.TbTitle;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class StarFragment extends Fragment {

    private TabLayout mTlHome;
    private ViewPager mVpHome;

    private ProgressDialog progressDialog;

    private String TITLES_URL = HttpURL.IP_ADDRESS + "/title/queryTitles.json";

    public StarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_star, container, false);

        mTlHome = view.findViewById(R.id.tl_star_nav);
        mVpHome = view.findViewById(R.id.vp_star_tab_content);

        initView();
        initData();
        return view;
    }

    private void initView() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("正在加载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  // 圆形旋转


    }

    private void initData() {
        new MyAsyncTask().execute(TITLES_URL);
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, List<TbTitle>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected List<TbTitle> doInBackground(String... strings) {
            try {
                URL url = new URL(TITLES_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.connect();

                String body = "tFrom=" + 2;
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

                    TitleResultType titleResultType = gson.fromJson(jsonCode, TitleResultType.class);
                    if (titleResultType.getState() == 1) {   // 找寻到标题信息
                        // 将用户实体带到主界面
                        return titleResultType.getTitles();

                    } else {    // 没有相关的标题
                        Looper.prepare();
                        Toast.makeText(getActivity(), "服务器数据丢失，请重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } else {    // 网络错误
                    Looper.prepare();
                    Toast.makeText(getActivity(), "无法连接到服务器,请重试", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<TbTitle> titles) {
            super.onPostExecute(titles);

            mVpHome.setAdapter(new StarTitleAdapter(getChildFragmentManager(), titles));
            mTlHome.setupWithViewPager(mVpHome);

            progressDialog.dismiss();
        }
    }
}
