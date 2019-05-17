package com.xtao.xindian.fragment.homeDetailPage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.LoginActivity;
import com.xtao.xindian.R;
import com.xtao.xindian.activities.FoodInfoActivity;
import com.xtao.xindian.common.FoodsResultType;
import com.xtao.xindian.common.task.BitmapTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.pojo.TbFood;
import com.xtao.xindian.utils.UserUtils;
import com.xtao.xindian.utils.ValueUtils;
import com.xtao.xindian.view.CircleImageView;
import com.xtao.xindian.view.RoundRectImageView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class HomeNewTastePageFragment extends Fragment {

    // lv_new_taste_list
    private ListView lvNewTasteList;
    // 下的UI控件
    private RoundRectImageView rrivFoodPic;
    private TextView tvFoodName;
    private TextView tvFoodType;
    private TextView tvFoodDprice;
    private ImageView ivFoodNew;
    private CircleImageView civUserIcon;
    private TextView tvMerName;
    private EditText etFoodBuy;

    //    数据源
    private List<TbFood> foods;


    // 网络数据来源
    private final String QUERY_FOOD_BY_CREATE_TIME_DESC = HttpURL.IP_ADDRESS + "/food/queryFoodsByTime.json";

    private ProgressDialog progressDialog;

    public HomeNewTastePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_newtaste_page, container, false);
        lvNewTasteList = view.findViewById(R.id.lv_new_taste_list);
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
        new QueryFoodsTask().execute(QUERY_FOOD_BY_CREATE_TIME_DESC, "");
    }

    private class QueryFoodsTask extends AsyncTask<String, Integer, List<TbFood>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<TbFood> tbFoods) {
            super.onPostExecute(tbFoods);
            foods = tbFoods;
            lvNewTasteList.setAdapter(new FoodsAdapter());
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

                String body = strings[1];
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
                        Toast.makeText(getActivity(), "没有相关记录", Toast.LENGTH_SHORT).show();
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

    private class FoodsAdapter extends BaseAdapter {

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(getContext(), R.layout.food_item_two, null);
            rrivFoodPic = convertView.findViewById(R.id.rriv_food_pic);
            String fUrl = foods.get(position).getfUrl();
            if (ValueUtils.isNull(fUrl)) {
                new BitmapTask().execute(rrivFoodPic, HttpURL.FOOD_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(rrivFoodPic, fUrl);
            }
            tvFoodName = convertView.findViewById(R.id.tv_food_name);
            tvFoodName.setText(foods.get(position).getfName());
            tvFoodType = convertView.findViewById(R.id.tv_food_type);
            tvFoodType.setText(foods.get(position).getFoodType().getFtName());
            tvFoodDprice = convertView.findViewById(R.id.tv_food_dprice);
            tvFoodDprice.setText("￥".concat("" + foods.get(position).getfDPrice()));
            ivFoodNew = convertView.findViewById(R.id.iv_food_new);
            if (position < 3) {
                ivFoodNew.setVisibility(View.VISIBLE);
            }

            civUserIcon = convertView.findViewById(R.id.civ_mer_icon);
            String mUrl = foods.get(position).getMer().getmUrl();
            if (ValueUtils.isNull(fUrl)) {
                new BitmapTask().execute(civUserIcon, HttpURL.MER_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(civUserIcon, mUrl);
            }

            tvMerName = convertView.findViewById(R.id.tv_mer_name);
            tvMerName.setText(foods.get(position).getMer().getmName());
            etFoodBuy = convertView.findViewById(R.id.et_food_buy);
            etFoodBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 获取菜品ID 存入bundle
                    int fId = foods.get(position).getfId();
                    Bundle bundle = new Bundle();
                    bundle.putInt("fId", fId);

                    // 页面跳转
                    if (UserUtils.isLogined(getContext())) {
                        Intent intent = new Intent(getContext(), FoodInfoActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    } else {    // 没有登录
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                }
            });

            return convertView;
        }
    }
}
