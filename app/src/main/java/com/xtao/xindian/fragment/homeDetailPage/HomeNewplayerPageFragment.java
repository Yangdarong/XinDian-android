package com.xtao.xindian.fragment.homeDetailPage;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.R;
import com.xtao.xindian.activities.FoodInfoActivity;
import com.xtao.xindian.common.FoodsResultType;
import com.xtao.xindian.common.task.BitmapTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.pojo.TbFood;
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


public class HomeNewplayerPageFragment extends Fragment {

    private ListView lvPlayerFoodList;

    //    数据源
    private List<TbFood> foods;
    //    适配器
    private FoodListAdapter mAdapter;
    //    数据网络来源
    private final String URL = HttpURL.IP_ADDRESS + "/food/getFoodsList.json";

    private ProgressDialog progressDialog;

    private View view;
    private Fragment fragment = null;

    public HomeNewplayerPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_newplayer_page, container, false);
        lvPlayerFoodList = view.findViewById(R.id.lv_player_food_list);
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

    private class FoodListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private TbFood food;
        private String picUrl;

        public FoodListAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

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
            convertView = mLayoutInflater.inflate(R.layout.food_item, null);
            food = (TbFood) getItem(position);

            RoundRectImageView picFood = convertView.findViewById(R.id.pic_food);
            // 图片需要下载
            if (food.getfUrl() == null || food.getfUrl().equals(""))  // 未设置图片
                picUrl = HttpURL.FOOD_DEFAULT_PIC;
            else     // 设置了图片
                picUrl = food.getfUrl();
            new BitmapTask().execute(picFood, picUrl);

            TextView tvFoodName = convertView.findViewById(R.id.tv_food_name);
            tvFoodName.setText(food.getfName());

            TextView tvFoodType = convertView.findViewById(R.id.tv_food_type);
            tvFoodType.setText(food.getFoodType().getFtName());

            TextView tvFoodDprice = convertView.findViewById(R.id.tv_food_dprice);
            String oPrice = tvFoodDprice.getText().toString();
            String nPrice = oPrice.replace(oPrice.substring(1, oPrice.length()), food.getfDPrice().toString());

            tvFoodDprice.setText(nPrice);

            CircleImageView picUserIcon = convertView.findViewById(R.id.pic_user_icon);
            if (food.getMer().getmUrl() == null || food.getMer().getmUrl().equals("")) {
                picUrl = HttpURL.MER_DEFAULT_PIC;
            } else {
                picUrl = food.getMer().getmUrl();
            }
            new BitmapTask().execute(picUserIcon, picUrl);

            TextView tvMerName = convertView.findViewById(R.id.tv_mer_name);
            tvMerName.setText(food.getMer().getmName());

//            et_food_buy 为按钮添加点击事件
            EditText etFoodBuy = convertView.findViewById(R.id.et_food_buy);
            View.OnClickListener doGetFoodInfoListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 获取菜品ID 存入bundle
                    int fId = ((TbFood) getItem(position)).getfId();
                    Bundle bundle = new Bundle();
                    bundle.putInt("fId", fId);
                    Intent intent = new Intent(getActivity(), FoodInfoActivity.class);
                    intent.putExtras(bundle);
                    // 页面跳转
                    startActivity(intent);
                    //Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
                    Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    //Toast.makeText(v.getContext(), "你拿到的菜品ID是:" + ((TbFood) getItem(position)).getfId(), Toast.LENGTH_SHORT).show();
                }
            };
            etFoodBuy.setOnClickListener(doGetFoodInfoListener);

            return convertView;
        }

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
            mAdapter = new FoodListAdapter(getContext());
            lvPlayerFoodList.setAdapter(mAdapter);

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

                String body = "ftId=" + 1;
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
