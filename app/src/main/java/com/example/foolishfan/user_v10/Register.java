package com.example.foolishfan.user_v10;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import dao.*;

public class Register extends AppCompatActivity {
    private EditText mAccount;                        //用户名编辑
    private EditText mEmail;                          //邮箱编辑
    private EditText mPwd;                            //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    private Button mSureButton;                       //确定按钮
    private Button mCancelButton;                     //取消按钮
    private Person me;
    private Handler handler1 = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mEmail = (EditText) findViewById(R.id.resetpwd_edit_email);
        mAccount = (EditText) findViewById(R.id.resetpwd_edit_name);
        mPwd = (EditText) findViewById(R.id.resetpwd_edit_pwd_old);
        mPwdCheck = (EditText) findViewById(R.id.resetpwd_edit_pwd_new);

        mSureButton = (Button) findViewById(R.id.register_btn_sure);
        mCancelButton = (Button) findViewById(R.id.register_btn_cancel);

        mSureButton.setOnClickListener(m_register_Listener);      //注册界面两个按钮的监听事件
        mCancelButton.setOnClickListener(m_register_Listener);
        }


    View.OnClickListener m_register_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.register_btn_sure:                       //确认按钮的监听事件
                    registertry();
                    break;
                case R.id.register_btn_cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                    Intent intent_Register_to_Login = new Intent(Register.this,Login.class) ;    //切换User Activity至Login Activity
                    startActivity(intent_Register_to_Login);
                    finish();
                    break;
            }
        }
    };

    public void registertry() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {
            String userName = mAccount.getText().toString().trim();
            String userEmail = mEmail.getText().toString().trim();
            String userPwd = mPwd.getText().toString().trim();
            String userPwdCheck = mPwdCheck.getText().toString().trim();

            me = new Person(userName);

            if(userPwd.equals(userPwdCheck)){
                        me.register(userPwd,userEmail,new Callback<Person>() {
                            @Override
                            public void handle(Person newRegister) {
                                register_check(true);
                                System.out.println("注册返回成功");
                            }
                            @Override
                            public void empty(Person newRegister){
                                register_check(false);
                                System.out.println("注册返回空");
                            }
                            @Override
                            public void error(String msg){
                                register_check(false);
                                System.out.println("注册返回错误");
                            }
                        });
            }
            else{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用post方式加到主线程的消息队列中
                        handler1.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "密码两次输入不一致，请重新输入！",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        }
    }


    public void register_check(Boolean successornot){
            if (!successornot){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用post方式加到主线程的消息队列中
                        handler1.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "注册失败，请重试！",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
            else
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用post方式加到主线程的消息队列中
                        handler1.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "注册成功！",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
/*
                MyApp user = (MyApp) getApplicationContext();
                user.setPerson(me);// 将User信息放入到Application
*/
                    Intent intent_Register_to_Login = new Intent(Register.this,Login.class) ;    //切换User Activity至Login Activity
                    startActivity(intent_Register_to_Login);
                    finish();
            }
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
                            Toast.makeText(getApplicationContext(), getString(R.string.account_empty), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), getString(R.string.pwd_empty), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), getString(R.string.pwd_check_empty), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();

            return false;
        }else if(mEmail.getText().toString().trim().equals("")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 使用post方式加到主线程的消息队列中
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "邮箱为空，请重新输入！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();

            return false;
        }
        return true;
    }
}
