package com.xtao.xindian.pojo;

public class TbOrderFood {
    private int ofId;
    private int oId;
    private int fId;
    private int ofAmount;

    // 外键
    private TbOrder order;
    private TbFood food;

    public TbOrderFood() {
    }

    public int getOfId() {
        return ofId;
    }

    public void setOfId(int ofId) {
        this.ofId = ofId;
    }

    public int getoId() {
        return oId;
    }

    public void setoId(int oId) {
        this.oId = oId;
    }

    public int getfId() {
        return fId;
    }

    public void setfId(int fId) {
        this.fId = fId;
    }

    public int getOfAmount() {
        return ofAmount;
    }

    public void setOfAmount(int ofAmount) {
        this.ofAmount = ofAmount;
    }

    public TbOrder getOrder() {
        return order;
    }

    public void setOrder(TbOrder order) {
        this.order = order;
    }

    public TbFood getFood() {
        return food;
    }

    public void setFood(TbFood food) {
        this.food = food;
    }
}
