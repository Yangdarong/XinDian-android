package com.xtao.xindian.fragment.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class HomeNavAdapter extends PagerAdapter {

    private List<ImageView> images;
    private ViewPager pager;

    public HomeNavAdapter(List<ImageView> images, ViewPager pager) {
        this.images = images;
        this.pager = pager;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // 把position 对应位置的ImageView添加到ViewPager中
        ImageView iv = images.get(position % images.size());
        pager.addView(iv);

        return iv;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        // 把 ImageView 从ViewPager 中移除
        pager.removeView(images.get(position % images.size()));
    }
}
