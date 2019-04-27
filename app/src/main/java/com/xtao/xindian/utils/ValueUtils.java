package com.xtao.xindian.utils;

public class ValueUtils {

    public static boolean isNull(String s) {
        return s == null || s.equals("");
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

}
