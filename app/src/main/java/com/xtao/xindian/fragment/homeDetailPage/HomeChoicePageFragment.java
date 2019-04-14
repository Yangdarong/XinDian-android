package com.xtao.xindian.fragment.homeDetailPage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xtao.xindian.R;
import com.xtao.xindian.fragment.adapter.HomeNavAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeChoicePageFragment extends Fragment {

    // vp_home_pic_nav
    private ViewPager vpHomePicNav;
    // tv_home_title_nav
    private TextView tvHomeTitleNav;

    private List<ImageView> mImageList;     // 轮播图片集合
    private String[] mImageTitles;          // 标题集合
    private int previousPosition = 0;       // 前一个被选中的position
    private List<View> mDots;               // 小点

    private boolean isStop = false;         // 线程是否停止
    private static int PAGER_TIME = 5000;   // 间隔时间

    private int[] imgae_ids = new int[]{R.id.pager_image1,R.id.pager_image2,R.id.pager_image3,R.id.pager_image4};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_choice_page, container, false);
        init(view);

        return view;
    }

    private void init(View view) {
        vpHomePicNav = view.findViewById(R.id.vp_home_pic_nav);
        tvHomeTitleNav = view.findViewById(R.id.tv_home_title_nav);
        initData(view); // 初始化数据
        initView(view); // 初始化View 设置适配器
        autoPlayView(view); // 开启线程自动播放
    }

    private void initData(View view) {
        mImageTitles = new String[]{
                "蛋糕1",
                "鱼2",
                "螃蟹3",
                "面条4"};
        int[] imageRess = new int[]{
                R.drawable.pager_image1,
                R.drawable.pager_image2,
                R.drawable.pager_image3,
                R.drawable.pager_image4};

        // 添加图片到图片列表里
        mImageList = new ArrayList<>();
        ImageView iv;

        for (int i = 0; i < imageRess.length; i++) {
            iv = new ImageView(view.getContext());
            iv.setBackgroundResource(imageRess[i]);//设置图片
            iv.setId(imgae_ids[i]);//顺便给图片设置id
            iv.setOnClickListener(new pagerImageOnClick());//设置图片点击事件
            mImageList.add(iv);
        }

        // 开启APP显示第一张图片

        //添加轮播点
        LinearLayout linearLayoutDots =  view.findViewById(R.id.lineLayout_dot);
        mDots = addDots(linearLayoutDots, fromResToDrawable(view.getContext(), R.drawable.point_home_nav),  mImageList.size(), view);//其中fromResToDrawable()方法是我自定义的，目的是将资源文件转成Drawable
    }

    // 图片点击事件
    private class pagerImageOnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pager_image1:
                    Toast.makeText(v.getContext(), "图片1被点击", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.pager_image2:
                    Toast.makeText(v.getContext(), "图片2被点击", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.pager_image3:
                    Toast.makeText(v.getContext(), "图片3被点击", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.pager_image4:
                    Toast.makeText(v.getContext(), "图片4被点击", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /**
     *  第三步、给PagerViw设置适配器，并实现自动轮播功能
     */
    public void initView(View view) {
        HomeNavAdapter adapter = new HomeNavAdapter(mImageList, vpHomePicNav);
        vpHomePicNav.setAdapter(adapter);
        vpHomePicNav.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { //TODO: 不行再改回去
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                //伪无限循环，滑到最后一张图片又从新进入第一张图片
                int newPosition = position % mImageList.size();
                // 把当前选中的点给切换了, 还有描述信息也切换
                tvHomeTitleNav.setText(mImageTitles[newPosition]);//图片下面设置显示文本
                //设置轮播点
                LinearLayout.LayoutParams newDotParams = (LinearLayout.LayoutParams) mDots.get(newPosition).getLayoutParams();
                newDotParams.width = 24;
                newDotParams.height = 24;

                LinearLayout.LayoutParams oldDotParams = (LinearLayout.LayoutParams) mDots.get(previousPosition).getLayoutParams();
                oldDotParams.width = 16;
                oldDotParams.height = 16;

                // 把当前的索引赋值给前一个索引变量, 方便下一次再切换.
                previousPosition = newPosition;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vpHomePicNav.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        isStop = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isStop = true;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        isStop = true;
                        break;
                }
                return false;
            }
        });

        setFirstLocation();
    }

    /**
     *  第四步：设置杠打开APP时候显示的图片和文字
     */
    private void setFirstLocation() {
        tvHomeTitleNav.setText(mImageTitles[previousPosition]);
        // 把ViewPager设置为默认选中Integer.MAX_VALUE / t2，从十几亿次开始轮播图片，达到无限循环目的;
        int m = (Integer.MAX_VALUE / 2) % mImageList.size();
        int currentPosition = Integer.MAX_VALUE / 2 - m;
        vpHomePicNav.setCurrentItem(currentPosition);
    }

    /**
     *  第五步：设置自动播放
     */
    private void autoPlayView(View view) {
        // 自动播放图片
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            vpHomePicNav.setCurrentItem(vpHomePicNav.getCurrentItem() + 1);
                        }
                    });
                    SystemClock.sleep(PAGER_TIME);
                }
            }
        }).start();
    }

    /**
     * 资源图片转 Drawable
     * @param context
     * @param resId
     * @return
     */
    public Drawable fromResToDrawable(Context context, int resId) {
        return context.getResources().getDrawable(resId);
    }

    public int addDot(final LinearLayout linearLayout, Drawable backgount) {
        final View dot = new View(getContext());
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dotParams.width = 16;
        dotParams.height = 16;
        dotParams.setMargins(4,0,4,0);
        dot.setLayoutParams(dotParams);
        dot.setBackground(backgount);
        dot.setId(View.generateViewId());
        linearLayout.addView(dot);
        return dot.getId();
    }

    /**
     * 添加多个轮播小点到横向线性布局
     * @param linearLayout
     * @param backgount
     * @param number
     * @return
     */
    public List<View> addDots(final LinearLayout linearLayout, Drawable backgount, int number, View view){
        List<View> dots = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            int dotId = addDot(linearLayout, backgount);
            dots.add(view.findViewById(dotId));
        }
        return dots;
    }
}
