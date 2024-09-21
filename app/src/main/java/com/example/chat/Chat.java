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

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.baidubce.qianfan.model.chat.ChatResponse;

public class Chat extends AppCompatActivity {

    private LinearLayout chatLayout;
    private EditText messageInput;
    private ScrollView scrollView;
    private  Qianfan qianfan;      // 声明千帆大模型
    private Button sendButton;
    private TextView showObject ;
    private String FN;
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

                // 当用户点击发送按钮之后，获取输入框中的内容，并保存在message变量当中
                String message = messageInput.getText().toString();

                // 这里判断message是否为空，这里通过取反来判断，如果为空，则不会进入这个if语句。
                if (!message.isEmpty()){
                    // 发送
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
                                String aiResp= GetQianFanResponse(message);

                                // 声明一个消息对象，用来存储大模型返回回来的消息结果，再给它设置一个唯一的数字表示0，最后方便主线程接收
                                Message respMessage = new Message();
                                respMessage.what = 0;
                                respMessage.obj = aiResp;

                                M_Handler.sendMessage(respMessage);
                            }
                        }).start();
                        showObject.setText(FN + "  发送成功，请等待AI回复...");

                        // 用户只是点击了发送按钮，那么message必然为空，代码也就会走到else这里
                        // 因为message为空，所以getReply不会返回任何东西，按钮自然就不会显示出来
                    }else {
                        addMessage(getReply(message) , FN);
                        scrollToBottom();
                    }

                }
            }
        });
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
//        Log.i("toad", "addMessage: " + message);
//        if (message.isEmpty()){
//            Log.i("toad", "addMessage: return");   // 这里后面再研究下，我这里想判断message是否为空，为空则直接返回，可是好像程序并没有走这里
//            return;
//        }
        // 初始化一个新的对话框（按钮）
        Button message_btn = new Button(this);

        // new一个布局对象，用于动态设置参数。这里使用了WRAP_CONTENT设置了按钮大小相对文本大小改变
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );


        params.bottomMargin = 100;

        //设置文字
        message_btn.setText(message);


        // 判断身份，如果是My，则设置左对齐，再设置颜色。
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

    private String getReply(String message) {
        // 模拟简单的回复
        switch (message) {
            case "你好呀":
                return "你好！";
            case "今天过得怎么样？":
                return "还不错，你呢？";
            default:
                return "哦？（统一回复）";
        }
    }

    private void scrollToBottom() {
        // 将输入框和按钮固定在最底下
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }
}

// 这是一个测试
