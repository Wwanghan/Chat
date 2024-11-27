package com.example.chat;

import android.app.Application;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Handler;

import Utils.SPDataUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class dataHub extends Application {

    private Socket Socket;
    private int Delay;  // AI流对话延迟
    private String UID; // 保存用户名
    private String userName;
    private String targetName; // 保存对方的名字
    private String ipAddress;

    private String isLogin;

    private Uri myAvatar = null;

    public void getConfig(){
        // 程序最开始运行时，先将一些默认值存储
        // 这里使用变量 isFirst 判断用户是否是第一次打开App, 如果是, 那么会加载默认值，反之则不会
        String isFirst = null;
        isFirst = Utils.SPDataUtils.getStorageInformation(getBaseContext() , "isFirst");
        if (isFirst == null){
            Utils.SPDataUtils.storageInformation(getBaseContext() , "isFirst" , "true");
            Utils.SPDataUtils.storageInformation(getBaseContext() , "userName" , "userNull");
            Utils.SPDataUtils.storageInformation(getBaseContext() , "UID" , "uidNull");
            Utils.SPDataUtils.storageInformation(getBaseContext() , "isLogin" , "false");
            Utils.SPDataUtils.storageInformation(getBaseContext() , "streamDelay" , "50");
        }

        // 将默认值赋值，方便后面从这儿取数据
        this.userName = Utils.SPDataUtils.getStorageInformation(getBaseContext() , "userName");
        this.UID = Utils.SPDataUtils.getStorageInformation(getBaseContext() , "UID");
        this.isLogin = Utils.SPDataUtils.getStorageInformation(getBaseContext() , "isLogin");
        this.Delay = Integer.parseInt(SPDataUtils.getStorageInformation(getBaseContext() , "streamDelay"));
    }


    public Socket getSocket() {
        return Socket;
    }
    public void setSocket(Socket socket) {
        this.Socket = socket;
    }


    public void setDelay(int delay){ this.Delay = delay; }
    public int getDelay(){ return this.Delay; }

    public void setUID(String UID){ this.UID = UID; }
    public String getUID(){ return this.UID; }

    public void setName(String name){ this.userName = name; }
    public String getName(){ return this.userName; }

    public void setTargetName(String targetName){ this.targetName = targetName; }
    public String getTargetName(){ return this.targetName; }

    public void setIpAddress(String ipaddress) { this.ipAddress = ipaddress; }
    public String getIpAddress(){ return this.ipAddress; }


    public void setAvatar(Uri Avatar){ this.myAvatar = Avatar; }
    public Uri getAvatar(){ return this.myAvatar; }

    public void setIsLogin(String islogin){ this.isLogin = islogin; }
    public String getIsLogin(){ return this.isLogin; }
}
