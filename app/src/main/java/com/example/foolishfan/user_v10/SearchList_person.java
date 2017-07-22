package com.example.foolishfan.user_v10;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

import dao.Book;
import dao.Callback;
import dao.Person;

import static com.example.foolishfan.user_v10.Login.me;


public class SearchList_person extends AppCompatActivity {
    private ImageView ReturntoUser;

    private Context mContext;
    private Handler handler1 = new Handler();
    private Handler handler2 = new Handler();
    private ListView lv_search;

    public static List<Person> allSearchs = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlist);
        mContext = this;
        ReturntoUser = (ImageView)findViewById(R.id.returnback0);
        lv_search = (ListView)findViewById(R.id.lvsearch);

        Intent intent = getIntent();
        Bundle d = intent.getExtras();
        String j = d.getString("content");
        me.getPersonByName(j, new Callback<Vector>() {
            @Override
            public void handle(Vector val) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用post方式加到主线程的消息队列中
                        handler2.post(new Runnable() {
                            @Override
                            public void run() {
                                allSearchs = val;
                                SearchAdapter searchAdapter = new SearchAdapter(mContext, allSearchs);
                                lv_search.setAdapter(searchAdapter);
                                System.out.println("搜索adapter成功");
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
                                lv_search.setEmptyView(findViewById(R.id.emptyElement));
                                System.out.println("搜索adapter空空");
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
                                lv_search.setEmptyView(findViewById(R.id.emptyElement));
                                System.out.println("搜索adapter错误");
                                failed();
                            }
                        });
                    }
                }).start();
            }
        });

        lv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(SearchList_person.this,User.class);
                Person thisperson = allSearchs.get(position);
                intent.putExtra("from","SearchListPerson");
                intent.putExtra("person_name",thisperson.userName);
                startActivity(intent);
            }
        });
    }

    public final class ViewHolder {
        public ImageView like_img_icon;
        public TextView like_tv_name;
        public TextView like_tv_email;
    }

    public class SearchAdapter extends BaseAdapter{

        private List<Person> list;
        private Context context;
        private LayoutInflater mInflater;

        public SearchAdapter(Context context, List<Person> list){
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

            final ViewHolder holder;

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
                            holder.like_img_icon.setImageResource(R.drawable.usericon);
                            holder.like_tv_name.setText(like.userName);
                            holder.like_tv_email.setText(like.email);
                        }
                    });
                }
            }).start();
            return convertView;
        }
    }


    public void back_to_search(View view) {
        Intent intent3 = new Intent(SearchList_person.this,MainActivity.class) ;
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
                        Toast.makeText(getApplicationContext(), "搜索不到该结果，请重新搜索！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}
