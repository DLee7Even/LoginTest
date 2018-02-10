package com.example.skipmaple.logintest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by SkipMaple on 2018/2/10.
 */

public class User extends AppCompatActivity {
    private Button mReturnButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);
        mReturnButton = (Button) findViewById(R.id.returnback);
    }

    public void back_to_login(View view) {
        Intent intent_Login = new Intent(User.this, Login.class);
        startActivity(intent_Login);
        finish();
    }
}
