package com.example.foolishfan.user_v10;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.content.SharedPreferences;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import dao.*;


public class Login extends Activity {                 //登录界面活动

    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private Button mRegisterButton;                   //注册按钮
    private Button mLoginButton;                      //登录按钮
    private CheckBox mRememberCheck;
    public static Person me;
    private View loginView;
    private TextView mChangepwdText;
    private Handler handler1 = new Handler();

    private SharedPreferences userInfo;
    private String userNameValue,passwordValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAccount = (EditText) findViewById(R.id.login_edit_account);
        mPwd = (EditText) findViewById(R.id.login_edit_pwd);
        mRegisterButton = (Button) findViewById(R.id.login_btn_register);
        mLoginButton = (Button) findViewById(R.id.login_btn_login);
        loginView=findViewById(R.id.login_view);
        mChangepwdText = (TextView) findViewById(R.id.login_text_change_pwd);
        mRememberCheck = (CheckBox) findViewById(R.id.Login_Remember);

        userInfo = getSharedPreferences("userInfo", 0);
        String name=userInfo.getString("USER_NAME", "");
        String pwd =userInfo.getString("PASSWORD", "");
        boolean choseRemember =userInfo.getBoolean("mRememberCheck", false);
        //如果上次选了记住密码，那进入登录页面也自动勾选记住密码，并填上用户名和密码

        if(choseRemember){
            mAccount.setText(name);
            mPwd.setText(pwd);
            mRememberCheck.setChecked(true);
        }

        mRegisterButton.setOnClickListener(mListener);                      //采用OnClickListener方法设置不同按钮按下之后的监听事件
        mLoginButton.setOnClickListener(mListener);
        mChangepwdText.setOnClickListener(mListener);
    }


    OnClickListener mListener = new OnClickListener() {                  //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_btn_register:                            //登录界面的注册按钮
                    Intent intent_Login_to_Register = new Intent(Login.this,Register.class) ;    //切换Login Activity至Register Activity
                    startActivity(intent_Login_to_Register);
                    finish();
                    break;
                case R.id.login_btn_login:                              //登录界面的登录按钮
                    logintry();
                    break;
                case R.id.login_text_change_pwd:                             //登录界面的修改密码按钮
                    Intent intent_Login_to_reset = new Intent(Login.this,Resetpwd.class) ;    //切换Login Activity至User Activity
                    startActivity(intent_Login_to_reset);
                    finish();
                    break;
            }
        }
    };

    public void logintry() {                                              //登录按钮监听事件
        if (isUserNameAndPwdValid()) {
            String userName = mAccount.getText().toString().trim();    //获取当前输入的用户名和密码信息
            String userPwd = mPwd.getText().toString().trim();

            me = new Person(userName);
            me.password = userPwd;

                me.login(new Callback<Person>() {
                    @Override
                    public void handle(Person ins) {
                       login_check(true, ins.userName, ins.password);
                       System.out.println("登陆成功");
                    }

                    @Override
                    public void empty(Person ins) {
                        login_check(false, ins.userName, ins.password);
                        System.out.println("登陆空");
                    }

                    @Override
                    public void error(String msg){

                        failed();
                        System.out.println("登陆错误");
                    }

                });
            }
        }

    public void login_check(Boolean successornot, String userName, String userPwd) {
        SharedPreferences.Editor editor = userInfo.edit();

        if (successornot) {
                editor.putString("USER_NAME", userName);
                editor.putString("PASSWORD", userPwd);

                //是否记住密码
                if (mRememberCheck.isChecked()) {
                    editor.putBoolean("mRememberCheck", true);
                } else {
                    editor.putBoolean("mRememberCheck", false);
                }
                editor.apply();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用post方式加到主线程的消息队列中
                        handler1.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "登陆成功！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();

                Intent intent = new Intent(Login.this, WelcomeActivity.class);    //切换Login Activity至Welcome Activity
                startActivity(intent);
                finish();

            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用post方式加到主线程的消息队列中
                        handler1.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "用户名或密码错误，请重新输入！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        }

    private void failed(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 使用post方式加到主线程的消息队列中
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),getString(R.string.login_fail),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    public boolean isUserNameAndPwdValid() {
        if (mAccount.getText().toString().trim().equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 使用post方式加到主线程的消息队列中
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),getString(R.string.account_empty),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
            return false;
        } else if (mPwd.getText().toString().trim().equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 使用post方式加到主线程的消息队列中
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),getString(R.string.pwd_empty),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
            return false;
        }
        return true;
    }
    }

