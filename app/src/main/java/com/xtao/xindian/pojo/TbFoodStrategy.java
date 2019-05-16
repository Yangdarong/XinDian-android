package com.xtao.xindian.pojo;

public class TbFoodStrategy {

    private int fsId;
    private int fId;
    private int sId;

    private TbFood food;
    private TbStrategy strategy;

    public TbFoodStrategy() {
    }

    public int getFsId() {
        return fsId;
    }

    public void setFsId(int fsId) {
        this.fsId = fsId;
    }

    public int getfId() {
        return fId;
    }

    public void setfId(int fId) {
        this.fId = fId;
    }

    public int getsId() {
        return sId;
    }

    public void setsId(int sId) {
        this.sId = sId;
    }

    public TbFood getFood() {
        return food;
    }

    public void setFood(TbFood food) {
        this.food = food;
    }

    public TbStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(TbStrategy strategy) {
        this.strategy = strategy;
    }
}
