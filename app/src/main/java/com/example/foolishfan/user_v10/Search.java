package com.example.foolishfan.user_v10;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;


/**
 * Created by 婧懿 on 2017/7/10.
 */

public class Search extends Activity {

    private EditText shuru;
    private Context context;
    private Button search;
    private TextView cancel;
    private Spinner spinner;
    String SearchName;
    String SearchType;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        //通过id找到相应的控件
        shuru = (EditText) findViewById(R.id.searchuser);
        search = (Button) findViewById(R.id.search);
        cancel = (TextView) findViewById(R.id.cancel);
        spinner = (Spinner) findViewById(R.id.spinner1);

        SearchName = shuru.getText().toString().trim();

        List<String> list = new ArrayList<String>();
        list.add("图书");
        list.add("用户");

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            // parent： 为控件Spinner view：显示文字的TextView position：下拉选项的位置从0开始
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TextView tvResult = (TextView) findViewById(R.id.tvResult);
//获取Spinner控件的适配器
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) parent.getAdapter();
                //tvResult.setText(adapter.getItem(position));
                SearchType=adapter.getItem(position);
            }
            //没有选中时的处理
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void search(View view){
        Intent intent;
        if(SearchType.equals("用户"))
        {
            intent = new Intent(Search.this, SearchList_person.class);
            intent.putExtra("content",SearchName);
        }
        else
        {
            intent = new Intent(Search.this, SearchList.class);
            intent.putExtra("content",SearchName);
        }
        startActivity(intent);
    }

    public void nosearch(View view){
        Intent intent = new Intent(Search.this, MainActivity.class);
        startActivity(intent);
    }
}