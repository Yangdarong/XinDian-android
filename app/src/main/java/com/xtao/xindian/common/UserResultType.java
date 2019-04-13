package com.xtao.xindian.common;


import com.xtao.xindian.pojo.TbUser;

/**
 *  查询用户的json 返回结果
 */
public class UserResultType {
    private int state;

    private TbUser user;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public TbUser getUser() {
        return user;
    }

    public void setUser(TbUser user) {
        this.user = user;
    }
}
