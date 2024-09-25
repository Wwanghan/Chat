package com.example.chat;

import android.app.Application;

import java.net.Socket;


// socket的中转，这个接口只有 get和set socket
// 当双方连接成功后，双方都会将socket保存在这个接口中。以方便到了新的页面中，快速通过这个接口获取socket

public class mySocket extends Application {

    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
