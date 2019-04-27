package com.xtao.xindian.common;

import com.xtao.xindian.pojo.TbFood;

import java.util.List;

/**
 *  食物的返回JSON对象格式模板
 */
public class FoodsResultType {

    private int state;  // 1 或 0
    private List<TbFood> foods;

    public FoodsResultType() {
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<TbFood> getFoods() {
        return foods;
    }

    public void setFoods(List<TbFood> foods) {
        this.foods = foods;
    }
}
