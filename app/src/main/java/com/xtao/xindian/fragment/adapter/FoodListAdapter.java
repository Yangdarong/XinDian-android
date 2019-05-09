package com.xtao.xindian.fragment.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.xtao.xindian.LoginActivity;
import com.xtao.xindian.R;
import com.xtao.xindian.activities.FoodInfoActivity;
import com.xtao.xindian.common.task.BitmapTask;
import com.xtao.xindian.common.value.HttpURL;
import com.xtao.xindian.pojo.TbFood;
import com.xtao.xindian.utils.UserUtils;
import com.xtao.xindian.view.CircleImageView;
import com.xtao.xindian.view.RoundRectImageView;

import java.util.List;
import java.util.Objects;

public class FoodListAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private TbFood food;
    private String picUrl;
    private List<TbFood> foods;
    private View view;
    private Fragment fragement;


    public FoodListAdapter(Context context, List<TbFood> foods, View view, Fragment fragment) {
        mLayoutInflater = LayoutInflater.from(context);
        this.foods = foods;
        this.view = view;
        this.fragement = fragment;
    }

    @Override
    public int getCount() {
        return foods.size();
    }

    @Override
    public Object getItem(int position) {
        return foods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(R.layout.food_item, null);
        food = (TbFood) getItem(position);

        RoundRectImageView picFood = convertView.findViewById(R.id.pic_food);
        // 图片需要下载
        if (food.getfUrl() == null || food.getfUrl().equals(""))  // 未设置图片
            picUrl = HttpURL.FOOD_DEFAULT_PIC;
        else     // 设置了图片
            picUrl = food.getfUrl();
        new BitmapTask().execute(picFood, picUrl);

        TextView tvFoodName = convertView.findViewById(R.id.tv_food_name);
        tvFoodName.setText(food.getfName());

        TextView tvFoodType = convertView.findViewById(R.id.tv_food_type);
        tvFoodType.setText(food.getFoodType().getFtName());

        TextView tvFoodDprice = convertView.findViewById(R.id.tv_food_dprice);
        String oPrice = tvFoodDprice.getText().toString();
        String nPlace = oPrice.replace(oPrice.substring(1, oPrice.length()), food.getfDPrice().toString());

        tvFoodDprice.setText(nPlace);

        CircleImageView picUserIcon = convertView.findViewById(R.id.pic_user_icon);
        if (food.getMer().getmUrl() == null || food.getMer().getmUrl().equals("")) {
            picUrl = HttpURL.MER_DEFAULT_PIC;
        } else {
            picUrl = food.getMer().getmUrl();
        }
        new BitmapTask().execute(picUserIcon, picUrl);

        TextView tvMerName = convertView.findViewById(R.id.tv_mer_name);
        tvMerName.setText(food.getMer().getmName());
        EditText etFoodBuy = convertView.findViewById(R.id.et_food_buy);
        View.OnClickListener doGetFoodInfoListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取菜品ID 存入bundle
                int fId = ((TbFood) getItem(position)).getfId();
                Bundle bundle = new Bundle();
                bundle.putInt("fId", fId);

                // 页面跳转
                if (UserUtils.isLogined(view.getContext())) {
                    Intent intent = new Intent(view.getContext().getApplicationContext(), FoodInfoActivity.class);
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);
                    Objects.requireNonNull(fragement.getActivity()).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else {    // 没有登录
                    Intent intent = new Intent(view.getContext().getApplicationContext(), LoginActivity.class);
                    view.getContext().startActivity(intent);
                }

            }
        };
        etFoodBuy.setOnClickListener(doGetFoodInfoListener);
        return convertView;
    }
}
