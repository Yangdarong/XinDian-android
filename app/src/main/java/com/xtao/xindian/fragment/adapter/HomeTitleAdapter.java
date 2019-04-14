package com.xtao.xindian.fragment.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.xtao.xindian.fragment.homeDetailPage.HomeBakePageFragment;
import com.xtao.xindian.fragment.homeDetailPage.HomeChoicePageFragment;
import com.xtao.xindian.fragment.homeDetailPage.HomeMenuPageFragment;
import com.xtao.xindian.fragment.homeDetailPage.HomeNewplayerPageFragment;
import com.xtao.xindian.fragment.homeDetailPage.HomeNewtastePageFragment;
import com.xtao.xindian.fragment.homeDetailPage.HomeStreamPageFragment;
import com.xtao.xindian.fragment.homeDetailPage.HomeWokersPageFragment;
import com.xtao.xindian.pojo.TbTitle;

import java.util.List;

public class HomeTitleAdapter extends FragmentPagerAdapter {

    private List<TbTitle> titles;

    public HomeTitleAdapter(FragmentManager fm, List<TbTitle> titles) {
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
            case 1: return new HomeNewplayerPageFragment();
            case 2: return new HomeNewtastePageFragment();
            case 3: return new HomeMenuPageFragment();
            case 4: return new HomeBakePageFragment();
            case 5: return new HomeStreamPageFragment();
            case 6: return new HomeWokersPageFragment();
            default:
                return new HomeChoicePageFragment();
        }
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position).gettName();
    }
}
