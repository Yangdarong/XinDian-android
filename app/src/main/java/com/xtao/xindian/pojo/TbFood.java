package com.xtao.xindian.pojo;

/**
 *  菜品
 */
public class TbFood {

    private int fId;
    private int ftId;
    private int mId;

    private String fName;
    private String fUrl;    // 保存图片地址
    private Float fPrice;
    private Float fDPrice;

    // 外键关联
    private TbFoodType foodType;
    private TbMer mer;

    public TbFood() {
    }

    public TbFoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(TbFoodType foodType) {
        this.foodType = foodType;
    }

    public TbMer getMer() {
        return mer;
    }

    public void setMer(TbMer mer) {
        this.mer = mer;
    }

    public int getfId() {
        return fId;
    }

    public void setfId(int fId) {
        this.fId = fId;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public int getFtId() {
        return ftId;
    }

    public void setFtId(int ftId) {
        this.ftId = ftId;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getfUrl() {
        return fUrl;
    }

    public void setfUrl(String fUrl) {
        this.fUrl = fUrl;
    }

    public Float getfPrice() {
        return fPrice;
    }

    public void setfPrice(Float fPrice) {
        this.fPrice = fPrice;
    }

    public Float getfDPrice() {
        return fDPrice;
    }

    public void setfDPrice(Float fDPrice) {
        this.fDPrice = fDPrice;
    }

}
