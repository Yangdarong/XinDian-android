package com.xtao.xindian.fragment.homeDetailPage;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.R;
import com.xtao.xindian.common.FoodsResultType;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.fragment.adapter.FoodListAdapter;
import com.xtao.xindian.pojo.TbFood;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class HomeWesternStylePageFragment extends Fragment {

    //    lv_western_style_food_list
    private ListView lvWesternStyleFoodList;
    //    数据源
    private List<TbFood> foods;
    //    适配器
    private FoodListAdapter mAdapter;
    //    数据网络来源
    private final String URL = HttpURL.IP_ADDRESS + "/food/getFoodsList.json";

    private ProgressDialog progressDialog;

    private View view;
    private Fragment fragment = null;

    public HomeWesternStylePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_western_style_page, container, false);
        lvWesternStyleFoodList = view.findViewById(R.id.lv_western_style_food_list);
        fragment = this;
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
        new FlAsyncTask().execute(URL);
    }

    private class FlAsyncTask extends AsyncTask<String, Integer, List<TbFood>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<TbFood> tbFoods) {
            super.onPostExecute(tbFoods);

            // 装配数据
            foods = tbFoods;
            mAdapter = new FoodListAdapter(getContext(), foods, view, fragment);
            lvWesternStyleFoodList.setAdapter(mAdapter);

            progressDialog.dismiss();
        }

        @Override
        protected List<TbFood> doInBackground(String... strings) {
            try {
                java.net.URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.connect();

                String body = "ftId=" + 4;
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

                    FoodsResultType foodResultType = gson.fromJson(jsonCode, FoodsResultType.class);
                    if (foodResultType.getState() == 1) {   // 找寻到标题信息
                        // 将用户实体带到主界面
                        return foodResultType.getFoods();

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
    }

}
