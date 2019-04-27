package com.xtao.xindian.pojo;

import java.util.List;

/**
 * 菜品种类
 */
public class TbFoodType {

    private int ftId;
    private String ftName;

    public TbFoodType(int ftId, String ftName) {
        this.ftId = ftId;
        this.ftName = ftName;
    }

    public TbFoodType() {
    }

    public int getFtId() {
        return ftId;
    }

    public void setFtId(int ftId) {
        this.ftId = ftId;
    }

    public String getFtName() {
        return ftName;
    }

    public void setFtName(String ftName) {
        this.ftName = ftName;
    }

}
