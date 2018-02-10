package com.example.skipmaple.logintest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by SkipMaple on 2018/2/9.
 */

public class Login extends Activity implements View.OnClickListener{
    public int pwdresetFlag = 0;
    private EditText mAccount;      //用户名编辑
    private EditText mPwd;          //密码编辑
    private Button mRegisterButton; //注册按钮
    private Button mLoginButton;    //登录按钮
    private Button mCancleButton;   //注销按钮
    private CheckBox mRememberCheck;//记住密码

    private SharedPreferences login_sp;
    private String userNameValue, passwordValue;
    //private String newPwd;          //更改后的新密码

    private View loginView;         //登录
    private View loginSuccessView;
    private TextView loginSuccessShow;
    private TextView mChangepwdText;
    private UserDataManager mUserDataManager;  //用户数据管理类

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mAccount = (EditText) findViewById(R.id.login_edit_account);
        mPwd = (EditText) findViewById(R.id.login_edit_pwd);
        mRegisterButton = (Button) findViewById(R.id.login_btn_register);
        mLoginButton = (Button) findViewById(R.id.login_btn_login);
        mCancleButton = (Button) findViewById(R.id.login_btn_cancel);
        loginView = findViewById(R.id.login_view);
        loginSuccessView = findViewById(R.id.login_success_view);
        loginSuccessShow = findViewById(R.id.login_success_show);

        mChangepwdText = (TextView) findViewById(R.id.login_text_change_pwd);
        mRememberCheck = (CheckBox) findViewById(R.id.login_remember);

        login_sp = getSharedPreferences("userInfo", 0);
        String name = login_sp.getString("USER_NAME", "");
        String pwd = login_sp.getString("PASSWORD", "");
        boolean choseRemember = login_sp.getBoolean("mRememberCheck", false);
        boolean choseAutoLogin = login_sp.getBoolean("mAutologinCheck", false);     //自动登录判断标识位

        //如果上次选了记住密码，那进入登录页面也自动勾选记住密码，并填上用户名和密码
        if(choseRemember) {
            mAccount.setText(name);
            mPwd.setText(pwd);
            mRememberCheck.setChecked(true);
        }

        //处理更改密码和新注册账号后Login界面的账号和密码自动更新
        Intent intent_resetpwd_back_login = getIntent();
        String newName = intent_resetpwd_back_login.getStringExtra("newName");
        String newPwd = intent_resetpwd_back_login.getStringExtra("newPwd");
        if (newPwd != null) {
            mAccount.setText(newName);
            mPwd.setText(newPwd);
            Log.d("Login", "账号：" + newName + " 新密码是：" + newPwd);
        }

        //采用OnClickListener方法设置不同按钮按下之后的监听事件
        mRegisterButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mCancleButton.setOnClickListener(this);
        mChangepwdText.setOnClickListener(this);

        //使用ImageView显示logo
        ImageView image = (ImageView) findViewById(R.id.logo);
        image.setImageResource(R.drawable.logo);

        if (mUserDataManager == null) {
            //建立本地数据库
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn_register:
                //登录界面的注册按钮
                //切换Login Activity至User Activity
                Intent intent_login_to_register = new Intent(Login.this, Register.class);
                startActivity(intent_login_to_register);
                finish();
                break;
            case R.id.login_btn_login:
                login();
                break;
            case R.id.login_btn_cancel:
                cancle();
                break;
            case R.id.login_text_change_pwd:
                //登录界面修改密码textview
                //切换Login Activity至User Activity
                Intent intent_login_to_reset = new Intent(Login.this, Resetpwd.class);
                //startActivity(intent_login_to_reset);
                startActivityForResult(intent_login_to_reset, 1);
                finish();
                break;
        }
    }

    //登录按钮监听事件
    private void login() {
        if (isUserNameAndPwdValid()) {
            String userName = mAccount.getText().toString().trim();
            String userPwd = mPwd.getText().toString().trim();
            SharedPreferences.Editor editor = login_sp.edit();
            int result = mUserDataManager.findUserByNameAndPwd(userName, userPwd);
            if (result == 1) {
                //返回1说明用户名和密码均正确
                //保存用户名和密码
                editor.putString("USER_NAME", userName);
                editor.putString("PASSWORD", userPwd);

                //是否记住密码
                if (mRememberCheck.isChecked()) {
                    editor.putBoolean("mRememberCheck", true);
                } else {
                    editor.putBoolean("mRememberCheck", false);
                }
                editor.commit();

                //切换Login Activity至User Activity
                Intent intent = new Intent(Login.this, User.class);
                startActivity(intent);
                finish();
                //登录成功提示
                Toast.makeText(this, getString(R.string.login_success),
                        Toast.LENGTH_SHORT).show();
            } else if (result == 0) {
                //登录失败提示
                Toast.makeText(this, getString(R.string.login_fail),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //判断账号和密码是否有效
    private boolean isUserNameAndPwdValid() {
        if (mAccount.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //注销
    private void cancle() {
        if (isUserNameAndPwdValid()) {
            //获取当前输入的用户名和密码信息
            String userName = mAccount.getText().toString().trim();
            String userPwd  = mPwd.getText().toString().trim();
            int result = mUserDataManager.findUserByNameAndPwd(userName, userPwd);
            if (result == 1) {
                //返回1说明用户名和密码均正确
                //注销成功提示
                Toast.makeText(this, getString(R.string.cancel_success),
                        Toast.LENGTH_SHORT).show();
                mPwd.setText("");
                mAccount.setText("");
                mUserDataManager.deleteUserDatabyname(userName);
            } else if (result == 0) {
                //注销失败提示
                Toast.makeText(this,getString(R.string.cancel_fail),
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onResume() {
        if (mUserDataManager == null) {
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mUserDataManager != null) {
            mUserDataManager.closeDataBase();
            mUserDataManager = null;
        }
        super.onPause();
    }
}
