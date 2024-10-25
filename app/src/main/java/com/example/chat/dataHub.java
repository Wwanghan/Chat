package com.example.chat;

import android.app.Application;
import android.net.Uri;

import java.net.Socket;

public class dataHub extends Application {

    private Socket socket;
    private int Delay;  // AI流对话延迟
    private String Name; // 保存用户名
    private String targetName; // 保存对方的名字
    private String ipAddress;

    private Uri myAvatar = null;

    file_os fs = new file_os();

    public void getConfig(){
        this.Delay = Integer.parseInt(fs.readConfig("chatConfig.conf" , "streamDelay" , getBaseContext()));
        this.Name = fs.readConfig("chatConfig.conf" , "Name" , getBaseContext());
    }


    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }


    public void setDelay(int delay){ this.Delay = delay; }
    public int getDelay(){ return this.Delay; }

    public void setName(String name){ this.Name = name; }
    public String getName(){ return this.Name; }

    public void setTargetName(String targetName){ this.targetName = targetName; }
    public String getTargetName(){ return this.targetName; }

    public void setIpAddress(String ipaddress) { this.ipAddress = ipaddress; }
    public String getIpAddress(){ return this.ipAddress; }


    public void setAvatar(Uri Avatar){ this.myAvatar = Avatar; }
    public Uri getAvatar(){ return this.myAvatar; }


}
