package com.xtao.xindian.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.R;
import com.xtao.xindian.activities.HomeSearchActivity;
import com.xtao.xindian.common.TitleResultType;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.fragment.adapter.HomeTitleAdapter;
import com.xtao.xindian.pojo.TbTitle;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class HomeFragment extends Fragment {

    private TabLayout mTlHome;
    private ViewPager mVpHome;

    private ProgressDialog progressDialog;

    private String TITLES_URL = HttpURL.IP_ADDRESS + "/title/queryTitles.json";
    // et_home_page_search
    private EditText etHomePageSearch;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mTlHome = view.findViewById(R.id.tl_top_nav);
        mVpHome = view.findViewById(R.id.vp_home_tab_content);
        etHomePageSearch = view.findViewById(R.id.et_home_page_search);

        initView();
        initData();
        initListener();
        return view;
    }

    private void initListener() {
        etHomePageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HomeSearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        // 1、 获取首页的标题信息
        // 访问 SSM 后台 需要开启子线程
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.1.100:8080/xindian/title/queryTitles.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.connect();

                    String body = "tFrom=" + 1;
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
                            titles = titleResultType.getTitles();

                            mVpHome.setAdapter(new HomeTitleAdapter(getChildFragmentManager(), titles));
                            mTlHome.setupWithViewPager(mVpHome);

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
            }
        }).start();*/

        // 填充标题界面
        /*mVpHome.setAdapter(new HomeTitleAdapter(getChildFragmentManager(), titles));
        mTlHome.setupWithViewPager(mVpHome);*/

        new MyAsyncTask().execute(TITLES_URL);
    }

    private void initView() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("正在加载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  // 圆形旋转


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

                String body = "tFrom=" + 1;
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

            mVpHome.setAdapter(new HomeTitleAdapter(getChildFragmentManager(), titles));
            mTlHome.setupWithViewPager(mVpHome);

            progressDialog.dismiss();
        }
    }

    final class MorePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView tv = new TextView(container.getContext());
            tv.setText("布局" + position);
            tv.setTextSize(30.0f);
            tv.setGravity(Gravity.CENTER);
            (container).addView(tv);
            return tv;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "选项" + position;
        }
    }

}
