package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatFragment = new chatFragment();
        myFragment = new myFragment();

        RadioButton chatNav = findViewById(R.id.chat_nav);
        chatNav.setChecked(true);

        // 默认显示聊天 Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, chatFragment).commit();

        RadioGroup rgGroup = findViewById(R.id.rg_group);

        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

                // 启动一个线程来处理客户端连接
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 客户端处理线程
    private class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

                String messageFromB;
                while ((messageFromB = input.readLine()) != null) {
                    Log.i("toad", "收到客户端消息: " + messageFromB);

                    // 检测到退出命令时，关闭当前客户端连接
                    if ("exit".equalsIgnoreCase(messageFromB.trim())) {
                        Log.i("toad", "客户端请求断开连接");
                        output.println("连接关闭");
                        break; // 退出循环，关闭连接
                    }

                    output.println("收到消息: " + messageFromB); // 回复客户端
                }

                // 关闭输入输出流和套接字
                input.close();
                output.close();
                clientSocket.close();
                Log.i("toad", "客户端已断开连接");
            } catch (IOException e) {
                e.printStackTrace();
            }
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