package com.example.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import Utils.SPDataUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class
myFragment extends Fragment {
    // 用于存放读取的文件信息
    String info_content;
    private View view;
    private Button wlanConnectBtn;
    private Button Settings;
    private Button aboutMe;
    private ImageButton personalInformation;
    private TextView userName;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView myAvatar;
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
        // 导入自己封装的对话框
        DialogUtils dialogUtils = new DialogUtils();

        wlanConnectBtn = view.findViewById(R.id.wlanConnectBtn);
        Settings = view.findViewById(R.id.Settings);
        aboutMe = view.findViewById(R.id.aboutMe);
        personalInformation = view.findViewById(R.id.personalInformation);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        userName = view.findViewById(R.id.userName);
        myAvatar = view.findViewById(R.id.myAvatar);

        // 判断用户是否登录
        Log.i("toad", "onViewCreated: " + ((dataHub) getActivity().getApplication()).getIsLogin());
        if (((dataHub) getActivity().getApplication()).getIsLogin().equals("true")){
            userName.setText("Name : " + ((dataHub) getActivity().getApplication()).getName());
        }else {
            userName.setText("请您注册/登录");
            userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent LAR_Page = new Intent(getActivity() , LAR_mainActivity.class);
                    startActivity(LAR_Page);
                }
            });
        }

        // 获取本机IP地址并显示在页面上
        getIpAddress(getContext());

        // 从 SharedPreferences 加载之前保存的头像，如果之前用户有修改过头像，那么可以直接读取并设置用户自定义选择的头像
        // 如果从 SharedPreferences 读取不到数据，那么表示用户没有自定义选择头像，则使用默认的青蛙头像
        String avatarUriString = Utils.SPDataUtils.getStorageInformation(getContext() , "avatarUri");
        if (avatarUriString != null) {
            Uri avatarUri = Uri.parse(avatarUriString);
            try {
                ((dataHub) getActivity().getApplication()).setAvatar(avatarUri);
                myAvatar.setImageURI(((dataHub) getActivity().getApplication()).getAvatar());
                // 使用 Glide 加载图片并裁剪为圆形
                Glide.with(this).load(((dataHub) getActivity().getApplication()).getAvatar()).circleCrop().into(myAvatar);
            } catch (SecurityException e) {
                Log.e("MainActivity", "Uri 权限失效: " + e.getMessage());
            }
        }else {
            myAvatar.setImageResource(R.mipmap.mrtoad);
            // 使用 Glide 加载图片并裁剪为圆形
            Glide.with(this).load(R.mipmap.mrtoad).circleCrop().into(myAvatar);
        }

        wlanConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建输入框
                EditText input = new EditText(getActivity());
                // 调用封装的对话框函数
                DialogUtils.showDialog(getActivity(),  "input" ,"请输入连接的内网IP", "确认" , input , (dialog, which) -> {
                    String connect_ip = input.getText().toString();
                    String serverAddress = connect_ip;
                    int serverPort = 9231;
                    if (!connect_ip.isEmpty()) {
                        // 执行连接操作
                        new Thread(() -> {
                            try {
                                InetSocketAddress socketAddress = new InetSocketAddress(serverAddress, serverPort);
                                Socket socket = new Socket();
                                socket.connect(socketAddress, 1000); // 设置超时

                                ((dataHub) getActivity().getApplication()).setSocket(socket);
                                getActivity().runOnUiThread(() -> {
                                    // 连接成功后跳转到聊天页面
                                    Toast.makeText(getActivity(), "已连接到服务器 : " + serverAddress + ":" + serverPort, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), Chat.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                });
                            } catch (UnknownHostException e){
                                // 处理无法解析IP地址的情况
                                getActivity().runOnUiThread(() ->
                                        Toast.makeText(getActivity(), "无效的IP地址: " + serverAddress, Toast.LENGTH_SHORT).show()
                                );
                            } catch (SocketTimeoutException e){
                                // 处理连接超时的情况
                                getActivity().runOnUiThread(() ->
                                        Toast.makeText(getActivity(), "连接超时，请检查IP地址和网络。", Toast.LENGTH_SHORT).show()
                                );
                            }
                            catch (Exception e) {
                                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "连接失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        }).start();
                    } else {
                        // 输入框为空时显示提示
                        Toast.makeText(getActivity(), "输入框不能为空", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        // 去到设置页面
        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_settings = new Intent(getActivity() , activity_settings.class);
                startActivity(intent_settings);

            }
        });

        // 去到关于我页面
        aboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_aboutMe = new Intent(getActivity() , activity_aboutMe.class);
                startActivity(intent_aboutMe);
            }
        });

        // 去到个人信息页面
        personalInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_personal_information = new Intent(getActivity() , personal_information.class);
                startActivity(intent_personal_information);
            }
        });

        // 刷新页面，调用refreshPage获取最新数据
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPage();
            }
        });

    }

    private void refreshPage() {
        // 模拟数据刷新操作，300毫秒后结束刷新
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 数据加载完成，隐藏刷新动画
                // 这里目前只是在本地获取最新数据，不花费什么时间，后面如果在数据库上获取最新数据，时间就不能写死。
                getIpAddress(getContext());
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 300);
    }

    // 用于获取本机IP地址，优先获取wifi,其次是数据网络
    public int getIpAddress(Context context) {
        // 优先获取Wi-Fi的IP地址
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        // 判断手机的wifi是否开启，如果开启则进入if开始获取本机连接的wifi的地址
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();

            if (ipAddress != 0) {
                // 转换Wi-Fi的IP地址为小数点分割的字符串形式返回
                String IP_ADDRESS =  (ipAddress & 0xFF) + "." +
                        ((ipAddress >> 8) & 0xFF) + "." +
                        ((ipAddress >> 16) & 0xFF) + "." +
                        ((ipAddress >> 24) & 0xFF);
                ((dataHub) getActivity().getApplication()).setIpAddress(IP_ADDRESS);
                return 1;
            }
        }

        // 如果Wi-Fi不可用，则获取移动数据网络的IP地址
        String mobileIpAddress = getMobileNetworkIpAddress(context);
        if (mobileIpAddress != null) {
            ((dataHub) getActivity().getApplication()).setIpAddress(mobileIpAddress);
            return 1;
        }

        // Wi-Fi和数据网络都不可用时，返回提示
        return 0;
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

    @Override
    public void onResume() {
        super.onResume();
        if (((dataHub) getActivity().getApplication()).getIsLogin().equals("true")){
            userName.setText("Name : " + ((dataHub) getActivity().getApplication()).getName());
        }else {
            userName.setText("请您注册/登录");
        }

        String avatarUriString = SPDataUtils.getStorageInformation(getContext() , "avatarUri");
        if (avatarUriString != null) {
            Uri avatarUri = Uri.parse(avatarUriString);
            try {
                ((dataHub) getActivity().getApplication()).setAvatar(avatarUri);
                myAvatar.setImageURI(((dataHub) getActivity().getApplication()).getAvatar());
                // 使用 Glide 加载图片并裁剪为圆形
                Glide.with(this).load(((dataHub) getActivity().getApplication()).getAvatar()).circleCrop().into(myAvatar);
            } catch (SecurityException e) {
                Log.e("MainActivity", "Uri 权限失效: " + e.getMessage());
            }
        }else {
            myAvatar.setImageResource(R.mipmap.mrtoad);
            // 使用 Glide 加载图片并裁剪为圆形
            Glide.with(this).load(R.mipmap.mrtoad).circleCrop().into(myAvatar);
        }

    }
}

