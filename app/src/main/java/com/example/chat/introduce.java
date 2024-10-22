package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class introduce extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);

        // 介绍页面，目前很简单，没有复杂的功能
        TextView introduce_text = findViewById(R.id.introduce_text);

        String Text = "目前，只是做了一个大概的框架，和简单的些功能。\n 现在仅支持AI聊天，AI聊天使用的是百度的文心一言大模型API接口实现。\n";

        introduce_text.setText(Text);
    }
}