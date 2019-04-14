package com.xtao.xindian;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
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
    private FragmentTransaction transaction;
    private Fragment mHomeFragment;
    private Fragment mStarFragment;
    private Fragment mFoodFragment;
    private Fragment mInfoFragment;

    private Fragment mFragmentText;
    private SparseArray<Fragment> mFragments;

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

    private int mCheckId;

    // 设置底部导航栏监听事件
    RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            /*switch (checkedId) {
                case R.id.home_tab:
                    //switchFragment(mHomeFragment);
                    break;
                case R.id.star_tab:
                    //switchFragment(mStarFragment);
                    break;
                case R.id.food_tab:
                    //switchFragment(mFoodFragment);
                    break;
                case R.id.info_tab:
                    //switchFragment(mInfoFragment);
                    break;
            }*/

            mCheckId = checkedId;
            addFragment(mCheckId);
            // 前一个fragment
            mFragmentText = createFragment(mCheckId);

        }
    };

    private void addFragment(int checkId) {
        // 如果前一个fragment实例不为空
        transaction = mFragmentManager.beginTransaction();
        if (mFragmentText != null) {
            // 且现有fragment 实例被添加过
            if (createFragment(checkId).isAdded()) {
                // 隐藏前一个实例
                transaction
                        .hide(mFragmentText)
                        .show(createFragment(checkId))
                        .commit();
            } else {    // 隐藏钱一个实例,添加现有的实例

                transaction
                        .hide(mFragmentText)
                        .add(R.id.fragment_container, createFragment(checkId))
                        .commit();

            }
        } else {
            transaction
                    .add(R.id.fragment_container, createFragment(checkId))
                    .commit();
        }
    }

    // 切换
    Fragment fragment = null;

    private Fragment createFragment(int checkId) {
        switch (checkId) {
            case R.id.home_tab:
                fragment = mFragments.get(0);
                break;
            case R.id.star_tab:
                fragment = mFragments.get(1);
                //switchFragment(mStarFragment);
                break;
            case R.id.food_tab:
                fragment = mFragments.get(2);
                //switchFragment(mFoodFragment);
                break;
            case R.id.info_tab:
                fragment = mFragments.get(3);
                //switchFragment(mInfoFragment);
                break;
        }
        return fragment;
    }

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
        mFragments = new SparseArray();

        mFragments.put(0, mHomeFragment);
        mFragments.put(1, mStarFragment);
        mFragments.put(2, mFoodFragment);
        mFragments.put(3, mInfoFragment);

    }
}
