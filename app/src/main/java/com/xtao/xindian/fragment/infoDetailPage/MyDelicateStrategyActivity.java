package com.xtao.xindian.fragment.infoDetailPage;

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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xtao.xindian.R;
import com.xtao.xindian.activities.StrategyInfoActivity;
import com.xtao.xindian.common.StrategiesResultType;
import com.xtao.xindian.common.task.CommonTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.dialog.CommonDialog;
import com.xtao.xindian.pojo.TbStrategy;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MyDelicateStrategyActivity extends AppCompatActivity {

    // 必备组件
    private int uId;
    private ImageView ivSysBack;
    private TextView tvTitleName;

    // 页面UI组件
    private ListView lvMyStrategyList;
    private TextView tvStrategyIntroName;
    private TextView tvStrategyIntroTime;

    private final String QUERY_MY_STRATEGIES = HttpURL.IP_ADDRESS + "/strategy/queryStrategiesByUid.json";
    private final String DELETE_MY_STRATEGY = HttpURL.IP_ADDRESS + "/strategy/removeStrategyBySid.json";

    // 数据源
    private List<TbStrategy> strategies;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_delicate_strategy);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            uId = bundle.getInt("uId");
        }
        initView();
        initData();
        initListener();
    }

    private void initView() {
        ivSysBack = findViewById(R.id.iv_sys_back);
        tvTitleName = findViewById(R.id.tv_title_name);
        tvTitleName.setText("我的美食攻略");

        lvMyStrategyList = findViewById(R.id.lv_my_strategy_list);

        progressDialog = new ProgressDialog(MyDelicateStrategyActivity.this);
        progressDialog.setTitle("正在加载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void initData() {
        if (uId != 0) {
            String body = "uId=" + uId;
            new MyStrategiesTask().execute(QUERY_MY_STRATEGIES, body);
        }
    }

    private void initListener() {
        ivSysBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lvMyStrategyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int sId = strategies.get(position).getsId();
                if (sId != 0) {
                    Intent intent = new Intent(MyDelicateStrategyActivity.this, StrategyInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("uId", uId);
                    bundle.putInt("sId", sId);
                    startActivity(intent);
                }
            }
        });

        lvMyStrategyList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupMenu(view);
                return false;
            }
        });
    }

    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.delete, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        // 删除这条记录
                        new CommonDialog(MyDelicateStrategyActivity.this, R.style.DialogTheme, "是否要删除这条攻略?", new CommonDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
                                    new CommonTask().execute();
                                }

                            }
                        }).setTitle("警告").show();
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

    private class MyStrategiesTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (strategies != null) {
                lvMyStrategyList.setAdapter(new MyStrategiesAdapter());
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

                    StrategiesResultType resultType = gson.fromJson(jsonCode, StrategiesResultType.class);
                    if (resultType.getState() == 1) {   // 找寻到标题信息
                        strategies = resultType.getStrategies();

                        return resultType.getMessage();
                    } else {    // 没有相关的标题
                        Looper.prepare();
                        Toast.makeText(MyDelicateStrategyActivity.this, "数据不正确，请重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        return null;
                    }

                } else {    // 网络错误
                    Looper.prepare();
                    Toast.makeText(MyDelicateStrategyActivity.this, "无法连接到服务器,请重试", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class MyStrategiesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return strategies.size();
        }

        @Override
        public Object getItem(int position) {
            return strategies.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(MyDelicateStrategyActivity.this, R.layout.layout_strategy_intro, null);
            tvStrategyIntroName = convertView.findViewById(R.id.tv_strategy_intro_name);
            tvStrategyIntroName.setText(strategies.get(position).getsName());
            Timestamp timestamp = strategies.get(position).getsCreateTime();
            tvStrategyIntroTime = convertView.findViewById(R.id.tv_strategy_intro_time);
            tvStrategyIntroTime.setText(
                    new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", new Locale("zh", "ZH"))
                            .format(timestamp));
            return convertView;
        }
    }


}
