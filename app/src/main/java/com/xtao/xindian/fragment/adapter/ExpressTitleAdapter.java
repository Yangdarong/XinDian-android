package com.xtao.xindian.fragment.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xtao.xindian.fragment.infoDetailPage.WaitConfirmPageFragment;
import com.xtao.xindian.fragment.infoDetailPage.WaitDeliverPageFragment;
import com.xtao.xindian.fragment.infoDetailPage.WaitReceivePageFragment;
import com.xtao.xindian.fragment.starDetailPage.StarDelicateSquarePageFragment;
import com.xtao.xindian.fragment.starDetailPage.StarDelicateStrategyPageFragment;
import com.xtao.xindian.pojo.TbTitle;

import java.util.List;

public class ExpressTitleAdapter extends FragmentPagerAdapter {

    private List<TbTitle> titles;

    public ExpressTitleAdapter(FragmentManager fm, List<TbTitle> titles) {
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
            case 1: return new WaitDeliverPageFragment();
            case 2: return new WaitReceivePageFragment();
            default:
                return new WaitConfirmPageFragment();
        }
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position).gettName();
    }
}
