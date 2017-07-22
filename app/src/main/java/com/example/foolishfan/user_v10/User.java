package com.example.foolishfan.user_v10;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import dao.Book;
import dao.Callback;
import dao.Person;

import static com.example.foolishfan.user_v10.Login.me;
import static com.example.foolishfan.user_v10.R.id.lvLike;

public class User extends AppCompatActivity {
    private ImageView mReturnButton;
    private ImageView mMainActivityButton;

    private Handler handler1 = new Handler();
    private Handler handler2 = new Handler();
    private Handler handler3 = new Handler();
    private Person show;

    private TextView mName;
    private Context mContext;

    private TextView ufollow;
    private TextView ufollower;

    private String follownum;
    private String followernum;

    public static List<Book> allLikes = new ArrayList<>();;
    private ImageView backtologin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);
        mMainActivityButton = (ImageView) findViewById(R.id.mainactivity);
        mName = (TextView) findViewById(R.id.person_name);
        ufollow = (TextView) findViewById(R.id.follow);
        ufollower = (TextView) findViewById(R.id.follower);
        mMainActivityButton = (ImageView) findViewById(R.id.returnback);
        mContext = this;

        backtologin = (ImageView) findViewById(R.id.returnback);
        backtologin.setOnClickListener(mListener);

        Intent intent = getIntent();
        Bundle d = intent.getExtras();
        String i = d.getString("from");
        String j = d.getString("person_name");

        if (i.equals("MainActivity")) {
            show = me;//展示我自己的信息
            //show.isLoggedin  = true;
        } else {
            show = new Person(j);//展示follow或者follower的信息
            //show.isLoggedin  = true;
            setContentView(R.layout.followuser);
            show.getFollowings(new Callback<Vector>() {
                @Override
                public void handle(Vector val) {
                    if (val.contains(show))
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 使用post方式加到主线程的消息队列中
                                handler1.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mReturnButton.setImageResource(R.drawable.follow_le);
                                        System.out.println("关注按钮设置");
                                    }
                                });
                            }
                        }).start();
                    else
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 使用post方式加到主线程的消息队列中
                                handler1.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mReturnButton.setImageResource(R.drawable.follow_ma);
                                        System.out.println("关注按钮设置");
                                    }
                                });
                            }
                        }).start();
                }

                @Override
                public void empty(Vector val) {
                    mReturnButton.setImageResource(R.drawable.follow_ma);
                    System.out.println("关注按钮设置空");
                }

                @Override
                public void error(String msg) {
                    mReturnButton.setImageResource(R.drawable.follow_ma);
                    failed();
                    System.out.println("关注按钮设置错误");
                }
            });
        }

        mName.setText(show.userName);

        show.getFollowCount(new Callback<Map>() {
            @Override
            public void handle(Map val) {
                follownum = (String) val.get("followings");
                followernum = (String) val.get("followers");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用post方式加到主线程的消息队列中
                        handler1.post(new Runnable() {
                            @Override
                            public void run() {
                                ufollow.setText(follownum);
                                ufollower.setText(followernum);
                                System.out.println("设置完毕好了");
                            }
                        });
                    }
                }).start();
                System.out.println(follownum);
                System.out.println(followernum);
                System.out.println("得到关注和粉丝");
            }

            @Override
            public void empty(Map val) {
                follownum = "0";
                followernum = "0";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用post方式加到主线程的消息队列中
                        handler1.post(new Runnable() {
                            @Override
                            public void run() {
                                ufollow.setText(follownum);
                                ufollower.setText(followernum);
                                System.out.println("设置完毕空空");
                            }
                        });
                    }
                }).start();
                System.out.println("关注粉丝空空");
            }

            public void error(String msg) {
                failed();
                System.out.println("关注粉丝错误");
            }
        });

        ufollow.setText(follownum);
        // System.out.println("设置完毕");
        ufollower.setText(followernum);
        final ListView lv_likes = (ListView) findViewById(lvLike);

        show.getLikeBooks(new Callback<Vector>() {
            @Override
            public void handle(Vector val) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用post方式加到主线程的消息队列中
                        handler2.post(new Runnable() {
                            @Override
                            public void run() {
                                int i;
                                for (i = 0; i < val.size(); i++) {
                                    show.getBookById((String) val.get(i), new Callback<Book>() {
                                        @Override
                                        public void handle(Book b) {
                                            allLikes.add(b);
                                        }

                                        @Override
                                        public void empty(Book b) {
                                            System.out.println("getbookbyid空空");
                                        }

                                        @Override
                                        public void error(String msg) {
                                            System.out.println("getbookbyid错误");
                                        }
                                    });
                                }
                            }
                        });
                    }
                }).start();
                        LikeAdapter likeAdapter = new LikeAdapter(mContext, allLikes);
                        lv_likes.setAdapter(likeAdapter);
                        System.out.println("成功进入adapter");
            }

            @Override
            public void empty(Vector val) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用post方式加到主线程的消息队列中
                        handler2.post(new Runnable() {
                            @Override
                            public void run() {
                                lv_likes.setEmptyView(findViewById(R.id.emptyElement));
                                System.out.println("进入adapter空空");
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void error(String msg) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用post方式加到主线程的消息队列中
                        handler1.post(new Runnable() {
                            @Override
                            public void run() {
                                failed();
                                lv_likes.setEmptyView(findViewById(R.id.emptyElement));
                                System.out.println("进入adapter失败");
                            }
                        });
                    }
                }).start();
            }
        });

        lv_likes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Book thisBook = allLikes.get(position);
                Intent intent = new Intent(User.this, Bookdetail.class);
                intent.putExtra("from", "User");
                intent.putExtra("person_name", show.userName);
                intent.putExtra("book_name", thisBook.id);//传递书名
                startActivity(intent);//跳转到书本介绍细节页面
            }
        });
    }

    View.OnClickListener mListener = new View.OnClickListener() {                  //不同按钮按下的监听事件选择
        public void onClick(View v) {
                me.logout(new Callback<Person>() {
                    @Override
                    public void handle(Person p) {
                        System.out.println("登出");
                        Intent intent3 = new Intent(User.this,Login.class) ;
                        startActivity(intent3);
                        finish();
                    }
                    @Override
                    public void empty(Person p){
                        failed();
                        System.out.println("登出空");
                        Intent intent3 = new Intent(User.this,Login.class) ;
                        startActivity(intent3);
                        finish();
                    }
                    @Override
                    public void error(String msg){
                        failed();
                        System.out.println("登出失败");
                        Intent intent3 = new Intent(User.this,Login.class) ;
                        startActivity(intent3);
                        finish();
                    }
                });
        }
    };

    public final class ViewHolder {
        public ImageView like_img_icon;
        public TextView like_tv_title;
        public TextView like_tv_author;
        public ImageView like_like_btn;
        public TextView like_like_num;
    }

    public class LikeAdapter extends BaseAdapter{

        private List<Book> list;
        private Context context;
        private LayoutInflater mInflater;
        private ViewHolder holder;

        public LikeAdapter(Context context, List<Book> list){
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
        public View getView(int position, View convertView, ViewGroup parent){

            final Book like = list.get(position);

            if(convertView != null){
                holder = (ViewHolder) convertView.getTag();
            }else{
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.listview_item, null);
                holder.like_img_icon = (ImageView) convertView.findViewById(R.id.img);
                holder.like_tv_author = (TextView) convertView.findViewById(R.id.author);
                holder.like_tv_title = (TextView) convertView.findViewById(R.id.title);
                holder.like_like_btn = (ImageView) convertView.findViewById(R.id.like_btn);
                holder.like_like_num = (TextView) convertView.findViewById(R.id.like_num);
                convertView.setTag(holder);
            }

            holder.like_tv_title.setText(like.title);
            holder.like_tv_author.setText(like.author);

            me.getLikeBooks(new Callback<Vector>() {
                @Override
                public void handle(Vector val) {
                    boolean flag = val.contains(like);
                    if (flag){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 使用post方式加到主线程的消息队列中
                                handler1.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.like_like_btn.setImageResource(R.drawable.like_focus);
                                        System.out.println("设置喜欢");
                                    }
                                });
                            }
                        }).start();
                    }else{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 使用post方式加到主线程的消息队列中
                                handler1.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.like_like_btn.setImageResource(R.drawable.like_release);
                                        System.out.println("设置不喜欢");
                                    }
                                });
                            }
                        }).start();
                    }
                }
                @Override
                public void empty(Vector val){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 使用post方式加到主线程的消息队列中
                            handler1.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.like_like_btn.setImageResource(R.drawable.like_release);
                                    System.out.println("设置喜欢空");
                                }
                            });
                        }
                    }).start();
                }
                @Override
                public void error(String msg){
                    failed();
                    System.out.println("设置喜欢错误");
                }
            });

            new Thread(){
                public void run() {
                    Bitmap bitmap = getBitmapFromUrl(like.imageUrl);
                    Message message = Message.obtain();
                    message.obj=bitmap;
                    System.out.println("showImageByThead");
                    mHandler.sendMessage(message);
                };
            }.start();

            like.getLikeCount(new Callback<Integer>() {
                @Override
                public void handle(Integer val) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 使用post方式加到主线程的消息队列中
                            handler1.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.like_like_num.setText(val+"");
                                    System.out.println("获取like数");
                                }
                            });
                        }
                    }).start();
                }
                @Override
                public void empty(Integer val){
                    failed();
                    System.out.println("获取like数空");
                }
                @Override
                public void error(String msg){
                    failed();
                    System.out.println("获取like数错误");
                }
            });
            return convertView;
        }

        Handler mHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                holder.like_img_icon.setImageBitmap((Bitmap) msg.obj);
                System.out.println("设置了设置了");
            };
        };
    }

    public void go_to_mainactivity(View view) {
        Intent intent4 = new Intent(User.this,MainActivity.class);
        startActivity(intent4);
        finish();
    }
    public void go_to_follow(View view) {
        Intent intent5 = new Intent(User.this,Follow.class);
        intent5.putExtra("from", "User");
        intent5.putExtra("person_name", show.userName);
        intent5.putExtra("from2","Follow");
        startActivity(intent5);
        finish();
    }
    public void go_to_follower(View view) {
        Intent intent6 = new Intent(User.this,Follow.class);
        intent6.putExtra("from", "User");
        intent6.putExtra("person_name", show.userName);
        intent6.putExtra("from2","Follower");
        startActivity(intent6);
        finish();
    }

    public void follow(View view){
        me.follow(show.id,new Callback<Person>(){
            @Override
            public void handle(Person val) {
                me.getFollowings(new Callback<Vector>() {
                    @Override
                    public void handle(Vector val) {
                        if (val.contains(show))
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // 使用post方式加到主线程的消息队列中
                                    handler1.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mReturnButton.setImageResource(R.drawable.follow_le);
                                            System.out.println("变换关注");
                                        }
                                    });
                                }
                            }).start();
                        else
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // 使用post方式加到主线程的消息队列中
                                    handler1.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mReturnButton.setImageResource(R.drawable.follow_ma);
                                            System.out.println("变换关注");
                                        }
                                    });
                                }
                            }).start();
                    }
                    @Override
                    public void empty(Vector val){
                        failed();
                        System.out.println("变换关注空空");
                    }
                    @Override
                    public void error(String msg){
                        failed();
                        System.out.println("变换关注错误");
                    }
                });
            }
            @Override
            public void empty(Person val){
                failed();
            }
            @Override
            public void error(String msg){
                failed();
            }
    });
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
            System.out.println("下载");
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
                handler3.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "读取数据失败！",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}
