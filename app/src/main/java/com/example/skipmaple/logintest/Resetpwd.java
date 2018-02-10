package com.example.skipmaple.logintest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by SkipMaple on 2018/2/10.
 */

public class Resetpwd extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "Resetpwd";
    private EditText mAccount;
    private EditText mPwd_old;
    private EditText mPwd_new;
    private EditText mPwd_check;
    private Button mSureButton;
    private Button mCancleButton;
    private UserDataManager mUserDataManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpwd);

        mAccount = (EditText) findViewById(R.id.resetpwd_edit_name);
        mPwd_old = (EditText) findViewById(R.id.resetpwd_edit_pwd_old);
        mPwd_new = (EditText) findViewById(R.id.resetpwd_edit_pwd_new);
        mPwd_check = (EditText) findViewById(R.id.resetpwd_edit_pwd_check);

        mSureButton = (Button) findViewById(R.id.resetpwd_btn_sure);
        mCancleButton = (Button) findViewById(R.id.resetpwd_btn_cancel);

        mSureButton.setOnClickListener(this);
        mCancleButton.setOnClickListener(this);

        //建立本地数据库
        if (mUserDataManager == null) {
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.resetpwd_btn_sure:
                resetpwd_check();
                break;
            case R.id.resetpwd_btn_cancel:
                //取消按钮监听事件，由修改密码界面返回登陆界面
                Intent intent_resetpwd_to_login = new Intent(Resetpwd.this,
                        Login.class);
                startActivity(intent_resetpwd_to_login);
                finish();
                break;
        }
    }

    //确认按钮的监听事件
    private void resetpwd_check() {
        if (isUserNameAndPwdValid()) {
            String userName = mAccount.getText().toString().trim();
            String userPwd_old = mPwd_old.getText().toString().trim();
            String userPwd_new = mPwd_new.getText().toString().trim();
            String userPwdCheck = mPwd_check.getText().toString().trim();

            //Log.e(TAG, "哇哇哇哇。。。。。");

            //判断数据库中是否存在该用户并核对密码
            int result = mUserDataManager.findUserByNameAndPwd(userName, userPwd_old);

            //用户名和密码均正确，继续进行后续操作
            if (result == 1) {
                if (userPwd_new.equals(userPwdCheck) == false) {
                    //两次输入的新密码不一致
                    Toast.makeText(this, getString(R.string.pwd_not_the_same),
                            Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //进行更改密码的操作
                    UserData mUser = new UserData(userName, userPwd_new);
                    mUserDataManager.openDataBase();
                    boolean flag = mUserDataManager.updateUserData(mUser);
                    if (flag == false) {
                        Toast.makeText(this, getString(R.string.resetpwd_fail),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.resetpwd_success),
                                Toast.LENGTH_SHORT).show();

                        mUser.pwdresetFlag = 1;
                        Intent intent_resetpwd_to_login = new Intent(Resetpwd.this,
                                Login.class);
                        intent_resetpwd_to_login.putExtra("newName", userName);
                        intent_resetpwd_to_login.putExtra("newPwd", userPwd_new);
                        startActivity(intent_resetpwd_to_login);
                        finish();
                    }
                }
            } else if (result == 0) {
                //返回 0 说明用户名和密码不匹配，重新输入
                Toast.makeText(this, getString(R.string.pwd_not_fit_user),
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    public boolean isUserNameAndPwdValid() {
        String userName = mAccount.getText().toString().trim();

        //检测用户是否存在
        int count = mUserDataManager.findUserByName(userName);

        //用户不存在时密码修改失败，并给出提示文字
        if (count <= 0) {
            Toast.makeText(this, getString(R.string.name_not_exist,userName),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        //判断是否输入了空字符
        if (mAccount.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd_old.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd_new.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_new_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd_check.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
