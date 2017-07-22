package com.example.foolishfan.user_v10;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import dao.Book;
import dao.Callback;
import dao.Person;

import static com.example.foolishfan.user_v10.Login.me;
import static com.example.foolishfan.user_v10.R.id.lvLike;
import static com.example.foolishfan.user_v10.R.id.lvfollow;
import static com.example.foolishfan.user_v10.R.id.lvsearch;

public class SearchList extends AppCompatActivity {
    private Context mContext;
    private ImageView ReturntoUser;
    private ListView lv_search;
    private Handler handler1 = new Handler();
    private Handler handler2 = new Handler();
    public static List<Book> allSearchs = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlist);
        mContext = this;
        ReturntoUser = (ImageView) findViewById(R.id.returnback0);
        lv_search = (ListView) findViewById(R.id.lvsearch);

        Intent intent = getIntent();
        Bundle d = intent.getExtras();
        String j = d.getString("content");
        me.getBookByName(j, new Callback<Vector>() {
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
                                                    for (i = 0; i<val.size(); i++){
                                                        me.getBookById((String)val.get(i), new Callback<Book>() {
                                                            @Override
                                                            public void handle(Book val) {
                                                                allSearchs.add(val);
                                                                System.out.println("添加1本找到的书");
                                                            }
                                                            @Override
                                                            public void empty(Book val) {
                                                                System.out.println("添加0本找到的书");
                                                            }
                                                            @Override
                                                            public void error(String msg){
                                                                System.out.println("添加找到的书错误");
                                                            }
                                                         });
                                                      }
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

                Intent intent = new Intent(SearchList.this, Bookdetail.class);
                Book thisbook = allSearchs.get(position);
                intent.putExtra("from", "SearchList");
                intent.putExtra("book_name", thisbook.title);
                startActivity(intent);//跳转到书本介绍细节页面
            }
        });
    }
    public final class ViewHolder {
        public ImageView like_img_icon;
        public TextView like_tv_name;
        public TextView like_tv_email;
    }

    public class SearchAdapter extends BaseAdapter{

        private List<Book> list;
        private Context context;
        private LayoutInflater mInflater;
        private ViewHolder holder;

        public SearchAdapter(Context context, List<Book> list){
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
                holder.like_tv_email = (TextView) convertView.findViewById(R.id.author);
                holder.like_tv_name = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            }

            new Thread(){
                public void run() {
                    Bitmap bitmap = getBitmapFromUrl(like.imageUrl);
                    Message message = Message.obtain();
                    message.obj=bitmap;
                    System.out.println("showImageByThead");
                    mHandler.sendMessage(message);
                };
            }.start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 使用post方式加到主线程的消息队列中
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.like_tv_name.setText(like.title);
                            holder.like_tv_email.setText(like.author);
                        }
                    });
                }
            }).start();

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

    public void back_to_search(View view) {
        Intent intent3 = new Intent(SearchList.this,MainActivity.class) ;
        startActivity(intent3);
        finish();
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
