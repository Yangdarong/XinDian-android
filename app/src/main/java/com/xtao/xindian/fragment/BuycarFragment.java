package com.xtao.xindian.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.R;
import com.xtao.xindian.common.OrderFoodsResultType;
import com.xtao.xindian.common.task.BitmapTask;
import com.xtao.xindian.common.task.FoodNcvAddTask;
import com.xtao.xindian.common.task.FoodNcvSubTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.pojo.TbFood;
import com.xtao.xindian.pojo.TbMer;
import com.xtao.xindian.pojo.TbOrder;
import com.xtao.xindian.pojo.TbOrderFood;
import com.xtao.xindian.pojo.TbUser;
import com.xtao.xindian.utils.UserUtils;
import com.xtao.xindian.utils.ValueUtils;
import com.xtao.xindian.view.CircleImageView;
import com.xtao.xindian.view.NumberControllerView;
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

public class BuycarFragment extends Fragment {

    // UI 控件
    private TextView tvBuyCarTitle;
    private TextView tvBuyCarAdmin;
    private TextView tvBuyCarNonFoods;

    // 核心控件
    private ExpandableListView elvBuyCarList;

    private RadioButton rbBuyCarSelectAll;
    private TextView tvBuyCarDelete;
    private TextView tvBuyCarMoney;
    private EditText etBuyCarBuy;

    private List<TbOrder> orders;
    private List<TbOrderFood> orderFoods;

    private List<TbMer> groupList = new ArrayList<>();
    private List<List<TbOrderFood>> childList = new ArrayList<>();

    private ProgressDialog progressDialog;

    private final String QUERY_BUY_CAR = HttpURL.IP_ADDRESS + "/order/queryBayCar.json";

    private Intent intent;
    private Bundle bundle;
    private TbUser user;

    private CircleImageView picBuyCarMerIcon;
    private TextView tvBuyCarMerName;

    private RoundRectImageView picBuyCarFoodUrl;
    private TextView tvBuyCarFoodName;
    private TextView tvBuyCarFoodDprice;

    private float cost;     // 计算总金额
    private NumberControllerView nvcBuyCarFoodItem;

    public BuycarFragment() {
        // 当类被构造的时候,
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buycar, container, false);

        intent = Objects.requireNonNull(getActivity()).getIntent();
        bundle = intent.getExtras();
        // 获取到用户信息
        if (bundle != null) {
            user = (TbUser) bundle.get("userInformation");
        } else {
            user = UserUtils.readLoginInfo(getActivity());
        }

