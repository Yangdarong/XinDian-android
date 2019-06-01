package com.xtao.xindian.fragment.infoDetailPage;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.xtao.xindian.dialog.CommonDialog;
import com.xtao.xindian.pojo.TbFood;
import com.xtao.xindian.pojo.TbUser;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WaitReceivePageFragment extends Fragment {

    //ll_wait_deliver
    private LinearLayout llWaitReceive;

    private TextView tvWaitNon;
    // lv_wait_deliver
    private ListView lvWaitReceive;
    private List<TbFood> foods;         // 需要装配的数据源

    private RoundRectImageView rrivFoodPic;
    private TextView tvFoodName;
    private CircleImageView civMerIcon;
    private TextView tvMerName;
    private EditText etFoodBuy;
    private int uId;

    private final String QUERY_WAIT_CONFIRM = HttpURL.IP_ADDRESS + "/order/queryWait.json";

    private ProgressDialog progressDialog;

    public WaitReceivePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wait_receive_page, container, false);
        llWaitReceive = view.findViewById(R.id.ll_wait_receive);
        tvWaitNon = view.findViewById(R.id.tv_wait_non);
        lvWaitReceive = view.findViewById(R.id.lv_wait_receive);
        initView();
        TbUser user = UserUtils.readLoginInfo(Objects.requireNonNull(getContext()));
        if (user.getuId() != 0) {
            uId = user.getuId();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initDate();
    }

    private void initDate() {
        foods = new ArrayList<>();
        String body = "uId=" +  uId + "&oState=" + 7;
        new WaitConfirmTask().execute(QUERY_WAIT_CONFIRM, body);
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
                llWaitReceive.removeView(tvWaitNon);
                lvWaitReceive.setAdapter(new WaitConfirmAdapter());
                lvWaitReceive.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        showPopupMenu(view);
                    }
                });
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
            etFoodBuy.setVisibility(View.INVISIBLE);
            return convertView;
        }
    }

    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.receive, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_receive:
                        new CommonDialog(getContext(), R.style.DialogTheme, "是否接收订单?", new CommonDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
                                    // 修改订单状态
                                }
                                dialog.dismiss();
                            }
                        }).setTitle("提示").show();
                        break;

                }

                //Toast.makeText(getApplicationContext(), item.get, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                // Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
            }
        });

        popupMenu.show();
    }

}
