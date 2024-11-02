package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.example.chat.R.id;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private  chatFragment chatFragment;
    private  myFragment myFragment;
    private ServerSocket serverSocket;

    private file_os fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 在app刚运行时，读取配置文件
        fs = new file_os();
        ((dataHub) getApplication()).getConfig();

        chatFragment = new chatFragment();
        myFragment = new myFragment();

        RadioButton chatNav = findViewById(R.id.chat_nav);
        chatNav.setChecked(true);

        // 默认显示聊天 Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, chatFragment).commit();

        RadioGroup rgGroup = findViewById(R.id.rg_group);

        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            // 设置多个页面切换
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment selectedFragment = null;
                if (checkedId == id.chat_nav) {
                    selectedFragment = chatFragment;
                } else if (checkedId == id.my_nav) {
                    selectedFragment = myFragment;
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, selectedFragment).commit();
                }
            }
        });
        // 启动 TCP 服务器
        new Thread(new Runnable() {
            @Override
            public void run() {
                startTcpServer();
            }
        }).start(); // 在后台线程中启动服务器
    }

    public void startTcpServer() {
        try {
            Log.i("toad", "startTcpServer: 服务器启动，等待客户端连接...");
            serverSocket = new ServerSocket(9231);

            while (true) { // 支持多个客户端连接
                Socket socket = serverSocket.accept(); // 等待客户端连接
                Log.i("toad", "客户端已连接");

                // 将连接保存到全局的 Application 中
                ((dataHub) getApplication()).setSocket(socket);

                // 跳转到聊天页面
                runOnUiThread(() -> {
                    Intent intent = new Intent(MainActivity.this, Chat.class);
                    startActivity(intent);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 销毁 Activity 时关闭服务器
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}