package com.example.chat;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ScrollView;

import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.baidubce.qianfan.model.chat.ChatResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Chat extends AppCompatActivity {

    private LinearLayout chatLayout;
    private EditText messageInput;
    private ScrollView scrollView;
    private  Qianfan qianfan;      // 声明千帆大模型
    private Button sendButton;
    private TextView showObject ;
    private String FN;


    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private TextView textViewChat;
    // Handler 用于接受子线程传递过来的参数，
    private Handler M_Handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

                // 这里传递过来了一个标识数字0，表示询问千帆大模型问题后返回成果数据了
                if (msg.what == 0){
                    showObject.setText(FN);
                    String aiRespContent = (String) msg.obj;
                    addMessage(aiRespContent , FN);
                    scrollToBottom();
                }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Friend Name ，用于表示好友名字的变量
        FN = getIntent().getStringExtra("friend_name");

        // 这里判断FN是否为NULL,如果不为NULL,则表明是从聊天页面进入的，那么FN是可以获取到一个值的，同时也不需要获取socket
        // 只有通过内网连接，双方连接成功后，才会有socket，如果将获取socket的代码写在if的外面，那么获取不到socket则会闪退
        // 如果是从内网连接方式双方连接成功后跳转到的聊天页面的话，那么FN是没有值的，为Null，if成立，进入if获取socket,开始聊天
        if (FN == null) {
            FN = "tmp_connect";
            socket = ((mySocket) getApplication()).getSocket();
            if (socket != null && socket.isConnected()) {
                try {
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("toad", "Socket 输入输出流初始化失败: " + e.getMessage());
                }

                // 开启一个线程监听消息接收
                try {
                    new Thread(() -> receiveMessages()).start();
                    Log.i("toad", "消息监听线程已启动");
                } catch (Exception e) {
                    Log.i("toad", "onCreate: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                Log.i("toad", "Socket 连接失败");
            }
        }


        qianfan = new Qianfan(Auth.TYPE_OAUTH , "5B0KyLICmzUL9agq7cg7mguR", "sqq17nJwzfalPhnExjbQ7FTwjVzPIibz");
        chatLayout = findViewById(R.id.chatLayout);
        scrollView = findViewById(R.id.scrollView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        showObject = findViewById(R.id.showObject);

        showObject.setText(FN);

        messageInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    scrollToBottom();
                }else {
                    Log.i("toad", "onFocusChange: cannel Focus");
                }
            }
        });

        // 监听用户是否点击了聊天窗口中的空白处，如果是，则隐藏用户键盘
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                messageInput.clearFocus();
//                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);//关闭输入法
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(messageInput.getWindowToken() , 0);

                view.performClick();
                return false;
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // 当用户点击发送按钮之后，获取输入框中的内容，并保存在message变量当中
                    String message = messageInput.getText().toString();

                    // 这里判断message是否为空，这里通过取反来判断，如果为空，表示用户并没有在输入框中输入任何东西，那么也不必发送消息
                    if (!message.isEmpty()){
                        // 发送并将输入框的内容清空
                        addMessage(message , "My");
                        messageInput.setText("");

                        // 模拟回复，聊天对象分为两类，一类是好友，另一个是AI语言大模型
                        // 判断当前聊天的对象的名字，如果是AI助手，则表示在跟AI聊天，则进入下面这个if分支，否则就是好友，走else。
                        if (FN.equals("AI助手")){
                            // 这里需要调用大模型接口，需要请求网络。所以这里需要开启一个子线程去执行网络请求
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // 调用我自己封装好的函数，函数封装了请求大模型的功能，只需要传递给它一个字符串，也就是你跟他聊天你的内容。
                                    // 这个内容上面已经保存在了message这个变量当中
                                    String aiResp = GetQianFanResponse(message);

                                    // 声明一个消息对象，用来存储大模型返回回来的消息结果，再给它设置一个唯一的数字表示0，方便主线程接收
                                    Message respMessage = new Message();
                                    respMessage.what = 0;
                                    respMessage.obj = aiResp;
                                    // 将信息发送给主线程
                                    M_Handler.sendMessage(respMessage);
                                }
                            }).start();
                            showObject.setText(FN + "  发送成功，请等待AI回复...");

                        // 如果是通过内网连接成功的双方，FN标志会是tmp_connect,会进入这个if
                        } else if (FN.equals("tmp_connect")) {
                            // 开启子线程，将信息发送给对方
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        output.println(message);
                                        output.flush();
                                    } catch (Throwable t) {
                                        Log.i("toad", "Error: " + t.getMessage());
                                        t.printStackTrace();
                                    } finally {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // 等信息发送完毕后，再滑动滚轮
                                                scrollToBottom();
                                            }
                                        });
                                    }
                                }
                            }).start();
                        }
                    }else {
                        // 如果输入框为空，会执行下方代码，输出提醒用户输入框为空
                        Toast.makeText(Chat.this , "输入框不能为空" , Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e){
                    e.printStackTrace();
                    Log.i("toad", "Error: " + e.getMessage());
                }
            }
        });
    }

    // 接收服务器的消息
    private void receiveMessages() {
        String message;
        try {
            while ((message = input.readLine()) != null) {
                Log.i("toad", "接收到消息: " + message);
                String finalMessage = message;
                runOnUiThread(() -> addMessage(finalMessage, FN)); // 更新UI，显示消息
                scrollToBottom();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("toad", "消息接收失败: " + e.getMessage());
        }
    }

    private String GetQianFanResponse(String message) {
        // 调用文心一言接口
        ChatResponse response = qianfan.chatCompletion()
                .model("ERNIE-Speed-128K")  // 替换为你实际使用的模型
                .addMessage("user", message)
                .temperature(0.7)
                .execute();
        return response.getResult();
    }

    //
    private void addMessage(String message , String identify) {
        // 初始化一个新的对话框（按钮）
        Button message_btn = new Button(this);

        // new一个布局对象，用于动态设置参数。这里使用了WRAP_CONTENT设置了按钮大小相对文本大小改变
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // 设置margin属性
        params.bottomMargin = 100;

        //设置文字
        message_btn.setText(message);

        // 判断身份，如果是My，则设置右对齐，再设置颜色。反之，则设置对框左对齐
        if (identify.equals("My")){
            params.gravity = Gravity.RIGHT;
            message_btn.setBackgroundResource(R.drawable.button_shape_my);
//            message_btn.setBackgroundColor(Color.parseColor("#2DC252"));
        }else {
            params.gravity = Gravity.LEFT;
            message_btn.setBackgroundResource(R.drawable.button_shape_opposition);
        }

        //给按钮对象应用布局
        message_btn.setLayoutParams(params);
        //最后，将对话框（按钮）添加到主屏幕
        chatLayout.addView(message_btn);
    }

    private void scrollToBottom() {
        // 将输入框和按钮固定在最底下
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

//  当任意一方退出聊天页面页面，socket会断开连接。对方也会退出页面
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
                Toast.makeText(this, "Socket 连接已关闭", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
