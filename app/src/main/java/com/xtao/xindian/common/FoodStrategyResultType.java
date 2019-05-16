package com.xtao.xindian.common;

import com.xtao.xindian.pojo.TbFood;
import com.xtao.xindian.pojo.TbStrategy;

import java.util.List;

public class FoodStrategyResultType {
    private int state;

    private List<TbStrategy> strategies;        // 美食攻略
    private List<List<TbFood>> foods;

    private String message;

    public FoodStrategyResultType() {
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<TbStrategy> getStrategies() {
        return strategies;
    }

    public void setStrategies(List<TbStrategy> strategies) {
        this.strategies = strategies;
    }

    public List<List<TbFood>> getFoods() {
        return foods;
    }

    public void setFoods(List<List<TbFood>> foods) {
        this.foods = foods;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
