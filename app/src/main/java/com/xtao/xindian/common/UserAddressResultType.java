package com.xtao.xindian.common;


import com.xtao.xindian.pojo.TbUserAddress;

import java.util.List;

public class UserAddressResultType {

    private int state;
    private List<TbUserAddress> addresses;

    public UserAddressResultType() {
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<TbUserAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<TbUserAddress> addresses) {
        this.addresses = addresses;
    }

}
