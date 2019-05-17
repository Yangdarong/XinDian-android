package com.xtao.xindian.fragment.infoDetailPage;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xtao.xindian.R;
import com.xtao.xindian.fragment.adapter.ExpressTitleAdapter;
import com.xtao.xindian.pojo.TbTitle;

import java.util.ArrayList;
import java.util.List;

public class ExpressInfoActivity extends AppCompatActivity {

    private TabLayout tlExpressNav;
    private ViewPager vpExpressTabContent;

    private int uId;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_info);

        Bundle bundle = getIntent().getExtras();
        page = bundle.getInt("page");

        initView();
        initData();
    }

    private void initView() {
        tlExpressNav = findViewById(R.id.tl_express_nav);
        vpExpressTabContent = findViewById(R.id.vp_express_tab_content);
    }

    private void initData() {
        List<TbTitle> titles = new ArrayList<>();
        TbTitle title = new TbTitle();

        title.settName("待提交");
        titles.add(title);

        title.settName("待发货");
        titles.add(title);

        title.settName("待收货");
        titles.add(title);

        vpExpressTabContent.setAdapter(new ExpressTitleAdapter(getSupportFragmentManager(), titles));
        tlExpressNav.setupWithViewPager(vpExpressTabContent);
        vpExpressTabContent.setCurrentItem(page);
    }
}
