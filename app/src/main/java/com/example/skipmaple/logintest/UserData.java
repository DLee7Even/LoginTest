package com.example.skipmaple.logintest;

/**
 * Created by SkipMaple on 2018/2/9.
 * 用户数据
 */

public class UserData {
    private String userName;
    private String userPwd;
    private int userId;
    public  int pwdresetFlag = 0;

    public UserData(String userName, String userPwd) {
        super();
        this.userName = userName;
        this.userPwd = userPwd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
