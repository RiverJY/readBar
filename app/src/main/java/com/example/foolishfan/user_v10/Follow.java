package com.example.foolishfan.user_v10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import dao.Callback;
import dao.Person;

import static com.example.foolishfan.user_v10.Login.me;
import static com.example.foolishfan.user_v10.R.id.lvLike;
import static com.example.foolishfan.user_v10.R.id.lvfollow;

public class Follow extends AppCompatActivity {
    private ImageView ReturntoUser;
    private Context mContext;
    private Person show;
    private TextView intro;
    private Handler handler1 = new Handler();
    private Handler handler2 = new Handler();
    private ListView lv_likes;
    public List<Person> allFollows = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow);
        intro = (TextView) findViewById(R.id.intro);
        mContext = this;
        ReturntoUser = (ImageView) findViewById(R.id.returnback0);
        lv_likes = (ListView) findViewById(lvfollow);

        Intent intent = getIntent();
        Bundle d = intent.getExtras();
        String i  = d.getString("person_name");
        String k = d.getString("from");
        String j = d.getString("from2");

        if (i.equals(me.userName)) {
            show = me;//展示我自己的信息
        } else {
            show = new Person(j);//展示follow或者follower的信息
        }

            if (j.equals("Follow")) {
                intro.setText("关注");
                show.getFollowings(new Callback<Vector>() {
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
                                        for(i=0; i<val.size();i++) {
                                            show.getPersonById((String) val.get(i), new Callback<Person>() {
                                                @Override
                                                public void handle(Person val) {
                                                    allFollows.add(val);
                                                    System.out.println("拿到用户ID");
                                                }
                                                @Override
                                                public void empty(Person val){
                                                    System.out.println("拿到用户ID空");
                                                }
                                                @Override
                                                public void error(String msg){
                                                    System.out.println("拿到用户ID错误");
                                                }
                                            });
                                        }
                                        LikeAdapter likeAdapter = new LikeAdapter(mContext, allFollows);
                                        lv_likes.setAdapter(likeAdapter);
                                        System.out.println("关注列表获取");
                                    }
                                });
                            }
                        }).start();
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
                                        System.out.println("关注列表空空");
                                        failed();
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
                                handler2.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        lv_likes.setEmptyView(findViewById(R.id.emptyElement));
                                        failed();
                                        System.out.println("关注列表失败");
                                    }
                                });
                            }
                        }).start();
                    }
                });
            }
            else if(j.equals("Follower")) {
                intro.setText("粉丝");
                show.getFollowers(new Callback<Vector>() {
                    @Override
                    public void handle(Vector val) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 使用post方式加到主线程的消息队列中
                                handler2.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        int i = 0;
                                        for(; i<val.size();i++) {
                                            show.getPersonById((String) val.get(i), new Callback<Person>() {
                                                @Override
                                                public void handle(Person val) {
                                                    allFollows.add(val);
                                                }
                                            });
                                        }
                                        LikeAdapter likeAdapter = new LikeAdapter(mContext, allFollows);
                                        lv_likes.setAdapter(likeAdapter);
                                        System.out.println("粉丝列表获取");
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
                                handler2.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        lv_likes.setEmptyView(findViewById(R.id.emptyElement));
                                        System.out.println("粉丝列表空空");
                                        failed();
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
                                handler1.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        lv_likes.setEmptyView(findViewById(R.id.emptyElement));
                                        failed();
                                        System.out.println("粉丝列表错误");
                                    }
                                });
                            }
                        }).start();
                    }
                });
            }

     lv_likes.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick (AdapterView < ? > parent, View view,
        int position, long id){
        Intent intent = new Intent(Follow.this, User.class);
        Person thisPerson = allFollows.get(position);
        intent.putExtra("from", "Follow");
        intent.putExtra("person_name", thisPerson.userName);
        startActivity(intent);
        }
    });
}

    public final class ViewHolder {
        public ImageView like_img_icon;
        public TextView like_tv_name;
        public TextView like_tv_email;
    }

    public class LikeAdapter extends BaseAdapter{

        private List<Person> list;
        private Context context;
        private LayoutInflater mInflater;
        private ViewHolder holder;

        public LikeAdapter(Context context, List<Person> list){
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

            final Person like = list.get(position);

            if(convertView != null){
                holder = (ViewHolder) convertView.getTag();
            }else{
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.follow_item, null);
                holder.like_img_icon = (ImageView) convertView.findViewById(R.id.pimg);
                holder.like_tv_email = (TextView) convertView.findViewById(R.id.pid);
                holder.like_tv_name = (TextView) convertView.findViewById(R.id.pname);
                convertView.setTag(holder);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 使用post方式加到主线程的消息队列中
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.like_tv_name.setText(like.userName);
                            holder.like_tv_email.setText(like.email);
                            holder.like_img_icon.setImageResource(R.drawable.flower);
                            System.out.println("设置每一条目");
                        }
                    });
                }
            }).start();
            return convertView;
        }
    }

    public void go_to_user(View view) {
        Intent intent3 = new Intent(Follow.this,MainActivity.class);
        intent3.putExtra("from", "Follow");
        intent3.putExtra("person_name", show.userName);
        startActivity(intent3);
        finish();
    }

    private void failed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 使用post方式加到主线程的消息队列中
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "列表为空！",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}
