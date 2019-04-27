package com.xtao.xindian.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xtao.xindian.R;
import com.xtao.xindian.pojo.TbFood;

import java.util.List;

public class BuycarFragment extends Fragment {

    private List<TbFood> buycarList;

    public BuycarFragment() {
        // 当类被构造的时候,
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buycar, container, false);
    }

    @Override
    public void onResume() {

        super.onResume();
        Toast.makeText(getActivity(), "我回来了", Toast.LENGTH_SHORT).show();
    }
}
