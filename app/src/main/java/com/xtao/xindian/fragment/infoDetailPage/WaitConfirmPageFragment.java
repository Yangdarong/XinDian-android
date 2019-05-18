package com.xtao.xindian.fragment.infoDetailPage;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xtao.xindian.R;
import com.xtao.xindian.common.FoodsResultType;
import com.xtao.xindian.common.task.BitmapTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.pojo.TbFood;
import com.xtao.xindian.utils.ValueUtils;
import com.xtao.xindian.view.CircleImageView;
import com.xtao.xindian.view.RoundRectImageView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class WaitConfirmPageFragment extends Fragment {

    private LinearLayout llWaitConfirm;

    private TextView tvWaitNon;

    // 装配的ListView
    private ListView lvWaitConfirm;
    private List<TbFood> foods;         // 需要装配的数据源

    private RoundRectImageView rrivFoodPic;
    private TextView tvFoodName;
    private CircleImageView civMerIcon;
    private TextView tvMerName;
    private EditText etFoodBuy;

    private final String QURRY_WAIT_CONFIRM = HttpURL.IP_ADDRESS;

    private ProgressDialog progressDialog;

    public WaitConfirmPageFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wait_confirm_page, container, false);
        tvWaitNon = view.findViewById(R.id.tv_wait_non);
        lvWaitConfirm = view.findViewById(R.id.lv_wait_confirm);
        initView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initDate();
    }

    private void initDate() {
        foods = new ArrayList<>();
        String body = "";
        new WaitConfirmTask().execute(QURRY_WAIT_CONFIRM, body);
    }

    private void initView() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("正在加载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private class WaitConfirmTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (foods.size() != 0) {
                lvWaitConfirm.setAdapter(new WaitConfirmAdapter());
            }
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.connect();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        connection.getOutputStream(), "UTF-8"));
                writer.write(strings[1]);
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
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    FoodsResultType resultType = gson.fromJson(jsonCode, FoodsResultType.class);
                    if (resultType.getState() == 1) {   // 找寻到标题信息
                        foods = resultType.getFoods();

                        return "成功";
                    } else {    // 没有相关的标题
                        Looper.prepare();
                        Toast.makeText(getContext(), "数据不正确，请重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        return null;
                    }

                } else {    // 网络错误
                    Looper.prepare();
                    Toast.makeText(getContext(), "无法连接到服务器,请重试", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class WaitConfirmAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return foods.size();
        }

        @Override
        public Object getItem(int position) {
            return foods.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(getContext(), R.layout.food_item_three, null);
            rrivFoodPic = convertView.findViewById(R.id.rriv_food_pic);
            String fUrl = foods.get(position).getfUrl();
            if (ValueUtils.isNull(fUrl)) {
                new BitmapTask().execute(rrivFoodPic, HttpURL.FOOD_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(rrivFoodPic, fUrl);
            }
            tvFoodName = convertView.findViewById(R.id.tv_food_name);
            tvFoodName.setText(foods.get(position).getfName());
            civMerIcon = convertView.findViewById(R.id.civ_mer_icon);
            String mUrl = foods.get(position).getMer().getmUrl();
            if (ValueUtils.isNull(fUrl)) {
                new BitmapTask().execute(civMerIcon, HttpURL.MER_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(civMerIcon, mUrl);
            }
            tvMerName = convertView.findViewById(R.id.tv_mer_name);
            tvMerName.setText(foods.get(position).getMer().getmName());
            etFoodBuy = convertView.findViewById(R.id.et_food_buy);
            return convertView;
        }
    }

}