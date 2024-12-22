package com.example.chat;


import android.app.Application;
import android.content.Context;
import android.net.Uri;
import java.net.Socket;

import Constants.GlobalDataConstants;
import Utils.SPDataUtils;

public class dataHub extends Application {

    private Socket Socket;
    private int Delay;  // AI流对话延迟
    private String UID; // 保存用户名
    private String userName;
    private String Gender;
    private String targetName; // 保存对方的名字
    private String ipAddress;
    private String createTime;
    private String phoneNumber;
    private String isLogin;

    private Uri myAvatar = null;

    public void getConfig(){
        // 程序最开始运行时，先从本地读取一些后面要用到的数据信息

        Context context = getBaseContext();
        ensureDefaultValue(context , GlobalDataConstants.KEY_UID , GlobalDataConstants.DEFAULT_UID);
        ensureDefaultValue(context , GlobalDataConstants.KEY_IS_LOGIN , GlobalDataConstants.DEFAULT_IS_LOGIN);
        ensureDefaultValue(context , GlobalDataConstants.KEY_USERNAME , GlobalDataConstants.DEFAULT_USERNAME);
        ensureDefaultValue(context , GlobalDataConstants.KEY_GENDER , GlobalDataConstants.DEFAULT_GENDER);
        ensureDefaultValue(context , GlobalDataConstants.KEY_PHONE_NUMBER , GlobalDataConstants.DEFAULT_PHONE_NUMBER);
        ensureDefaultValue(context , GlobalDataConstants.KEY_CREATE_TIME , GlobalDataConstants.DEFAULT_CREATE_TIME);
        ensureDefaultValue(context , GlobalDataConstants.KEY_STREAM_DELAY , GlobalDataConstants.DEFAULT_STREAM_DELAY);

        this.isLogin = SPDataUtils.getStorageInformation(getBaseContext() , GlobalDataConstants.KEY_IS_LOGIN);
        this.UID = SPDataUtils.getStorageInformation(getBaseContext() , GlobalDataConstants.KEY_UID);
        this.userName = SPDataUtils.getStorageInformation(getBaseContext() , GlobalDataConstants.KEY_USERNAME);
        this.Gender = SPDataUtils.getStorageInformation(getBaseContext() , GlobalDataConstants.KEY_GENDER);
        this.phoneNumber = SPDataUtils.getStorageInformation(getBaseContext() , GlobalDataConstants.KEY_PHONE_NUMBER);
        this.createTime = SPDataUtils.getStorageInformation(getBaseContext() , GlobalDataConstants.KEY_CREATE_TIME);
        this.Delay = Integer.parseInt(SPDataUtils.getStorageInformation(getBaseContext() , GlobalDataConstants.KEY_STREAM_DELAY));
    }

    private void ensureDefaultValue(Context context, String key, String defaultValue) {
        if (SPDataUtils.getStorageInformation(context, key) == null) {
            SPDataUtils.storageInformation(context, key, defaultValue);
        }
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

    public void setPhoneNumber(String phoneNumber){ this.phoneNumber = phoneNumber; }
    public String getPhoneNumber(){ return this.phoneNumber; }

    public void setCreate_time(String createTime){ this.createTime = createTime; }
    public String getCreate_time(){ return this.createTime; }


    public void setAvatar(Uri Avatar){ this.myAvatar = Avatar; }
    public Uri getAvatar(){ return this.myAvatar; }

    public void setIsLogin(String islogin){ this.isLogin = islogin; }
    public String getIsLogin(){ return this.isLogin; }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }
}
