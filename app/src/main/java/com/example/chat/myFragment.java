package com.example.chat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class
myFragment extends Fragment {

    file_os fs = new file_os();  // new一个我自己写的文件类

    String info_content;  // 用于存放读取的文件信息
    private View view;
    private TextView chat;
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView uid = view.findViewById(R.id.UID);
        uid.setText(info_content);

        Button wlan_connect_btn = view.findViewById(R.id.WLAN_CONNECT_BTN);

        wlan_connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = new EditText(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);

                // 创建 AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("请输入连接的内网IP");
                builder.setView(input);

                // 设置“确认”按钮
                builder.setPositiveButton("确认", (dialog, which) -> {
                    String connect_ip = input.getText().toString();
                    String serverAddress = connect_ip; // 服务端地址
                    int serverPort = 9231; // 服务端端口

                    // 启动新的线程进行网络连接
                    new Thread(() -> {
                        try {
                            // 连接到服务器i
                            Socket socket = new Socket(serverAddress, serverPort);

                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getActivity(), "已连接到服务器 : " + serverAddress + ":" + serverPort, Toast.LENGTH_SHORT).show()
                            );

                            // 用于读取服务器的响应
                            BufferedReader s_input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            // 用于向服务器发送消息
                            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                            // 测试发送一条数据
                            String messageToSend = "this is a test message";
                            output.println(messageToSend);

                            // 读取服务端的响应
                            String responseFromServer = s_input.readLine();
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getActivity(), "服务端响应: " + responseFromServer, Toast.LENGTH_SHORT).show()
                            );

                            // 关闭资源
                            s_input.close();
                            output.close();
                            socket.close();

                            ((mySocket) getActivity().getApplication()).setSocket(socket);
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getActivity(), "已连接到服务器 : " + serverAddress + ":" + serverPort, Toast.LENGTH_SHORT).show();

                                // 连接成功后跳转到新页面
                                Intent intent = new Intent(getActivity(), Chat.class); // 替换为你的新页面类名
                                startActivity(intent);
                                getActivity().finish(); // 可选，关闭当前页面
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getActivity(), "连接失败: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                        }
                    }).start();
                });

                // 设置“取消”按钮
                builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

                // 显示对话框
                builder.show();
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        动态请求读写权限
        if (ContextCompat.checkSelfPermission(getActivity() , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(getActivity() , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity() , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE} , 1);
        }else {
            getUserUid();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            // 检查请求结果是否包含读写权限
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意了读写权限请求，执行文件操作
                getUserUid();
            } else {
                Log.i("toad", "onRequestPermissionsResult: no");
            }
        }
    }

    public void getUserUid(){
        // 最开始先执行这儿的代码，使用readFromFile读取文件时，这个函数先会判断本地Download下到底有没有这个文件
        // 如果有，那么最后会返回文件内容，info_content就可以接收到文件中的数据。
        // 如果找不到要读的文件，则返回null，那么下面的if语句成立，于是先创建文件，再读取文件。
        info_content = fs.readFromFile("chat_personalInformation.txt" , getActivity());
        if (info_content == null){
            fs.writeToFile("chat_personalInformation.txt" , "10000" , getActivity());
            info_content = fs.readFromFile("chat_personalInformation.txt" , getActivity());
        }
    }


}
