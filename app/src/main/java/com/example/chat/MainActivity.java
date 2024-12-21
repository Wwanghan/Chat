package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private  chatFragment chatFragment;
    private  myFragment myFragment;
    private ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((dataHub) getApplication()).getConfig();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置聊天页为默认页面
        loadFragment(new chatFragment());

        // 设置 BottomNavigationView 的监听事件
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.nav_frontPage) {
                fragment = new chatFragment();
            } else if (item.getItemId() == R.id.nav_my) {
                fragment = new myFragment();
            } else if (item.getItemId() == R.id.nav_friend) {
                fragment = new friendFragment();
            }
            return loadFragment(fragment);
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

    // 加载 Fragment
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_layout, fragment)
                    .commit();
            return true;
        }
        return false;
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