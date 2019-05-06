package com.xtao.xindian.common;


import com.xtao.xindian.pojo.TbOrder;
import com.xtao.xindian.pojo.TbOrderFood;

import java.util.List;

public class OrderFoodsResultType {
    private int state;
    private List<TbOrder> orders;
    private List<TbOrderFood> orderFoods;
    private String message;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<TbOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<TbOrder> orders) {
        this.orders = orders;
    }

    public List<TbOrderFood> getOrderFoods() {
        return orderFoods;
    }

    public void setOrderFoods(List<TbOrderFood> orderFoods) {
        this.orderFoods = orderFoods;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
