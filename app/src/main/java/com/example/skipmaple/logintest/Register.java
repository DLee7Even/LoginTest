package com.example.skipmaple.logintest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by SkipMaple on 2018/2/10.
 */

public class Register extends AppCompatActivity implements View.OnClickListener{
    private EditText mAccount;                      //用户名编辑
    private EditText mPwd;                          //密码编辑
    private EditText mPwdCheck;                     //密码确定检查
    private Button mSureButton;                     //确定按钮
    private Button mCancleButton;                   //取消按钮
    private UserDataManager mUserDataManager;       //用户数据管理类

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mAccount = (EditText) findViewById(R.id.resetpwd_edit_name);
        mPwd = (EditText) findViewById(R.id.resetpwd_edit_pwd_old);
        mPwdCheck = (EditText) findViewById(R.id.resetpwd_edit_pwd_new);

        mSureButton = (Button) findViewById(R.id.register_btn_sure);
        mCancleButton = (Button) findViewById(R.id.register_btn_cancel);

        mSureButton.setOnClickListener(this);
        mCancleButton.setOnClickListener(this);

        if (mUserDataManager == null) {
            //建立本地数据库
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_btn_sure:
                //确认按钮监听事件
                register_check();
                break;
            case R.id.register_btn_cancel:
                //取消按钮监听事件，由注册页面返回登陆页面
                Intent intent_register_to_login = new Intent(Register.this,
                        Login.class);
                startActivity(intent_register_to_login);
                finish();
                break;
            default:
                break;
        }
    }

    //确认按钮的监听事件
    private void register_check() {
        if (isUserNameAndPwdValid()) {
            String userName = mAccount.getText().toString().trim();
            String userPwd = mPwd.getText().toString().trim();
            String userPwdCheck = mPwdCheck.getText().toString().trim();

            //检测新添加用户是否存在
            int count = mUserDataManager.findUserByName(userName);

            //用户已存在时注册失败，并给出提示文字
            if (count > 0) {
                Toast.makeText(this, getString(R.string.name_already_exist,
                        userName), Toast.LENGTH_SHORT).show();
                return;
            }

            //检测两次输入的密码是否一致
            if (userPwd.equals(userPwdCheck) == false) {
                Toast.makeText(this, getString(R.string.pwd_not_the_same),Toast.
                        LENGTH_SHORT).show();
                return;
            } else {
                //新建用户信息
                UserData mUser = new UserData(userName, userPwd);
                mUserDataManager.openDataBase();
                long flag = mUserDataManager.insertUserData(mUser);
                //加入数据库失败
                if (flag == -1) {
                    Toast.makeText(this, getString(R.string.register_fail), Toast.
                            LENGTH_SHORT).show();
                } else {
                    //加入数据库成功，切换至login acitivity
                    Toast.makeText(this, getString(R.string.register_success), Toast.
                            LENGTH_SHORT).show();
                    Intent intent_register_to_login = new Intent(Register.this,
                            Login.class);
                    intent_register_to_login.putExtra("newName", userName);
                    intent_register_to_login.putExtra("newPwd", userPwd);
                    startActivity(intent_register_to_login);
                    finish();
                }
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
}
