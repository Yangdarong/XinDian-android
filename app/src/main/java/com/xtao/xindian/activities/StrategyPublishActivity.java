package com.xtao.xindian.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
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
import com.xtao.xindian.common.FoodsResultType;
import com.xtao.xindian.common.StrategiesResultType;
import com.xtao.xindian.common.task.BitmapTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.dialog.CommonDialog;
import com.xtao.xindian.pojo.TbFood;
import com.xtao.xindian.pojo.TbStrategy;
import com.xtao.xindian.utils.ValueUtils;
import com.xtao.xindian.view.CircleImageView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StrategyPublishActivity extends AppCompatActivity {

    // et_strategy_title
    private EditText etStrategyTitle;
    // et_strategy_content
    private EditText etStrategyContent;
    // iv_strategy_add
    private ImageView ivStrategyAdd;
    // lv_strategy_list
    private ListView lvStrategyList;
    // tv_strategy_ok
    private TextView tvStrategyOK;
    // iv_strategy_bk
    private ImageView ivStrategyBk;

    private ProgressDialog progressDialog;

    // 网络请求URL
    private final String CREATE_DELICATE_STRATEGY = HttpURL.IP_ADDRESS + "/strategy/createStrategy.json";
    private final String CANCEL_DELICATE_STRATEGY = HttpURL.IP_ADDRESS + "/strategy/cancelStrategy.json";
    private final String UPDATE_FOOD_STRATEGY_LIST = HttpURL.IP_ADDRESS + "/strategy/updateMyNewStrategy.json";
    private final String REMOVE_FOOD_FROM_LIST = HttpURL.IP_ADDRESS + "/strategy/removeFoodFromStrategy.json";
    private final String PUBLISH_NEW_STRATEGY = HttpURL.IP_ADDRESS + "/strategy/publishStrategy.json";

    // 美食攻略主键
    private int sId;

    // 装配的数据
    private List<TbFood> foodList;
    // 相应装配的组件
    private CircleImageView civStrategyEditPic;
    private TextView tvStrategyEditName;
    private ImageView ivStrategyEditSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy_publish);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        etStrategyTitle = findViewById(R.id.et_strategy_title);
        etStrategyContent = findViewById(R.id.et_strategy_content);
        ivStrategyAdd = findViewById(R.id.iv_strategy_add);
        lvStrategyList = findViewById(R.id.lv_strategy_list);
        tvStrategyOK = findViewById(R.id.tv_strategy_ok);
        ivStrategyBk = findViewById(R.id.iv_strategy_bk);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在加载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int uId = bundle.getInt("uId");
            if (uId != 0) {
                String body = "uId=" + uId;
                new CreateNewStrategyTask().execute(CREATE_DELICATE_STRATEGY, body);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        doReFreshen();
    }

    private void doReFreshen() {
        // 刷新界面
        String body = "sId=" + sId;
        foodList = new ArrayList<>();
        new UpdateMyNewStrategyTask().execute(UPDATE_FOOD_STRATEGY_LIST, body);
    }

    private void initListener() {
        ivStrategyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(ivStrategyAdd);
            }
        });
        ivStrategyBk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommonDialog(StrategyPublishActivity.this, R.style.DialogTheme, "您的编辑将不会保存,确定取消发布吗？", new CommonDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {  //
                            String body = "sId=" + sId;
                            new CommonTask().execute(CANCEL_DELICATE_STRATEGY, body);
                            finish();
                        }
                    }
                }).setTitle("警告").show();
            }
        });

        tvStrategyOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommonDialog(StrategyPublishActivity.this, R.style.DialogTheme, "即将发布你的美食记录,点击确定以继续。", new CommonDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {  //
                            String title = etStrategyTitle.getText().toString();
                            String content = etStrategyContent.getText().toString();
                            if (ValueUtils.isNull(title) || ValueUtils.isNull(content)) {
                                // 任意一个为空,不允许通过
                                new CommonDialog(StrategyPublishActivity.this, R.style.DialogTheme, "标题或正文为空,请重新确认!", new CommonDialog.OnCloseListener() {
                                    @Override
                                    public void onClick(Dialog dialog, boolean confirm) {
                                        dialog.dismiss();
                                    }
                                }).setTitle("错误").show();
                            } else {
                                TbStrategy strategy = new TbStrategy();
                                strategy.setsId(sId);
                                strategy.setsContext(content);
                                strategy.setsName(title);
                                List<TbStrategy> strategies = new ArrayList<>();
                                strategies.add(strategy);

                                StrategiesResultType resultType = new StrategiesResultType();
                                resultType.setState(1);
                                resultType.setStrategies(strategies);
                                resultType.setMessage("传输新的美食攻略");

                                Gson gson = new Gson();
                                String json = gson.toJson(resultType);

                                new PublishTask().execute(PUBLISH_NEW_STRATEGY, json);
                            }

                            dialog.dismiss();
                        }
                    }
                }).setTitle("提示").show();
            }
        });
    }

    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_page_search:
                        // 跳转到搜索界面
                        Intent intent = new Intent(StrategyPublishActivity.this, StrategySearchActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("sId", sId);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case R.id.action_early_buy:
                        // 跳转到最近购买
                        Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_my_collection:
                        // 跳转到我的收藏
                        Toast.makeText(getApplicationContext(), "3", Toast.LENGTH_SHORT).show();
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

    private class PublishTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            new CommonDialog(StrategyPublishActivity.this, R.style.DialogTheme, "发布成功,即将退出当前界面", new CommonDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    finish();
                }
            }).setTitle("提示").show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return HttpURL.getCommonResultType(strings[0], strings[1], StrategyPublishActivity.this);
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
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            return HttpURL.getCommonResultType(strings[0], strings[1], StrategyPublishActivity.this);
        }
    }

    private class UpdateMyNewStrategyTask extends AsyncTask<String, Integer, List<TbFood>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<TbFood> tbFoods) {
            super.onPostExecute(tbFoods);
            foodList = tbFoods;
            lvStrategyList.setAdapter(new EditFoodListAdapter());
            progressDialog.dismiss();
        }

        @Override
        protected List<TbFood> doInBackground(String... strings) {
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

                    FoodsResultType resultType = gson.fromJson(jsonCode, FoodsResultType.class);
                    if (resultType.getState() == 1) {   // 找寻到标题信息

                        return resultType.getFoods();
                    } else {    // 没有相关的标题
                        Looper.prepare();
                        Toast.makeText(StrategyPublishActivity.this, "服务器数据丢失，请重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } else {    // 网络错误
                    Looper.prepare();
                    Toast.makeText(StrategyPublishActivity.this, "无法连接到服务器,请重试", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class CreateNewStrategyTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            sId = integer;
            progressDialog.dismiss();
        }

        @Override
        protected Integer doInBackground(String... strings) {
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

                    StrategiesResultType resultType = gson.fromJson(jsonCode, StrategiesResultType.class);
                    if (resultType.getState() == 1) {   // 找寻到标题信息
                        List<TbStrategy> strategies = resultType.getStrategies();

                        return strategies.get(0).getsId();
                    } else {    // 没有相关的标题
                        Looper.prepare();
                        Toast.makeText(StrategyPublishActivity.this, "服务器数据丢失，请重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } else {    // 网络错误
                    Looper.prepare();
                    Toast.makeText(StrategyPublishActivity.this, "无法连接到服务器,请重试", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class EditFoodListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return foodList.size();
        }

        @Override
        public Object getItem(int position) {
            return foodList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(StrategyPublishActivity.this, R.layout.layout_strategy_edit, null);
            civStrategyEditPic = convertView.findViewById(R.id.civ_strategy_edit_pic);
            String url = foodList.get(position).getfUrl();
            if (url == null) {
                new BitmapTask().execute(civStrategyEditPic, HttpURL.FOOD_DEFAULT_PIC);
            } else {
                new BitmapTask().execute(civStrategyEditPic, url);
            }
            tvStrategyEditName = convertView.findViewById(R.id.tv_strategy_edit_name);
            tvStrategyEditName.setText(foodList.get(position).getfName());
            ivStrategyEditSub = convertView.findViewById(R.id.iv_strategy_edit_sub);
            ivStrategyEditSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CommonDialog(StrategyPublishActivity.this, R.style.DialogTheme, "是否要删除这条菜品信息?", new CommonDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                String body = "sId=" + sId + "&fId=" + foodList.get(position).getfId();
                                new CommonTask().execute(REMOVE_FOOD_FROM_LIST, body);
                                doReFreshen();
                            }
                            dialog.dismiss();
                        }
                    }).setTitle("警告").show();
                }
            });

            return convertView;
        }
    }
}
