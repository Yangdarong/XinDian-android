package com.xtao.xindian.common;

import com.xtao.xindian.pojo.TbOrder;

public class OrderResultType {

    private int state;
    private TbOrder order;

    public OrderResultType() {
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public TbOrder getOrder() {
        return order;
    }

    public void setOrder(TbOrder order) {
        this.order = order;
    }
}
