package com.xtao.xindian.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.R;
import com.xtao.xindian.common.OrderResultType;
import com.xtao.xindian.common.UserAddressResultType;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.dialog.CommonDialog;
import com.xtao.xindian.pojo.TbOrder;
import com.xtao.xindian.pojo.TbUser;
import com.xtao.xindian.pojo.TbUserAddress;
import com.xtao.xindian.utils.UserUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OrderConfirmActivity extends AppCompatActivity {

    //et_order_confirm_tel
    private EditText etOrderConfirmTel;
    //iv_order_confirm_address
    private ImageView ivOrderConfirmAddress;
    // et_order_confirm_address
    private EditText etOrderConfirmAddress;
//    tv_order_confirm_bk
    private TextView tvOrderConfirmBk;
//    tv_order_confirm_ok
    private TextView tvOrderConfirmOK;
//    lv_order_confirm_lists
    private ListView lvOrderConfirmLists;

    private TbUser user;

    private String oAddress, uPhone;

    // 相关组件
    // tv_order_confirm_address
    private TextView tvOrderConfirmAddress;
    // tv_order_confirm_usual
    private TextView tvOrderConfirmUsual;

    private String body;


    // 网络地址
    // 查询用户地址请求
    private final String QUERY_USER_ADDRESS = HttpURL.IP_ADDRESS + "/user/queryUserAddress.json";
    // 添加用户地址请求
    private final String INSERT_USER_ADDRESS = HttpURL.IP_ADDRESS + "/user/createUserAddress.json";
    // 更新订单信息请求
    private final String UPDATE_ORDER_INFO = HttpURL.IP_ADDRESS + "/order/updateOrderInfo.json";

    // 数据源
    private List<TbUserAddress> addressList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        addressList = new ArrayList<>();
        initView();
        initData();
        initListener();
    }

    private boolean check(String phone, String address) {
        String telRegex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            return false;
        } else {
            return phone.matches(telRegex);
        }

    }

    private void initListener() {


        ivOrderConfirmAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到百度定位
                Intent intent = new Intent(OrderConfirmActivity.this, BaiduMapActivity.class);
                startActivity(intent);
            }
        });

        tvOrderConfirmBk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvOrderConfirmOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断手机号 和地址信息
                uPhone = etOrderConfirmTel.getText().toString();
                oAddress = etOrderConfirmAddress.getText().toString();
                if(check(uPhone, oAddress)) {
                    // 判断地址信息是否被添加
                    boolean flag = false;
                    for (TbUserAddress address : addressList) {
                        if (oAddress.equals(address.getUaAddress())) {
                            flag = true;
                        }
                    }

                    if (!flag) {
                        new CommonDialog(OrderConfirmActivity.this, R.style.DialogTheme, "是否记录新的地址信息?", new CommonDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
//                                    String body = "uId=" + user.getuId() + "&uaAddress=" + oAddress;

                                    String json = toJson();
                                    new CommonTask().execute(INSERT_USER_ADDRESS, json);
                                    dialog.dismiss();
                                }


                            }
                        }).setTitle("提示").show();
                    }
                    // 转化成JSON数据
                    TbOrder order = new TbOrder();
                    order.setuId(user.getuId());
                    order.setoAddress(oAddress);
                    order.setuPhone(uPhone);
                    OrderResultType resultType = new OrderResultType();
                    resultType.setState(1);
                    resultType.setOrder(order);
                    Gson gson = new Gson();
                    body = gson.toJson(resultType);
                    // 执行订单的更新

                    new CommonDialog(OrderConfirmActivity.this, R.style.DialogTheme, "确认提交并支付这笔订单?", new CommonDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                new OrderInfoConfirmTask().execute(UPDATE_ORDER_INFO, body);
                            }
                            dialog.dismiss();
                        }
                    }).setTitle("请确认").show();
                } else {
                    new CommonDialog(OrderConfirmActivity.this, R.style.DialogTheme, "订单信息填写错误,请及时检查", new CommonDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {

                                dialog.dismiss();
                            }
                        }
                    }).setTitle("错误提示").show();
                }
            }
        });
    }

    private String toJson() {
        TbUserAddress address = new TbUserAddress();
        address.setUaAddress(oAddress);
        address.setUaIsUsual(0);
        address.setuId(user.getuId());
        List<TbUserAddress> addresses = new ArrayList<>();
        addresses.add(address);
        UserAddressResultType resultType = new UserAddressResultType();
        resultType.setState(1);
        resultType.setAddresses(addresses);

        Gson gson = new Gson();
        return gson.toJson(resultType);
    }

    private void initData() {
        user = UserUtils.readLoginInfo(getApplicationContext());
        etOrderConfirmTel.setHint("" + user.getuPhone());

        new QueryAddressListTask().execute(QUERY_USER_ADDRESS, "uId=" + user.getuId());
    }

    private void initView() {
        etOrderConfirmTel = findViewById(R.id.et_order_confirm_tel);
        etOrderConfirmAddress = findViewById(R.id.et_order_confirm_address);
        ivOrderConfirmAddress = findViewById(R.id.iv_order_confirm_address);
        tvOrderConfirmBk = findViewById(R.id.tv_order_confirm_bk);
        tvOrderConfirmOK = findViewById(R.id.tv_order_confirm_ok);
        lvOrderConfirmLists = findViewById(R.id.lv_order_confirm_lists);

        progressDialog = new ProgressDialog(OrderConfirmActivity.this);
        progressDialog.setTitle("正在加载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  // 圆形旋转
    }

    private AddressListAdapter mAdapter = new AddressListAdapter();
    private class AddressListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return addressList.size();
        }

        @Override
        public Object getItem(int position) {
            return addressList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(OrderConfirmActivity.this, R.layout.layout_user_address, null);
            tvOrderConfirmAddress = convertView.findViewById(R.id.tv_order_confirm_address);
            tvOrderConfirmAddress.setText(addressList.get(position).getUaAddress());
            tvOrderConfirmAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etOrderConfirmAddress.setText(addressList.get(position).getUaAddress());
                }
            });
            tvOrderConfirmUsual = convertView.findViewById(R.id.tv_order_confirm_usual);
            if (addressList.get(position).getUaIsUsual() == 0) {
                tvOrderConfirmUsual.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    }

    private class OrderInfoConfirmTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            Intent intent = new Intent(OrderConfirmActivity.this, PaySuccessfulActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

        @Override
        protected String doInBackground(String... strings) {
            return HttpURL.getCommonResultType(strings[0], strings[1], OrderConfirmActivity.this);
        }
    }

    private class CommonTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            return HttpURL.getCommonResultType(strings[0], strings[1], OrderConfirmActivity.this);
        }
    }

    private class QueryAddressListTask extends AsyncTask<String, Integer, List<TbUserAddress>> {

        @Override
        protected void onPostExecute(List<TbUserAddress> addresses) {
            super.onPostExecute(addresses);
            addressList = addresses;
            if (addressList != null) {
                lvOrderConfirmLists.setAdapter(mAdapter);
            }
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected List<TbUserAddress> doInBackground(String... strings) {
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
                    Gson gson = new Gson();

                    UserAddressResultType resultType = gson.fromJson(jsonCode, UserAddressResultType.class);
                    if (resultType.getState() == 1) {   // 找寻到标题信息

                        return resultType.getAddresses();

                    } else {    // 没有相关的标题
                        Looper.prepare();
                        Toast.makeText(OrderConfirmActivity.this, "服务器数据丢失，请重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } else {    // 网络错误
                    Looper.prepare();
                    Toast.makeText(OrderConfirmActivity.this, "无法连接到服务器,请重试", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
