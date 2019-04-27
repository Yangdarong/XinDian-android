package com.xtao.xindian.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.xtao.xindian.pojo.TbUser;

public class UserUtils {

    public final static String USER_DEFAULT_PIC = "/upload/users/default.png";

    public static void saveLoginInfo(Context context, TbUser user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("uId", user.getuId());
        editor.putString("uSignature", user.getuSignature());
        editor.putString("uSex", user.getuSex());
        editor.putString("uMail", user.getuMail());
        editor.putString("uPhone", user.getuPhone());
        editor.putString("uHeadPortrait", user.getuHeadPortrait());
        editor.putInt("uUserStateId", user.getuUserStateId());

        editor.apply();
    }

    public static TbUser readLoginInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

        return new TbUser(
                sharedPreferences.getInt("uId", 0),
                sharedPreferences.getString("uSignature", "默认用户名"),
                sharedPreferences.getString("uSex", "未设置"),
                sharedPreferences.getString("uMail", "未设置"),
                sharedPreferences.getString("uPhone", "未设置"),
                sharedPreferences.getString("uHeadPortrait", USER_DEFAULT_PIC),
                sharedPreferences.getInt("uUserStateId", 0)
        );
    }

    public static void removeLoginInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("uId");
        editor.remove("uSignature");
        editor.remove("uSex");
        editor.remove("uMail");
        editor.remove("uPhone");
        editor.remove("uHeadPortrait");
        editor.remove("uUserStateId");

        editor.apply();
    }

    public static boolean isLogined(Context context) {
        return readLoginInfo(context).getuId() != 0;
    }
}
