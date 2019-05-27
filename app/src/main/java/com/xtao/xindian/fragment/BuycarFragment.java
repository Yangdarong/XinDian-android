package com.xtao.xindian.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xtao.xindian.R;
import com.xtao.xindian.activities.BuycarSettleActivity;
import com.xtao.xindian.common.OrderFoodsResultType;
import com.xtao.xindian.common.task.BitmapTask;
import com.xtao.xindian.common.task.BuycarCostUpdateTask;
import com.xtao.xindian.common.task.FoodNcvAddTask;
import com.xtao.xindian.common.task.FoodNcvSubTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.dialog.CommonDialog;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BuycarFragment extends Fragment {

    // UI 控件
    private TextView tvBuyCarTitle;
    private TextView tvBuyCarDelete;
    private TextView tvBuyCarNonFoods;

    // 核心控件
    private ExpandableListView elvBuyCarList;
    private BuyCarListAdapter adapter;

    private CheckBox cbBuyCarSelectAll;
    private TextView tvBuyCarMoney;
    private EditText etBuyCarBuy;

    private LinearLayout myLayout;

    private List<TbOrder> orders;
    private List<TbOrderFood> orderFoods;

    private List<TbMer> groupList = new ArrayList<>();
    private List<List<TbOrderFood>> childList = new ArrayList<>();
    private List<List<Boolean>> childCheckBox;

    private ProgressDialog progressDialog;

    private final String QUERY_BUY_CAR = HttpURL.IP_ADDRESS + "/order/queryBuyCar.json";
    private final String DELETE_BUY_CAR = HttpURL.IP_ADDRESS + "/order/deleteBuyCar.json";

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

    private List<TbOrderFood> deleteOrderFoods;
    private List<TbOrder> deleteOrders;

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

        adapter = new BuyCarListAdapter();
        initView(view);
        //initData();
        initListener();
        return view;
    }
    private boolean flag = false;
    private void initListener() {
        if (groupList == null && childList == null) {
            cbBuyCarSelectAll.setVisibility(View.INVISIBLE);
        } else {
            cbBuyCarSelectAll.setVisibility(View.VISIBLE);
        }

        etBuyCarBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSettle();

            }
        });
        cbBuyCarSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag) {
                    flag = true;
                    modifyChildCheckList(flag);
                    tvBuyCarDelete.setVisibility(View.VISIBLE);

                } else {
                    flag = false;
                    modifyChildCheckList(flag);
                    tvBuyCarDelete.setVisibility(View.INVISIBLE);
                }
                adapter.notifyDataSetChanged();
            }
        });

        // 删除选中的
        tvBuyCarDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommonDialog(getContext(), R.style.DialogTheme, "确定要删除选中的菜品吗?", new CommonDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            // 1. 移除选中项
                            // 1.1 如果子项目被删除，父项目也会被删除
                            deleteOrderFoods = new ArrayList<>();
                            deleteOrders = new ArrayList<>();
                            deleteChildWithGroup();


                            // 2 发送网络请求
                            // 2.1 封装成JSON字符串
                            String json = packageJsonCode(deleteOrders, deleteOrderFoods);
                            // 2.2 异步发送网络数据
                            new CommonTask().execute(DELETE_BUY_CAR, json);
                            // 2.3 更新价格
                            new BuycarCostUpdateTask().execute(tvBuyCarMoney, user.getuId());
                            dialog.dismiss();

                        }
                    }
                }).setTitle("删除菜品").show();

            }
        });
    }

    /**
     *
     * @param deleteOrders
     * @param deleteOrderFoods
     * @return
     */
    private String packageJsonCode(List<TbOrder> deleteOrders, List<TbOrderFood> deleteOrderFoods) {
        OrderFoodsResultType resultType = new OrderFoodsResultType();
        resultType.setState(1);
        resultType.setOrders(deleteOrders);
        resultType.setOrderFoods(deleteOrderFoods);
        resultType.setMessage("这是需要删除的信息");

        Gson gson = new Gson();
        return gson.toJson(resultType);
    }


    /**
     * 删除对应的子项,如果没有子项,父项一并删除

     */
    private void deleteChildWithGroup() {
        while (true) {
            List<Map<Integer, Integer>> deletes = getSelect();
            if (deletes == null || deletes.size() == 0) {
                break;
            } else {
                Map<Integer, Integer> map = deletes.get(0);
                for (int groupPosition : map.keySet()) {
                    int a = map.get(groupPosition);
                    TbOrderFood orderFood = childList.get(groupPosition).get(a);
                    deleteOrderFoods.add(orderFood);
                    childList.get(groupPosition).remove(a);
                    childCheckBox.get(groupPosition).remove(a);

                    if (childList.get(groupPosition).size() == 0) {
                        TbOrder order = new TbOrder();
                        order.setoId(orderFood.getOrder().getoId());
                        deleteOrders.add(order);
                        childList.remove(groupPosition);
                        childCheckBox.remove(groupPosition);
                        groupList.remove(groupPosition);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * 获取购物车列表中被选中的项目,刷新
     * @return
     */
    private List<Map<Integer, Integer>> getSelect() {
        List<Map<Integer, Integer>> list = new ArrayList<>();
        for (int i = 0; i < childCheckBox.size(); i++) {
            for (int j = 0; j < childCheckBox.get(i).size() ; j++) {
                Map<Integer, Integer> map = new HashMap<>();
                if(childCheckBox.get(i).get(j) == Boolean.TRUE) {

                    map.put(i, j);
                    list.add(map);
                }
            }
        }

        return list;
    }

    /**
     * 增加判断当前是否有食物
     */
    private void toSettle() {
        if (childList.size() == 0) {    // 没有可以结算的菜品
            Toast.makeText(getContext(), "购物车没有内容", Toast.LENGTH_SHORT).show();
        } else {
            new CommonDialog(getContext(), R.style.DialogTheme, "是否确认订单,并进入结算?", new CommonDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        Intent intent = new Intent(getActivity(), BuycarSettleActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("uId", user.getuId());
                        intent.putExtras(bundle);
                        startActivity(intent);
                        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                    dialog.dismiss();
                }
            }).setTitle("提示").show();
        }
    }

    private void modifyChildCheckList(boolean flag) {
        for (int i = 0; i < childCheckBox.size(); i++) {
            for (int j = 0; j < childCheckBox.get(i).size() ; j++) {
                childCheckBox.get(i).set(j, flag);
            }
        }
    }

    private boolean allAreTrueChildCheckList() {
        for (int i = 0; i < childCheckBox.size(); i++) {
            for (int j = 0; j < childCheckBox.get(i).size() ; j++) {
                if(childCheckBox.get(i).get(j) == Boolean.FALSE) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean existTrueChildCheckList() {
        for (int i = 0; i < childCheckBox.size(); i++) {
            for (int j = 0; j < childCheckBox.get(i).size() ; j++) {
                if(childCheckBox.get(i).get(j) == Boolean.TRUE) {
                    return true;
                }
            }
        }

        return false;
    }

    private void initView(View view) {
        tvBuyCarTitle = view.findViewById(R.id.tv_buycar_title);
        tvBuyCarDelete = view.findViewById(R.id.tv_buycar_delete);
        tvBuyCarNonFoods = view.findViewById(R.id.tv_buycar_nonfoods);

        elvBuyCarList = view.findViewById(R.id.elv_buycar_list);

        cbBuyCarSelectAll = view.findViewById(R.id.cb_buycar_selectall);
        tvBuyCarMoney = view.findViewById(R.id.tv_buycar_money);
        etBuyCarBuy = view.findViewById(R.id.et_buycar_buy);

        myLayout = view.findViewById(R.id.my_layout);

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

        childCheckBox = new ArrayList<>();
        childCheckBox.clear();
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

        childCheckBox.clear();
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
            final View view = View.inflate(getContext(), R.layout.layout_food_mer, null);
            picBuyCarFoodUrl = view.findViewById(R.id.pic_buycar_food_url);
            String foodIconUrl = childList.get(groupPosition).get(childPosition).getFood().getfUrl();
            if (ValueUtils.isNull(foodIconUrl)) {
                new BitmapTask().execute(picBuyCarFoodUrl, HttpURL.FOOD_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(picBuyCarFoodUrl, foodIconUrl);
            }
            tvBuyCarFoodName = view.findViewById(R.id.tv_buycar_food_name);
            tvBuyCarFoodName.setText(childList.get(groupPosition).get(childPosition).getFood().getfName());

            tvBuyCarFoodDprice = view.findViewById(R.id.tv_buycar_food_dprice);
            tvBuyCarFoodDprice.setText("￥" + childList.get(groupPosition).get(childPosition).getFood().getfDPrice());
            nvcBuyCarFoodItem = view.findViewById(R.id.ncv_buycar_food_item);
            nvcBuyCarFoodItem.setValue(childList.get(groupPosition).get(childPosition).getOfAmount());
            nvcBuyCarFoodItem.setValueChangeListener(new NumberControllerView.onNumChangedListener() {
                @Override
                public void addValueListener(View v, int value) {
                    progressDialog.show();
                    new FoodNcvAddTask().execute(childList.get(groupPosition).get(childPosition));
                    progressDialog.dismiss();
                    // 更新价格
                    new BuycarCostUpdateTask().execute(tvBuyCarMoney, user.getuId());
                }

                @Override
                public void subValueListener(View v, int value) {
                    progressDialog.show();
                    new FoodNcvSubTask().execute(childList.get(groupPosition).get(childPosition));
                    progressDialog.dismiss();
                    // 更新价格
                    new BuycarCostUpdateTask().execute(tvBuyCarMoney, user.getuId());
                }
            });
            CheckBox cbBuycarSelect = view.findViewById(R.id.cb_buycar_select);
            cbBuycarSelect.setChecked(getCheck(groupPosition, childPosition));
            cbBuycarSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 更新状态
                    updateCheck(groupPosition, childPosition);
//                    Toast.makeText(getActivity(), "你选择:" + groupPosition + ":" + childPosition + "=" + getCheck(groupPosition, childPosition), Toast.LENGTH_SHORT).show();
                    if (existTrueChildCheckList()) {    // 至少有一个是真的
                        tvBuyCarDelete.setVisibility(View.VISIBLE);
                        if (allAreTrueChildCheckList()) {   // 都是真的
                            cbBuyCarSelectAll.setChecked(true);
                            flag = true;
                        } else {    // 至少有一个是假的
                            cbBuyCarSelectAll.setChecked(false);
                            flag = false;
                        }
                    } else {    // 都不为真
                        tvBuyCarDelete.setVisibility(View.INVISIBLE);
                        cbBuyCarSelectAll.setChecked(false);
                        flag = false;
                    }
                }
            });

            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
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
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();

        }

        @Override
        protected String doInBackground(String... strings) {
            return HttpURL.getCommonResultType(strings[0], strings[1], getContext());
        }
    }

    private class BuyCarAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (orderFoods.size() != 0) {
                // 消除控件
                myLayout.removeView(tvBuyCarNonFoods);

                adapter = new BuyCarListAdapter();
                elvBuyCarList.setAdapter(adapter);
                // 自动展开
                for (int i = 0; i < groupList.size(); i++) {
                    elvBuyCarList.expandGroup(i);
                }
                elvBuyCarList.setGroupIndicator(null);

                // 更新金额
                tvBuyCarMoney.setText("￥".concat(String.format("%.2f", cost)));

            } else {

            }

            progressDialog.dismiss();
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
                        childCheckBox = new ArrayList<>();
                        for (int i = 0; i < orders.size(); i++) {
                            List<TbOrderFood> foods = new ArrayList<>();
                            List<Boolean> checkList = new ArrayList<>();
                            for (int j = 0; j < orderFoods.size(); j++) {
                                // 更新总金额
                                 if (orders.get(i).getoId() == orderFoods.get(j).getOrder().getoId()) {
                                     foods.add(orderFoods.get(j));
                                     cost += orderFoods.get(j).getOfAmount() * orderFoods.get(j).getFood().getfDPrice();

                                    checkList.add(Boolean.FALSE);
                                 }
                            }
                            addData(orders.get(i).getMer(), foods);
                            addCheck(checkList);
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

    private void addCheck(List<Boolean> checkList) {
        childCheckBox.add(checkList);
    }

    private void updateCheck(int groupPosition, int childPosition) {
        Boolean check = childCheckBox.get(groupPosition).get(childPosition);
        childCheckBox.get(groupPosition).set(childPosition, check == Boolean.FALSE);
    }

    private boolean getCheck(int groupPosition, int childPosition) {
        return childCheckBox.get(groupPosition).get(childPosition);
    }
}
