package com.example.foolishfan.user_v10;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import dao.Book;
import dao.Callback;
import dao.Person;

import static com.example.foolishfan.user_v10.Login.me;

public class Bookdetail extends ActionBarActivity{

    private TextView booktitle;
    private TextView bookdescription;
    private TextView booklike;
    private RatingBar bookstar;
    private ImageView bookimage;
    private TextView bookauthor;
    private ImageView booklikeornot;
    private TextView amazonprice;
    private TextView ddprice;
    private TextView jdprice;
    private TextView tag1;
    private TextView tag2;
    private TextView tag3;
    private Person show;
    private Handler handler1 = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookdetail);

        System.out.println("登陆了吗"+me.isLoggedin);

        Intent intent = getIntent();
        Bundle d = intent.getExtras();
        String i = d.getString("from");
        String j = d.getString("person_name");


        String book_id = d.getString("book_name");


        if(i.equals("MainActivity"))
        {
            show = me;//展示我自己的信息
        }
        else if(j.equals(me.userName))
        {
            show = me;//展示我自己的信息
        }
        else
        {
            show = new Person(j);//展示follow或者follower的信息
        }

        this.initView();

        // 依position读取对应的书
        me.getBookById(book_id, new Callback<Book>() {
            @Override
            public void handle(Book val) {
                new Thread(new Runnable() {
                    public void run() {
                        Drawable image = LoadImageFromWebOperations(val.imageUrl);
                        Message message = mHandler.obtainMessage();
                        message.what = 1;
                        message.obj = image;
                        mHandler.sendMessage(message);
                    }
                }).start();

                booktitle.setText(val.title);
                bookauthor.setText(val.author);
                bookdescription.setText((String)(((List)val.specs).get(3)));
                bookstar.setRating((float)(((List)val.specs).get(4)));

                val.getTags(new Callback<List>(){
                    @Override
                    public void handle(List str){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 使用post方式加到主线程的消息队列中
                                handler1.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Map map1 = (Map)str.get(0);
                                        tag1.setText((String)map1.get(0));
                                        Map map2 = (Map)str.get(1);
                                        tag3.setText((String)map2.get(1));
                                        Map map3 = (Map)str.get(2);
                                        tag3.setText((String)map3.get(2));
                                        //tag2.setText(str.get(1).get("tag__name"));
                                        //tag3.setText(str.get(2).get("tag__name"));
                                        System.out.println("tag设置好了");
                                    }
                                });
                            }
                        }).start();
                    }
                    @Override
                    public void empty(List str)
                    {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 使用post方式加到主线程的消息队列中
                                handler1.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tag1.setText("-");
                                        tag2.setText("-");
                                        tag3.setText("-");
                                        System.out.println("tag设置空空");
                                    }
                                });
                            }
                        }).start();
                    }
                    @Override
                    public void error(String msg)
                    {
                        failed();
                        System.out.println("tag设置错误");
                    }
                });

                show.getLikeBooks(new Callback<Vector>() {
                    @Override
                    public void handle(Vector list) {
                        boolean flag = list.contains(val);
                        if (flag) {
                            booklikeornot.setImageResource(R.drawable.like_focus);
                        } else {
                            booklikeornot.setImageResource(R.drawable.like_release);
                        }
                    }
                    @Override
                    public void empty(Vector list){
                        booklikeornot.setImageResource(R.drawable.like_release);
                    }
                    @Override
                    public void error(String msg){
                        booklikeornot.setImageResource(R.drawable.like_release);
                        failed();
                    }
                });

                booklikeornot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        me.likeBook(val, new Callback<Person>() {
                            @Override
                            public void handle(Person p) {
                                me.getLikeBooks(new Callback<Vector>() {
                                    @Override
                                    public void handle(Vector llist) {
                                        boolean flag = llist.contains(val);
                                        if (flag) {
                                            booklikeornot.setImageResource(R.drawable.like_focus);
                                        } else {
                                            booklikeornot.setImageResource(R.drawable.like_release);
                                        }
                                    }
                                    @Override
                                    public void empty(Vector llist){
                                        booklikeornot.setImageResource(R.drawable.like_release);
                                    }
                                });
                                AnimationTools.scale(booklikeornot);
                                val.getLikeCount(new Callback<Integer>() {
                                    @Override
                                    public void handle(Integer in)
                                    {
                                        booklike.setText(in + "");
                                    }
                                    @Override
                                    public void empty(Integer in){

                                        booklike.setText("-");
                                    }
                                });
                            }
                            @Override
                            public void empty(Person p){

                                failed();
                            }
                        });

                        val.getWebUrls(new Callback<Map>(){
                            @Override
                            public void handle(Map url) {
                                val.getPrices(new Callback<Map>(){
                                    @Override
                                    public void handle(Map price) {

                                        amazonprice.setText(Html.fromHtml("< a href='"+url.get(0)+"'>"+price.get(0)+"</ a>"));
                                        amazonprice.setMovementMethod(LinkMovementMethod.getInstance());
                                        ddprice.setText(Html.fromHtml("< a href='"+url.get(1)+"'>"+price.get(1)+"</ a>"));
                                        ddprice.setMovementMethod(LinkMovementMethod.getInstance());
                                        jdprice.setText(Html.fromHtml("< a href='"+url.get(2)+"'>"+price.get(2)+"</ a>"));
                                        jdprice.setMovementMethod(LinkMovementMethod.getInstance());
                                    }
                                    @Override
                                    public void empty(Map price){
                                        amazonprice.setText("暂无数据");
                                        ddprice.setText("暂无数据");
                                        jdprice.setText("暂无数据");
                                    }
                                });
                            }
                            @Override
                            public void empty(Map price){
                                amazonprice.setText("暂无数据");
                                ddprice.setText("暂无数据");
                                jdprice.setText("暂无数据");
                            }
                        });

                        booktitle.setText(val.title);
                        bookauthor.setText(val.author);
                        val.getLikeCount(new Callback<Integer>()
                        {
                            @Override
                            public void handle(Integer count)
                            {
                                booklike.setText(count + "");
                            }
                            @Override
                            public void empty(Integer count){
                                booklike.setText("-");
                            }
                        });
                    }
                });
            }
            @Override
            public void empty(Book val){
                System.out.println("获取书空空");
            }
            @Override
            public void error(String msg){
                System.out.println("获取书错误");
            }
        });
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Drawable drawable = (Drawable) msg.obj;
                    bookimage.setImageDrawable(drawable);
                    break;
                case 2:
                    // ...
                    break;
                default:
                    break;
            }
        }
    };


    private void initView() {
        booktitle = (TextView) findViewById(R.id.title);
        booklike = (TextView) findViewById(R.id.like_num);
        bookdescription = (TextView) findViewById(R.id.info);
        bookimage = (ImageView) findViewById(R.id.img);
        bookstar = (RatingBar) findViewById(R.id.bookdetail_star);
        bookauthor = (TextView) findViewById(R.id.author);
        booklikeornot = (ImageView) findViewById(R.id.like_btn);
        amazonprice = (TextView)findViewById(R.id.amazonp);
        ddprice = (TextView)findViewById(R.id.ddp);
        jdprice = (TextView)findViewById(R.id.jdp);
        tag1 = (TextView)findViewById(R.id.bookdetail_tag1);
        tag2 = (TextView)findViewById(R.id.bookdetail_tag2);
        tag3 = (TextView) findViewById(R.id.bookdetail_tag3);
    }

    public void onBackClick (View view){

        this.finish();//返回上一级activity
    }

    private void failed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 使用post方式加到主线程的消息队列中
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "操作失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}
