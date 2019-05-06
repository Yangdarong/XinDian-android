package com.xtao.xindian.common;

import com.xtao.xindian.pojo.TbFood;
import com.xtao.xindian.pojo.TbMer;

import java.util.List;

public class MerFoodsResultType {
    private int state;
    private TbMer mer;
    private List<TbFood> foods;


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public TbMer getMer() {
        return mer;
    }

    public void setMer(TbMer mer) {
        this.mer = mer;
    }

    public List<TbFood> getFoods() {
        return foods;
    }

    public void setFoods(List<TbFood> foods) {
        this.foods = foods;
    }
}
