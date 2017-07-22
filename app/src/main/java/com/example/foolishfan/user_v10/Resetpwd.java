package com.example.foolishfan.user_v10;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import dao.Callback;
import dao.Person;


public class Resetpwd extends AppCompatActivity {
    private EditText mAccount;                        //用户名编辑
    private EditText mPwd_old;                            //密码编辑
    private EditText mPwd_new;                            //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    private Button mSureButton;                       //确定按钮
    private Button mCancelButton;                     //取消按钮
    private Person me;
    private Handler handler1 = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpwd);
        mAccount = (EditText) findViewById(R.id.resetpwd_edit_name);
        mPwd_old = (EditText) findViewById(R.id.resetpwd_edit_pwd_old);
        mPwd_new = (EditText) findViewById(R.id.resetpwd_edit_pwd_new);
        mPwdCheck = (EditText) findViewById(R.id.resetpwd_edit_pwd_check);

        mSureButton = (Button) findViewById(R.id.resetpwd_btn_sure);
        mCancelButton = (Button) findViewById(R.id.resetpwd_btn_cancel);

        mSureButton.setOnClickListener(m_resetpwd_Listener);      //注册界面两个按钮的监听事件
        mCancelButton.setOnClickListener(m_resetpwd_Listener);
/*
        MyApp user = (MyApp) getApplicationContext();
        me = user.getPerson();//取出User信息*/
    }
    View.OnClickListener m_resetpwd_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.resetpwd_btn_sure:                       //确认按钮的监听事件
                    resetpwd_check();
                    break;
                case R.id.resetpwd_btn_cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                    Intent intent_Resetpwd_to_Login = new Intent(Resetpwd.this,Login.class) ;    //切换Resetpwd Activity至Login Activity
                    startActivity(intent_Resetpwd_to_Login);
                    finish();
                    break;
            }
        }
    };
    public void resetpwd_check() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {
            String userName = mAccount.getText().toString().trim();
            String userPwd_old = mPwd_old.getText().toString().trim();
            String userPwd_new = mPwd_new.getText().toString().trim();

            me = new Person(userName);
            me.password = userPwd_old;

            me.resetPassword(userPwd_old, userPwd_new, new Callback<Person>() {
                    @Override
                    public void handle(Person ins) {

                        reset_check(true);
                        System.out.println("修改成功");
                    }
                    @Override
                    public void empty(Person ins) {

                        reset_check(false);
                        System.out.println("修改空");
                    }
                    @Override
                    public void error(String msg){

                        reset_check(false);
                        System.out.println("修改错误");
                    }
                });
        }
    }

    public void reset_check(Boolean successornot){
        if(!successornot){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 使用post方式加到主线程的消息队列中
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "修改密码错误，请重试！",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        }
        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 使用post方式加到主线程的消息队列中
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "修改密码成功！",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        }
    }

    public boolean isUserNameAndPwdValid() {
        String userPwdCheck = mPwdCheck.getText().toString().trim();
        if (mAccount.getText().toString().trim().equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 使用post方式加到主线程的消息队列中
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),  getString(R.string.account_empty),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
            return false;
        } else if (mPwd_old.getText().toString().trim().equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 使用post方式加到主线程的消息队列中
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),  getString(R.string.pwd_empty),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
            return false;
        } else if (mPwd_old.getText().toString().trim().equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 使用post方式加到主线程的消息队列中
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),  getString(R.string.pwd_empty),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
            return false;
        } else if (!mPwd_new.getText().toString().trim().equals(userPwdCheck)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 使用post方式加到主线程的消息队列中
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"两次密码输入不一致，请重新输入！",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
            return false;
        }else if(mPwdCheck.getText().toString().trim().equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 使用post方式加到主线程的消息队列中
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),getString(R.string.pwd_check_empty),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
            return false;
        }
        return true;
    }
}

