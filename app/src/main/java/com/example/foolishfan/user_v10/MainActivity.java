package com.example.foolishfan.user_v10;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import dao.Book;
import dao.Callback;
import dao.Person;

import static com.example.foolishfan.user_v10.Login.me;
import static com.example.foolishfan.user_v10.R.id.lvMember;
import static com.example.foolishfan.user_v10.R.id.mainactivity_btn_mrank;

public class MainActivity extends Activity{

    private String website = "General";
    private int freq = 0;
    private int type = 1;

    private ImageView gotoperson;
    private ImageView gotosearch;
    private TextView test;

    private TextView a1;
    private TextView a2;
    private TextView a3;
    private TextView a4;
    private TextView a5;
    private TextView s1;
    private TextView s2;
    private TextView s3;
    private TextView s4;

    private BookAdapter bookAdapter;
    private ListView lv_books;

    private ImageButton like_Btn;
    private Context mContext;
    private Handler handler1 = new Handler();
    private Handler handler2 = new Handler();
    private Handler handler3 = new Handler();

    public List<Book> allBooks = new ArrayList<>();;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mContext = this;

        a1 = (TextView)findViewById(R.id.mainactivity_btn_mrank);
        a2 = (TextView)findViewById(R.id.mainactivity_btn_amazonrank);
        a3 = (TextView)findViewById(R.id.mainactivity_btn_ddrank);
        a4 = (TextView)findViewById(R.id.mainactivity_btn_jdrank);
        a5 = (TextView)findViewById(R.id.mainactivity_btn_prank);
        s1 = (TextView)findViewById(R.id.mainactivity_btn_dayrank);
        s2 = (TextView)findViewById(R.id.mainactivity_btn_monthrank);
        s3 = (TextView)findViewById(R.id.mainactivity_btn_hotrank);
        s4 = (TextView)findViewById(R.id.mainactivity_btn_newrank);

        gotoperson = (ImageView) findViewById(R.id.mainactivity_btn_user);
        gotoperson.setOnClickListener(mListener);

        gotosearch = (ImageView) findViewById(R.id.mainactivity_btn_search);
        gotosearch.setOnClickListener(mListener);

        test = (TextView) findViewById(R.id.mainactivity_test);
        test.setOnClickListener(mListener);

        lv_books = (ListView) findViewById(lvMember);

        bookAdapter = new BookAdapter(mContext, allBooks);/////init
        //me.isLoggedin = true;

