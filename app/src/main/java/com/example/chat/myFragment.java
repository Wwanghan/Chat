package com.example.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;


public class
myFragment extends Fragment {

    file_os fs = new file_os();  // new一个我自己写的文件类

    String info_content;  // 用于存放读取的文件信息
    private View view;
    Button wlan_connect_btn;
    private Button to_settings;
    private Button to_aboutMe;
    private ImageButton toPersonalInformation;

    private TextView chat;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

          // UID暂时取消动态获取
//        TextView uid = view.findViewById(R.id.UID);
//        uid.setText(info_content);

        wlan_connect_btn = view.findViewById(R.id.WLAN_CONNECT_BTN);
        to_settings = view.findViewById(R.id.Settings);
        to_aboutMe = view.findViewById(R.id.aboutMe);
        toPersonalInformation = view.findViewById(R.id.personalInformation);

        // 获取本机IP地址并显示在页面上
        TextView localIpAddress = view.findViewById(R.id.localIp);
        String ipAddress = getIpAddress(getContext());
        localIpAddress.setText("IP: " + ipAddress);

        wlan_connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = new EditText(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                lp.setMargins(40 , 20 , 0 , 0);
                input.setLayoutParams(lp);

                // 给输入框设置自定义的样式
                input.setBackgroundResource(R.drawable.input_style);

                // 等比例缩小输入框的大小，太大将对话框宽度占满，不好看
                input.setScaleX(0.9f);
                input.setScaleY(0.9f);

                // 创建一个自定义的 TextView 作为标题
                TextView title = new TextView(getActivity());
                title.setText("请输入连接的内网IP");
                title.setTextSize(20); // 设置标题字体大小
                title.setGravity(Gravity.START); // 标题左对齐
                title.setPadding(55, 30, 0, 10); // 设置标题的 padding (相当于 margin)，对位置进行微调

                // 创建 AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                // 使用自定义的标题
                builder.setCustomTitle(title);
                // 使用输入框
                builder.setView(input);

                // 设置“确认”按钮
                builder.setPositiveButton("确认", (dialog, which) -> {
                    String connect_ip = input.getText().toString();
                    String serverAddress = connect_ip; // 服务端地址
                    int serverPort = 9231; // 服务端端口

                    if (!input.getText().toString().isEmpty()){
                        // 启动新的线程进行网络连接
                        new Thread(() -> {
                            try {
                                // 创建一个带有超时设置的 SocketAddress
                                InetSocketAddress socketAddress = new InetSocketAddress(serverAddress, serverPort);
                                Socket socket = new Socket();

                                // 设置超时时间为1秒，因为是内网，只要确保连接的IP是正确的，连接速度很快的，1秒够用了
                                socket.connect(socketAddress, 1000);

                                // 如果连接成功，设置 socket
                                ((dataHub) getActivity().getApplication()).setSocket(socket);

                                // 更新 UI，显示成功提示并跳转页面
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getActivity(), "已连接到服务器 : " + serverAddress + ":" + serverPort, Toast.LENGTH_SHORT).show();

                                    // 连接成功后跳转到新页面
                                    Intent intent = new Intent(getActivity(), Chat.class); // 替换为你的新页面类名
                                    startActivity(intent);
                                    getActivity().finish(); // 可选，关闭当前页面
                                });

                            } catch (SocketTimeoutException e) {
                                // 处理连接超时的情况
                                getActivity().runOnUiThread(() ->
                                        Toast.makeText(getActivity(), "连接超时，请检查IP地址和网络。", Toast.LENGTH_SHORT).show()
                                );
                            } catch (UnknownHostException e) {
                                // 处理无法解析IP地址的情况
                                getActivity().runOnUiThread(() ->
                                        Toast.makeText(getActivity(), "无效的IP地址: " + serverAddress, Toast.LENGTH_SHORT).show()
                                );
                            } catch (IOException e) {
                                // 处理其他 I/O 异常 (例如网络问题)
                                getActivity().runOnUiThread(() ->
                                        Toast.makeText(getActivity(), "无法连接到服务器: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                            } catch (Exception e) {
                                // 捕获其他异常
                                getActivity().runOnUiThread(() ->
                                        Toast.makeText(getActivity(), "连接失败: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                            }
                        }).start();
                    }else {
                        Toast.makeText(getActivity() , "输入框不能为空" , Toast.LENGTH_SHORT).show();
                    }
                });

                // 设置“取消”按钮
                builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

                AlertDialog dialog = builder.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
                // 显示对话框
                dialog.show();
            }
        });

        // 去到设置页面
        to_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_settings = new Intent(getActivity() , activity_settings.class);
                startActivity(intent_settings);

            }
        });

        // 去到关于我页面
        to_aboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_aboutMe = new Intent(getActivity() , activity_aboutMe.class);
                startActivity(intent_aboutMe);
            }
        });

        // 去到个人信息页面
        toPersonalInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_personal_information = new Intent(getActivity() , personal_information.class);
                startActivity(intent_personal_information);
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UID暂时取消动态获取
//        动态请求读写权限
//        if (ContextCompat.checkSelfPermission(getActivity() , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(getActivity() , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(getActivity() , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE} , 1);
//        }else {
//            getUserUid();
//        }

    }
    // UID暂时取消动态获取
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == 1) {
//            // 检查请求结果是否包含读写权限
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                // 用户同意了读写权限请求，执行文件操作
//                getUserUid();
//            } else {
//                Log.i("toad", "onRequestPermissionsResult: no");
//            }
//        }
//    }
      // UID暂时取消动态获取
//    public void getUserUid(){
//        // 最开始先执行这儿的代码，使用readFromFile读取文件时，这个函数先会判断本地Download下到底有没有这个文件
//        // 如果有，那么最后会返回文件内容，info_content就可以接收到文件中的数据。
//        // 如果找不到要读的文件，则返回null，那么下面的if语句成立，于是先创建文件，再读取文件。
//        info_content = fs.readFromFile("chat_personalInformation.txt" , getActivity());
//        if (info_content == null){
//            fs.writeToFile("chat_personalInformation.txt" , "10000" , getActivity());
//            info_content = fs.readFromFile("chat_personalInformation.txt" , getActivity());
//        }
//    }

    // 用于获取本机IP地址，优先获取wifi,其次是数据网络
    public static String getIpAddress(Context context) {
        // 优先获取Wi-Fi的IP地址
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        // 判断手机的wifi是否开启，如果开启则进入if开始获取本机连接的wifi的地址
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();

            if (ipAddress != 0) {
                // 转换Wi-Fi的IP地址为小数点分割的字符串形式返回
                return (ipAddress & 0xFF) + "." +
                        ((ipAddress >> 8) & 0xFF) + "." +
                        ((ipAddress >> 16) & 0xFF) + "." +
                        ((ipAddress >> 24) & 0xFF);
            }
        }

        // 如果Wi-Fi不可用，则获取移动数据网络的IP地址
        String mobileIpAddress = getMobileNetworkIpAddress(context);
        if (mobileIpAddress != null) {
            return mobileIpAddress;
        }

        // Wi-Fi和数据网络都不可用时，返回提示
        return "未获取本机IP地址";
    }

    // 获取移动数据网络的IP地址
    private static String getMobileNetworkIpAddress(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Network activeNetwork = cm.getActiveNetwork();
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);

            if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                // 移动数据网络连接时，获取IP地址
                return getIpAddressFromNetworkInterfaces();
            }
        }
        return null;
    }

    // 从网络接口中获取IP地址
    private static String getIpAddressFromNetworkInterfaces() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress() && addr instanceof java.net.Inet4Address) {
                        return addr.getHostAddress();  // 返回IPv4地址
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}