        initView(view);
        //initData();
        return view;
    }

    private void initView(View view) {
        tvBuyCarTitle = view.findViewById(R.id.tv_buycar_title);
        tvBuyCarAdmin = view.findViewById(R.id.tv_buycar_admin);
        tvBuyCarNonFoods = view.findViewById(R.id.tv_buycar_nonfoods);

        elvBuyCarList = view.findViewById(R.id.elv_buycar_list);

        rbBuyCarSelectAll = view.findViewById(R.id.rb_buycar_selectall);
        tvBuyCarDelete = view.findViewById(R.id.tv_buycar_delete);
        tvBuyCarMoney = view.findViewById(R.id.tv_buycar_money);
        etBuyCarBuy = view.findViewById(R.id.et_buycar_buy);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("正在加载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void initData() {
        cost = 0;
        groupList = new ArrayList<>();
        groupList.clear();
        childList = new ArrayList<>();
        childList.clear();

        orders = new ArrayList<>();
        orders.clear();
        orderFoods = new ArrayList<>();
        orderFoods.clear();
        if (user.getuId() != 0)
            new BuyCarAsyncTask().execute(QUERY_BUY_CAR);
        else {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 重新刷新购物车界面
        initData();
    }

    private class BuyCarListAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        /**
         * 返回对应外层的item里面的列表
         * @param groupPosition
         * @return
         */
        @Override
        public int getChildrenCount(int groupPosition) {
            return childList.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupPosition;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View view = View.inflate(getContext(), R.layout.layout_title_mer, null);
            picBuyCarMerIcon = view.findViewById(R.id.pic_buycar_mer_icon);
            String merIconUrl = groupList.get(groupPosition).getmUrl();
            if (ValueUtils.isNull(merIconUrl)) {
                new BitmapTask().execute(picBuyCarMerIcon, HttpURL.MER_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(picBuyCarMerIcon, merIconUrl);
            }

            tvBuyCarMerName = view.findViewById(R.id.tv_buycar_mer_name);
            tvBuyCarMerName.setText(groupList.get(groupPosition).getmName());

            return view;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = View.inflate(getContext(), R.layout.layout_food_mer, null);
            picBuyCarFoodUrl = view.findViewById(R.id.pic_buycar_food_url);
            String foodIconUrl = childList.get(groupPosition).get(childPosition).getFood().getfUrl();
            if (ValueUtils.isNull(foodIconUrl)) {
                new BitmapTask().execute(picBuyCarFoodUrl, HttpURL.MER_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(picBuyCarFoodUrl, foodIconUrl);
            }
            Log.i("number", "groupPosition: " + groupPosition + " childPosition: " + childPosition);
            tvBuyCarFoodName = view.findViewById(R.id.tv_buycar_food_name);
            tvBuyCarFoodName.setText(childList.get(groupPosition).get(childPosition).getFood().getfName());

            tvBuyCarFoodDprice = view.findViewById(R.id.tv_buycar_food_dprice);
            tvBuyCarFoodDprice.setText("￥" + childList.get(groupPosition).get(childPosition).getFood().getfDPrice());
            nvcBuyCarFoodItem = view.findViewById(R.id.ncv_buycar_food_item);
            nvcBuyCarFoodItem.setValue(childList.get(groupPosition).get(childPosition).getOfAmount());
            nvcBuyCarFoodItem.setValueChangeListener(new NumberControllerView.onNumChangedListener() {

                @Override
                public void addValueListener(View v, int value) {
                    new FoodNcvAddTask().execute(childList.get(groupPosition).get(childPosition));
                    initData();
                }

                @Override
                public void subValueListener(View v, int value) {
                    new FoodNcvSubTask().execute(childList.get(groupPosition).get(childPosition));
                    initData();
                }
            });

            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private class BuyCarAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (orderFoods.size() != 0) {

                elvBuyCarList.setAdapter(new BuyCarListAdapter());
                // 自动展开
                for (int i = 0; i < groupList.size(); i++) {
                    elvBuyCarList.expandGroup(i);
                }
                elvBuyCarList.setGroupIndicator(null);

                // 更新金额
                tvBuyCarMoney.setText("￥" + String.format("%.2f", cost));

            } else {

            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
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
                String body = "uId=" + user.getuId();
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

                    OrderFoodsResultType resultType = gson.fromJson(jsonCode, OrderFoodsResultType.class);
                    if (resultType.getState() == 1) {   // 找寻到标题信息
                        // 将用户实体带到主界面
                        orders = resultType.getOrders();
                        orderFoods = resultType.getOrderFoods();

                        for (int i = 0; i < orders.size(); i++) {
                            List<TbOrderFood> foods = new ArrayList<>();
                            for (int j = 0; j < orderFoods.size(); j++) {
                                // 更新总金额
                                 if (orders.get(i).getoId() == orderFoods.get(j).getOrder().getoId()) {
                                     foods.add(orderFoods.get(j));
                                     cost += orderFoods.get(j).getOfAmount() * orderFoods.get(j).getFood().getfDPrice();
                                 }
                            }
                            addData(orders.get(i).getMer(), foods);
                        }

                        return resultType.getMessage();

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

    private void addData(TbMer mer, List<TbOrderFood> foods) {
        groupList.add(mer);
        childList.add(foods);
    }
}