        me.getBooksOfRank(website, type, freq, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                for(Object tempid : val.values()) {
                    String id = tempid.toString();
                    System.out.println("tempid = "+id);
                    me.getBookById(id, new Callback<Book>() {
                        @Override
                        public void handle(Book tempbook) {
                            allBooks.add(tempbook);
                            System.out.println("getbookbyid成功");
                        }

                        @Override
                        public void empty(Book tempbook) {
                            failed();
                            System.out.println("getbookbyid空空");
                        }

                        @Override
                        public void error(String msg) {
                            failed();
                            System.out.println("getbookbyid错误");
                        }
                    });
                }
                bookAdapter.notifyDataSetChanged();
                bookAdapter = new BookAdapter(mContext, allBooks);
                bookAdapter.notifyDataSetChanged();
                lv_books.setAdapter(bookAdapter);
                System.out.println("初始排行榜设置成功");
            }
            @Override
            public void empty(Map val){
                failed();
                System.out.println("初始排行榜设置为空");
            }
            @Override
            public void error(String msg){
                failed();
                System.out.println("初始排行榜设置错误");
            }
        });

        lv_books.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Book thisBook = allBooks.get(position);
                Intent intent = new Intent(MainActivity.this,Bookdetail.class);
                intent.putExtra("book_name", thisBook.id);//传递书名
                intent.putExtra("from","MainActivity");
                startActivity(intent);//跳转到书本介绍细节页面
            }
        });
    }

    public void Amazon(View view) {
        //allBooks.clear();
        website = "Amazon";
        type = 1;

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 使用post方式加到主线程的消息队列中
                handler2.post(new Runnable() {
                    @Override
                    public void run() {
                        s2 = (TextView)findViewById(R.id.mainactivity_btn_monthrank);
                        s2.setText("每小时畅销");
                        s1 = (TextView)findViewById(R.id.mainactivity_btn_hotrank);
                        s1.setText("每小时新书");

                        lv_books = (ListView) findViewById(lvMember);
                    }
                });
            }
        }).start();

        me.getBooksOfRank(website, type, freq, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                for(Object tempid : val.values()) {
                    String id = tempid.toString();
                    System.out.println("tempid = "+id);
                    me.getBookById(id, new Callback<Book>() {
                        @Override
                        public void handle(Book tempbook) {
                            allBooks.add(tempbook);
                            System.out.println("getbookbyid成功");
                        }

                        @Override
                        public void empty(Book tempbook) {
                            failed();
                            System.out.println("getbookbyid空空");
                        }

                        @Override
                        public void error(String msg) {
                            failed();
                            System.out.println("getbookbyid错误");
                        }
                    });
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用post方式加到主线程的消息队列中
                        handler2.post(new Runnable() {
                            @Override
                            public void run() {
                                //bookAdapter.notifyDataSetInvalidated();
                                bookAdapter.notifyDataSetChanged();
                                bookAdapter = new BookAdapter(mContext, allBooks);
                                //bookAdapter.notifyDataSetInvalidated();
                                bookAdapter.notifyDataSetChanged();
                                lv_books.setAdapter(bookAdapter);
                                //bookAdapter.notifyDataSetInvalidated();
                                //bookAdapter.notifyDataSetChanged();
                                System.out.println("AMAZON排行榜设置成功");
                            }
                        });
                    }
                }).start();
            }
            @Override
            public void empty(Map val){
                failed();
                System.out.println("AMAZON排行榜设置为空");
            }
            @Override
            public void error(String msg){
                failed();
                System.out.println("AMAZON排行榜设置错误");
            }
        });
    }

    public void mix(View view) {
        //allBooks.clear();
        website = "General";
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 使用post方式加到主线程的消息队列中
                handler2.post(new Runnable() {
                    @Override
                    public void run() {
                        s1 = (TextView)findViewById(R.id.mainactivity_btn_dayrank);
                        s1.setText("");
                        s1.setHeight(1);
                        s2 = (TextView)findViewById(R.id.mainactivity_btn_monthrank);
                        s2.setText("");
                        s2.setHeight(1);
                        s3 = (TextView)findViewById(R.id.mainactivity_btn_hotrank);
                        s3.setText("");
                        s3.setHeight(1);
                        s4 = (TextView)findViewById(R.id.mainactivity_btn_newrank);
                        s4.setText("");
                        s4.setHeight(1);

                        lv_books = (ListView) findViewById(lvMember);
                    }
                });
            }
        }).start();

        me.getBooksOfRank(website, type, freq, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                for(Object tempid : val.values()) {
                    String id = tempid.toString();
                    System.out.println("tempid = "+id);
                    me.getBookById(id, new Callback<Book>() {
                        @Override
                        public void handle(Book tempbook) {
                            allBooks.add(tempbook);
                            System.out.println("getbookbyid成功");
                        }

                        @Override
                        public void empty(Book tempbook) {
                            failed();
                            System.out.println("getbookbyid空空");
                        }

                        @Override
                        public void error(String msg) {
                            failed();
                            System.out.println("getbookbyid错误");
                        }
                    });
                }
                bookAdapter.notifyDataSetChanged();
                bookAdapter.notifyDataSetChanged();
                bookAdapter.notifyDataSetChanged();
                bookAdapter = new BookAdapter(mContext, allBooks);
                lv_books.setAdapter(bookAdapter);
                System.out.println("综合排行榜设置成功");
            }
            @Override
            public void empty(Map val){
                failed();
                System.out.println("综合排行榜设置为空");
            }
            @Override
            public void error(String msg){
                failed();
                System.out.println("综合排行榜设置错误");
            }
        });

    }

    public void Dangdang(View view) {
        //allBooks.clear();
        website = "Dangdang";
        freq = 0;
        type = 1;

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 使用post方式加到主线程的消息队列中
                handler2.post(new Runnable() {
                    @Override
                    public void run() {
                        s1 = (TextView)findViewById(R.id.mainactivity_btn_dayrank);
                        s1.setText("每日畅销");
                        s2 = (TextView)findViewById(R.id.mainactivity_btn_monthrank);
                        s2.setText("每月畅销");
                        s3 = (TextView)findViewById(R.id.mainactivity_btn_hotrank);
                        s3.setText("每日新书");
                        s4 = (TextView)findViewById(R.id.mainactivity_btn_newrank);
                        s4.setText("每月新书");
                        lv_books = (ListView) findViewById(lvMember);
                    }
                });
            }
        }).start();

        me.getBooksOfRank(website, type, freq, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                for(Object tempid : val.values()) {
                    String id = tempid.toString();
                    System.out.println("tempid = "+id);
                    me.getBookById(id, new Callback<Book>() {
                        @Override
                        public void handle(Book tempbook) {
                            allBooks.add(tempbook);
                            System.out.println("getbookbyid成功");
                        }

                        @Override
                        public void empty(Book tempbook) {
                            failed();
                            System.out.println("getbookbyid空空");
                        }

                        @Override
                        public void error(String msg) {
                            failed();
                            System.out.println("getbookbyid错误");
                        }
                    });
                }
                bookAdapter.notifyDataSetChanged();
                BookAdapter bookAdapter = new BookAdapter(mContext, allBooks);
                bookAdapter.notifyDataSetChanged();
                lv_books.setAdapter(bookAdapter);
                System.out.println("DD排行榜设置成功");
            }
            @Override
            public void empty(Map val){
                failed();
                System.out.println("DD排行榜设置为空");
            }
            @Override
            public void error(String msg){
                failed();
                System.out.println("DD排行榜设置错误");
            }
        });

    }

    public void Jingdong(View view) {
        //allBooks.clear();
        website = "JD";
        freq = 0;
        type = 1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 使用post方式加到主线程的消息队列中
                handler2.post(new Runnable() {
                    @Override
                    public void run() {
                        s1 = (TextView)findViewById(R.id.mainactivity_btn_dayrank);
                        s1.setText("每日畅销");
                        s2 = (TextView)findViewById(R.id.mainactivity_btn_monthrank);
                        s2.setText("每月畅销");
                        s3 = (TextView)findViewById(R.id.mainactivity_btn_hotrank);
                        s3.setText("每日新书");
                        s4 = (TextView)findViewById(R.id.mainactivity_btn_newrank);
                        s4.setText("每月新书");
                        lv_books = (ListView) findViewById(lvMember);
                    }
                });
            }
        }).start();

        me.getBooksOfRank(website, type, freq, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                for(Object tempid : val.values()) {
                    String id = tempid.toString();
                    System.out.println("tempid = "+id);
                    me.getBookById(id, new Callback<Book>() {
                        @Override
                        public void handle(Book tempbook) {
                            allBooks.add(tempbook);
                            System.out.println("getbookbyid成功");
                        }

                        @Override
                        public void empty(Book tempbook) {
                            failed();
                            System.out.println("getbookbyid空空");
                        }

                        @Override
                        public void error(String msg) {
                            failed();
                            System.out.println("getbookbyid错误");
                        }
                    });
                }
                bookAdapter.notifyDataSetChanged();
                BookAdapter bookAdapter = new BookAdapter(mContext, allBooks);
                bookAdapter.notifyDataSetChanged();
                lv_books.setAdapter(bookAdapter);
                System.out.println("JD排行榜设置成功");
            }
            @Override
            public void empty(Map val){
                failed();
                System.out.println("JD排行榜设置为空");
            }
            @Override
            public void error(String msg){
                failed();
                System.out.println("JD排行榜设置错误");
            }
        });

    }

    public void personal(View view) {
        //allBooks.clear();
        website =  "Personal";

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 使用post方式加到主线程的消息队列中
                handler2.post(new Runnable() {
                    @Override
                    public void run() {
                        s1 = (TextView)findViewById(R.id.mainactivity_btn_dayrank);
                        s1.setText("");
                        s1.setHeight(1);
                        s2 = (TextView)findViewById(R.id.mainactivity_btn_monthrank);
                        s2.setText("");
                        s2.setHeight(1);
                        s3 = (TextView)findViewById(R.id.mainactivity_btn_hotrank);
                        s3.setText("");
                        s3.setHeight(1);
                        s4 = (TextView)findViewById(R.id.mainactivity_btn_newrank);
                        s4.setText("");
                        s4.setHeight(1);
                        lv_books = (ListView) findViewById(lvMember);
                    }
                });
            }
        }).start();

        me.getBooksOfRank(website, type, freq, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                for(Object tempid : val.values()) {
                    String id = tempid.toString();
                    System.out.println("tempid = "+id);
                    me.getBookById(id, new Callback<Book>() {
                        @Override
                        public void handle(Book tempbook) {
                            allBooks.add(tempbook);
                            System.out.println("getbookbyid成功");
                        }

                        @Override
                        public void empty(Book tempbook) {
                            failed();
                            System.out.println("getbookbyid空空");
                        }

                        @Override
                        public void error(String msg) {
                            failed();
                            System.out.println("getbookbyid错误");
                        }
                    });
                }
                bookAdapter.notifyDataSetChanged();
                BookAdapter bookAdapter = new BookAdapter(mContext, allBooks);
                bookAdapter.notifyDataSetChanged();
                lv_books.setAdapter(bookAdapter);
                System.out.println("个人排行榜设置成功");
            }
            @Override
            public void empty(Map val){
                failed();
                System.out.println("个人排行榜设置为空");
            }
            @Override
            public void error(String msg){
                failed();
                System.out.println("个人排行榜设置错误");
            }
        });

    }

    public void daily(View view) {
        //allBooks.clear();
        freq = 0;
        type = 1;

        me.getBooksOfRank(website, type, freq, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                for(Object tempid : val.values()) {
                    String id = tempid.toString();
                    System.out.println("tempid = "+id);
                    me.getBookById(id, new Callback<Book>() {
                        @Override
                        public void handle(Book tempbook) {
                            allBooks.add(tempbook);
                            System.out.println("getbookbyid成功");
                        }

                        @Override
                        public void empty(Book tempbook) {
                            failed();
                            System.out.println("getbookbyid空空");
                        }

                        @Override
                        public void error(String msg) {
                            failed();
                            System.out.println("getbookbyid错误");
                        }
                    });
                }
                bookAdapter.notifyDataSetChanged();
                BookAdapter bookAdapter = new BookAdapter(mContext, allBooks);
                bookAdapter.notifyDataSetChanged();
                lv_books.setAdapter(bookAdapter);
                System.out.println("dailynew排行榜设置成功");
            }
            @Override
            public void empty(Map val){
                failed();
                System.out.println("dailynew排行榜设置为空");
            }
            @Override
            public void error(String msg){
                failed();
                System.out.println("dailynew排行榜设置错误");
            }
        });

    }

    public void monthly(View view) {
        //allBooks.clear();
        freq = 1;
        type = 1;

        me.getBooksOfRank(website, type, freq, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                for(Object tempid : val.values()) {
                    String id = tempid.toString();
                    System.out.println("tempid = "+id);
                    me.getBookById(id, new Callback<Book>() {
                        @Override
                        public void handle(Book tempbook) {
                            allBooks.add(tempbook);
                            System.out.println("getbookbyid成功");
                        }

                        @Override
                        public void empty(Book tempbook) {
                            failed();
                            System.out.println("getbookbyid空空");
                        }

                        @Override
                        public void error(String msg) {
                            failed();
                            System.out.println("getbookbyid错误");
                        }
                    });
                }
                bookAdapter.notifyDataSetChanged();
                BookAdapter bookAdapter = new BookAdapter(mContext, allBooks);
                bookAdapter.notifyDataSetChanged();
                lv_books.setAdapter(bookAdapter);
                System.out.println("dailyhot排行榜设置成功");
            }
            @Override
            public void empty(Map val){
                failed();
                System.out.println("dailyhot排行榜设置为空");
            }
            @Override
            public void error(String msg){
                failed();
                System.out.println("dailyhot排行榜设置错误");
            }
        });

    }

    public void hot(View view) {
        //allBooks.clear();
        freq = 0;
        type = 0;

        me.getBooksOfRank(website, type, freq, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                for(Object tempid : val.values()) {
                    String id = tempid.toString();
                    System.out.println("tempid = "+id);
                    me.getBookById(id, new Callback<Book>() {
                        @Override
                        public void handle(Book tempbook) {
                            allBooks.add(tempbook);
                            System.out.println("getbookbyid成功");
                        }

                        @Override
                        public void empty(Book tempbook) {
                            failed();
                            System.out.println("getbookbyid空空");
                        }

                        @Override
                        public void error(String msg) {
                            failed();
                            System.out.println("getbookbyid错误");
                        }
                    });
                }
                bookAdapter.notifyDataSetChanged();
                BookAdapter bookAdapter = new BookAdapter(mContext, allBooks);
                bookAdapter.notifyDataSetChanged();
                lv_books.setAdapter(bookAdapter);
                System.out.println("monthlynew排行榜设置成功");
            }
            @Override
            public void empty(Map val){
                failed();
                System.out.println("monthlynew排行榜设置为空");
            }
            @Override
            public void error(String msg){
                failed();
                System.out.println("monthlynew排行榜设置错误");
            }
        });
    }

    public void newq(View view) {
        //allBooks.clear();
        freq = 1;
        type = 0;
        me.getBooksOfRank(website, type, freq, new Callback<Map>() {
            @Override
            public void handle(Map val) {
                for(Object tempid : val.values()) {
                    String id = tempid.toString();
                    System.out.println("tempid = "+id);
                    me.getBookById(id, new Callback<Book>() {
                        @Override
                        public void handle(Book tempbook) {
                            allBooks.add(tempbook);
                            System.out.println("getbookbyid成功");
                        }

                        @Override
                        public void empty(Book tempbook) {
                            failed();
                            System.out.println("getbookbyid空空");
                        }

                        @Override
                        public void error(String msg) {
                            failed();
                            System.out.println("getbookbyid错误");
                        }
                    });
                }
                bookAdapter.notifyDataSetChanged();
                BookAdapter bookAdapter = new BookAdapter(mContext, allBooks);
                bookAdapter.notifyDataSetChanged();
                lv_books.setAdapter(bookAdapter);
                System.out.println("monthly排行榜设置成功");
            }
            @Override
            public void empty(Map val){
                failed();
                System.out.println("monthlyhot排行榜设置为空");
            }
            @Override
            public void error(String msg){
                failed();
                System.out.println("monthlyhot排行榜设置错误");
            }
        });
    }


    View.OnClickListener mListener = new View.OnClickListener() {                  //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.mainactivity_btn_user:
                    Intent intent_Main_to_Person = new Intent(MainActivity.this, User.class);    //切换至User Activity
                    intent_Main_to_Person.putExtra("from","MainActivity");
                    startActivity(intent_Main_to_Person);
                    finish();
                    break;
                case R.id.mainactivity_btn_search:
                    Intent intent_Main_to_Search = new Intent(MainActivity.this, Search.class);    //切换至User Activity
                    startActivity(intent_Main_to_Search);
                    finish();
                    break;
                case R.id.mainactivity_test:
                    Intent intent_Main_to_test = new Intent(MainActivity.this, Bookdetail.class);    //切换至User Activity
                    intent_Main_to_test.putExtra("book_name","81");//传递书名
                    intent_Main_to_test.putExtra("from","MainActivity");
                    startActivity(intent_Main_to_test);
                    finish();
                    break;
            }
        }
    };

    public final class ViewHolder {
        public ImageView item_img_icon;
        public TextView item_tv_title;
        public TextView item_tv_author;
        public ImageView item_like_btn;
        public TextView item_like_num;
    }

    public class BookAdapter extends BaseAdapter {

        private List<Book> list;
        private Context context;
        private LayoutInflater mInflater;
        private ViewHolder holder;
        private Handler mHandler;

        //通过构造方法接受要显示的数据集合
        public BookAdapter(Context context, List<Book> list) {
            this.list = list;
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public boolean areAllItemsEnabled() {
            // all items are separator
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            // all items are separator
            return true;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Book book = list.get(position);

            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.listview_item, null);
                holder.item_img_icon = (ImageView) convertView.findViewById(R.id.img);
                holder.item_tv_author = (TextView) convertView.findViewById(R.id.author);
                holder.item_tv_title = (TextView) convertView.findViewById(R.id.title);
                holder.item_like_btn = (ImageView) convertView.findViewById(R.id.like_btn);
                holder.item_like_num = (TextView) convertView.findViewById(R.id.like_num);
                convertView.setTag(holder);
            }

            me.getLikeBooks(new Callback<Vector>() {
                @Override
                public void handle(Vector val) {
                    boolean flag = val.contains(book);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 使用post方式加到主线程的消息队列中
                            handler3.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (flag){
                                        holder.item_like_btn.setImageResource(R.drawable.like_focus);
                                    }else{
                                        holder.item_like_btn.setImageResource(R.drawable.like_release);
                                    }
                                    System.out.println("设置爱心成功");
                                }
                            });
                        }
                    }).start();
                }
                @Override
                public void empty(Vector val){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 使用post方式加到主线程的消息队列中
                            handler3.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.item_like_btn.setImageResource(R.drawable.like_release);
                                    System.out.println("设置爱心空空");
                                }
                            });
                        }
                    }).start();
                }
                @Override
                public void error(String msg){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 使用post方式加到主线程的消息队列中
                            handler3.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.item_like_btn.setImageResource(R.drawable.like_release);
                                    System.out.println("设置爱心错误");
                                }
                            });
                        }
                    }).start();
                }
            });
                    book.getLikeCount(new Callback<Integer>() {
                        @Override
                        public void handle(Integer val) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // 使用post方式加到主线程的消息队列中
                                    handler3.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.item_like_num.setText(val +"");
                                        }
                                    });
                                }
                            }).start();
                        }
                        @Override
                        public void empty(Integer val) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // 使用post方式加到主线程的消息队列中
                                    handler3.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.item_like_num.setText("暂无数据");
                                        }
                                    });
                                }
                            }).start();
                        }
                    });
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 使用post方式加到主线程的消息队列中
                    handler3.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.item_tv_title.setText(book.title);
                            holder.item_tv_author.setText(book.author);
                        }
                    });
                }
            }).start();

            new Thread(){
                public void run() {
                    Bitmap bitmap = getBitmapFromUrl(book.imageUrl);
                    Message message = Message.obtain();
                    message.obj=bitmap;
                    System.out.println("showImageByThead");
                    mHandler.sendMessage(message);
                };
            }.start();

            return convertView;
        }

        public void run() {
            Looper.prepare();
            mHandler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    super.handleMessage(msg);
                    holder.item_img_icon.setImageBitmap((Bitmap) msg.obj);
                    System.out.println("设置了设置了");
                }
            };
            Looper.loop();
        }
    }



    public Bitmap getBitmapFromUrl(String urlString){
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL mUrl= new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap= BitmapFactory.decodeStream(is);
            connection.disconnect();
            System.out.println("下载完成");
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void failed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 使用post方式加到主线程的消息队列中
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "获取信息失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}


