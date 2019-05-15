package com.xtao.xindian.fragment.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xtao.xindian.fragment.starDetailPage.StarDelicateSquarePageFragment;
import com.xtao.xindian.fragment.starDetailPage.StarDelicateStrategyPageFragment;
import com.xtao.xindian.pojo.TbTitle;

import java.util.List;

public class StarTitleAdapter extends FragmentPagerAdapter {

    private List<TbTitle> titles;

    public StarTitleAdapter(FragmentManager fm, List<TbTitle> titles) {
        super(fm);
        this.titles = titles;
    }


    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1: return new StarDelicateSquarePageFragment();
            default:
                return new StarDelicateStrategyPageFragment();
        }
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position).gettName();
    }
}
