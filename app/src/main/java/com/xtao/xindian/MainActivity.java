package com.xtao.xindian;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xtao.xindian.fragment.FoodFragment;
import com.xtao.xindian.fragment.HomeFragment;
import com.xtao.xindian.fragment.InfoFragment;
import com.xtao.xindian.fragment.StarFragment;

public class MainActivity extends AppCompatActivity {

    private FrameLayout flContainer;

    private RadioGroup rbTabs;
    private RadioButton rbHome;
    private RadioButton rbStar;
    private RadioButton rbFood;
    private RadioButton rbInfo;

    private FragmentManager mFragmentManager;
    private Fragment mHomeFragment;
    private Fragment mStarFragment;
    private Fragment mFoodFragment;
    private Fragment mInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        mFragmentManager = getSupportFragmentManager();
        // 默认首页被选中(将Action先隐藏)
        switchFragment(mHomeFragment);
        // 点击RadioGroup切换页面
        rbTabs.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    // 设置底部导航栏监听事件
    RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId) {
                case R.id.home_tab:

                    switchFragment(mHomeFragment);
                    break;
                case R.id.star_tab:
                    switchFragment(mStarFragment);
                    break;
                case R.id.food_tab:
                    switchFragment(mFoodFragment);
                    break;
                case R.id.info_tab:
                    switchFragment(mInfoFragment);
                    break;
            }
        }
    };

    /**
     * 切换Fragment的方法
     * @param fragment
     */
    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    // 初始化页面控件对象
    private void initView() {
        flContainer = findViewById(R.id.fragment_container);
        rbHome = findViewById(R.id.home_tab);
        rbStar = findViewById(R.id.star_tab);
        rbFood = findViewById(R.id.food_tab);
        rbInfo = findViewById(R.id.info_tab);
        rbTabs = findViewById(R.id.tabs_rg);

        mHomeFragment = new HomeFragment();
        mStarFragment = new StarFragment();
        mFoodFragment = new FoodFragment();
        mInfoFragment = new InfoFragment();
    }
}
