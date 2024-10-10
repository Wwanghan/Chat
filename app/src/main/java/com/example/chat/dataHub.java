package com.example.chat;

import android.app.Application;

import java.net.Socket;

public class dataHub extends Application {

    private Socket socket;
    private int Delay;  // AI流对话延迟

    file_os fs = new file_os();

    public void getConfig(){
        this.Delay = Integer.parseInt(fs.readConfig("chatConfig.conf" , "streamDelay" , getBaseContext()));
    }


    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }


    public void setDelay(int delay){ this.Delay = delay; }
    public int getDelay(){ return this.Delay; }



}
