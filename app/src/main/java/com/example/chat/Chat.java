package com.example.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ScrollView;

import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.baidubce.qianfan.model.chat.ChatResponse;
import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    private ImageButton chatExit;
    private LinearLayout.LayoutParams scrollParams;
    private View rootView;
    private boolean isShowVirtualKeyBoard;
    private String FN;


    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private TextView textViewChat;

    private ImageButton avatar;
    // 用于存储接收到的图像
    private Bitmap targetAvatar;

    private Bitmap avatarBitmap;

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
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_chat);

        socket = null;
        isShowVirtualKeyBoard = false;
        // Friend Name ，用于表示好友名字的变量
        FN = getIntent().getStringExtra("friend_name");

        // 这里判断FN是否为NULL,如果不为NULL,则表明是从聊天页面进入的，那么FN是可以获取到一个值的，同时也不需要获取socket
        // 只有通过内网连接，双方连接成功后，才会有socket，如果将获取socket的代码写在if的外面，那么获取不到socket则会闪退
        // 如果是从内网连接方式双方连接成功后跳转到的聊天页面的话，那么FN是没有值的，为Null，if成立，进入if获取socket,开始聊天
        if (FN == null) {
            socket = ((dataHub) getApplication()).getSocket();
            if (socket != null && socket.isConnected()) {
                try {
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 添加短暂延迟，确保连接完成
                            try {
                                Thread.sleep(100); // 等待100毫秒
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // 发送名字和头像
                            output.println("__SEND__NAME__" + ((dataHub) getApplication()).getName());
                        }
                    }).start();

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

        qianfan = new Qianfan(Auth.TYPE_OAUTH , BuildConfig.API_QIANFAN_AK, BuildConfig.API_QIANFAN_SK);
        chatLayout = findViewById(R.id.chatLayout);
        scrollView = findViewById(R.id.scrollView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        showObject = findViewById(R.id.showObject);
        chatExit = findViewById(R.id.chatExit);
        rootView = findViewById(R.id.rootView);

        showObject.setText(FN);

        scrollParams = (LinearLayout.LayoutParams) scrollView.getLayoutParams();
        scrollParams.height = 0;
        scrollParams.weight = 1;
        scrollView.setLayoutParams(scrollParams);
        scrollView.requestLayout();

        // 这里在用户刚进入聊天页面时，先手动将按钮设置为不可点击状态，背景设置为灰色
        // 因为下方检测输入框内容是当用户输入了任意内容或删除了任意内容才会触发
        // 用户刚进入聊天页面，输入框自然为空，而刚进入页面不会触发下面的检测事件，所以要先手动将按钮设置为不可点击状态，并且背景设置为灰色
        sendButton.setEnabled(false);
        sendButton.setBackgroundResource(R.drawable.send_button_unenabled);
        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 如果我想知道用户删除或替换了哪些字符，或需要在文本发生前做某些校验，在这里写。目前暂时不需要
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 实时处理文本发生的变化，这里检测用户输入框的内容是否为空，对应设置按钮的状态
                // 如果输入框为空，则将按钮设置为不可点击状态，并将背景颜色设置为灰色
                // 如果输入框不为空，则将按钮设置为正常可点击状态，并将背景颜色设置为蓝色
                if (charSequence.toString().trim().isEmpty()){
                    sendButton.setEnabled(false);
                    sendButton.setBackgroundResource(R.drawable.send_button_unenabled);

                }else {
                    sendButton.setEnabled(true);
                    sendButton.setBackgroundResource(R.drawable.send_button_style);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 在用户输入后做某些事情，例如检查文本的合法性，或者格式化什么的，在这里写。目前暂时不需要
            }
        });

        messageInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    // 这里获取输入框焦点后，动态设置聊天区域的高度。并且将聊天区域滑动到最底部。
                    rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            // 获取根视图的可见区域
                            Rect rect = new Rect();
                            rootView.getWindowVisibleDisplayFrame(rect);

                            // 获取屏幕的整体高度
                            int screenHeight = rootView.getRootView().getHeight();

                            int keypadHeight = screenHeight - rect.bottom;

                            // 判断键盘是否弹出（键盘高度大于屏幕高度的15%）
                            if (keypadHeight > screenHeight * 0.15){
                                // 键盘显示时，仅当键盘之前不可见时，才执行
                                if (!isShowVirtualKeyBoard) {
                                    isShowVirtualKeyBoard = true;  // 更新标志位，表示键盘已显示
                                    scrollParams.height = rect.bottom - 400;  // 预留400px给输入框和按钮
                                    scrollParams.weight = 0;  // 移除权重
                                    scrollView.setLayoutParams(scrollParams);
                                    scrollView.requestLayout();
                                    // 延迟执行滚动到底部的操作，确保内容加载完毕
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            scrollToBottom();
                                        }
                                    }, 100);  // 延迟100毫秒（可以根据情况调整）
                                }
                            } else {
                                // 键盘收起时，仅当键盘之前是可见的，才执行
                                if (isShowVirtualKeyBoard) {
                                    isShowVirtualKeyBoard = false;  // 更新标志位，表示键盘已收起
                                    scrollParams.height = 0;  // 恢复到 weight 布局
                                    scrollParams.weight = 1;
                                    scrollView.setLayoutParams(scrollParams);
                                    scrollView.requestLayout();
                                    Log.i("toad", "onGlobalLayout: keyboard is hidden");
                                }
                            }
                        }
                    });
                }else {
                    scrollParams.height = 0;
                    scrollParams.weight = 1;
                    // 添加一定的延迟，以确保键盘动画完全结束
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isShowVirtualKeyBoard) {
                                scrollParams.height = 0;  // 恢复到 weight 布局
                                scrollParams.weight = 1;
                                scrollView.setLayoutParams(scrollParams);
                                scrollView.requestLayout();
                            }
                        }
                    }, 200);  // 延迟200毫秒执行，确保键盘完全收起
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
                        showObject.setText("发送成功，请等待AI回复...");

                        // 如果是通过内网连接成功的双方，FN标志会是tmp_connect,会进入这个if
                    } else {
                        // 开启子线程，将信息发送给对方
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    output.println(message);
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
                } catch (Exception e){
                    e.printStackTrace();
                    Log.i("toad", "Error: " + e.getMessage());
                }
            }
        });

//        当用户按下左上角退出按钮，聊天页面
        chatExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // 接收服务器的消息
    private void receiveMessages() {
        String message;
        try {
            // 这边循环接受信息的同时，也会检测socket连接是否断开，如果对方断开，则退出循环，执行finish退出当前页面
            while ((message = input.readLine()) != null && !socket.isClosed()) {
                Log.i("toad", "Received message: " + message);  // 打印每条接收到的消息
                // 用于检测对方发过来的用户名，分别是发送和回复。连接方刚连接成功后，会发送一个__SEND__NAME__的标识
                // 对方接收到带有__SEND__NAME__的标识，就会给FN设置对方的名字,再将自己的名字回复给对方，这样，双方就都有对方的名字了
                if (message.startsWith("__SEND__NAME__")){
                    // 剪切掉前面的 "__NAME__" 部分，只保留后面的内容
                    FN = message.substring(14);

                    runOnUiThread(() -> showObject.setText(FN));  // 在住线程更新
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            output.println("__REPLY__NAME__" + ((dataHub) getApplication()).getName());
                        }
                    }).start();
                    continue;
                }

                // 用于判断是否是对方回复的用户名
                if (message.startsWith("__REPLY__NAME__")){
                    FN = message.substring(15);
                    runOnUiThread(() -> showObject.setText(FN));
                    continue;
                }

                // 发送用户手动输入的信息
                String finalMessage = message;
                runOnUiThread(() -> addMessage(finalMessage, FN)); // 更新UI，显示消息
                scrollToBottom();
            }
            finish();
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

    // 在聊天界面添加一条动态添加一条消息
    private void addMessage(String message , String identify) {
        // 首先，创建一个 LinearLayout 来包含文本和 ImageButton
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);  // 设置为水平布局
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // 初始化一个新的对话框（按钮）
        Button message_btn = new Button(this);
        avatar = new ImageButton(this);

        int message_btnHeight = message_btn.getHeight();
        int tmp_height = message_btn.getHeight();

        // new一个布局对象，用于动态设置参数。这里使用了WRAP_CONTENT设置了按钮大小相对文本大小改变
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // 设置margin属性
        params.bottomMargin = 100;

        // 设置 ImageButton 的大小（确保图片不大）
        LinearLayout.LayoutParams imageButtonParams = new LinearLayout.LayoutParams(
                200, // 宽度，单位为像素，100px 只是示例，你可以根据需要调整
                200  // 高度，单位为像素
        );
        imageButtonParams.setMargins(0, -30, 0, 0);  // 设置左边距，以便与文本有间隔
        avatar.setLayoutParams(imageButtonParams);

        // 设置图片等比例缩放
        avatar.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        avatar.setAdjustViewBounds(true);  // 调整视图边界以保持图片比例

        // 设置 ImageButton 的背景为透明（如果不需要显示背景）
        avatar.setBackgroundColor(Color.TRANSPARENT);

        // 这里判断聊天的对象需要是AI助手，并且不是发送方的信息
        // 满足这两点，则增加的新信息使用流对话效果显示
        if (FN.equals("AI助手") && !identify.equals("My")){
            // 使用 Handler 来设置定时任务
            Handler handler = new Handler();
            int delay = ((dataHub) getApplication()).getDelay(); // 每个字符的显示间隔，单位是毫秒

            for (int i = 0; i < message.length(); i++) {
                final int index = i;  // 记录当前字符的索引
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 设置按钮文本为当前字符之前的所有字符
                        // 这里通过实时获取文本的高度，保证当聊天内容超出聊天范围，程序会自动将聊天区域内容滑动到最底部
                        int message_btnHeight = message_btn.getHeight();

                        // 检测AI生成的内容是否超出聊天区域，超出则将页面滑至最底部，保证一个好的使用体验
                        if (tmp_height != message_btnHeight){
                            scrollToBottom();
                        }
                        int tmp_height = message_btnHeight;
                        // 循环生成内容
                        message_btn.setText(message.substring(0, index + 1));
                    }
                }, i * delay);  // 延迟显示每个字符
            }
        }else {
            //设置文字
            message_btn.setText(message);
        }

        //给按钮对象应用布局
        message_btn.setLayoutParams(params);

        // 判断身份，如果是My，则设置右对齐，再设置颜色。反之，则设置对框左对齐
        // 判断身份，如果是My，则设置右对齐，再设置颜色。反之，则设置对框左对齐
        if (identify.equals("My")) {
            // 设置图片资源，动态获取头像
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyApp" , Context.MODE_PRIVATE);
            String avatarUriString = sharedPreferences.getString("avatarUri", null);
            if (avatarUriString != null) {
                Uri avatarUri = Uri.parse(avatarUriString);
                try {
                    ((dataHub) getApplication()).setAvatar(avatarUri);
                    avatar.setImageURI(((dataHub) getApplication()).getAvatar());
                    // 将头像裁剪为圆形
                    Glide.with(this)
                            .load(avatarUri)
                            .circleCrop()
                            .into(avatar);

                } catch (SecurityException e) {
                    Log.e("MainActivity", "Uri 权限失效: " + e.getMessage());
                }
            }else {
                avatar.setImageResource(R.mipmap.mrtoad);
                // 将头像裁剪为圆形
                Glide.with(this)
                        .load(R.mipmap.mrtoad)
                        .circleCrop()
                        .into(avatar);
            }

            // 将整个 LinearLayout 右对齐
            linearLayout.setGravity(Gravity.RIGHT);
            message_btn.setBackgroundResource(R.drawable.button_shape_my);
            message_btn.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            linearLayout.addView(message_btn);
            linearLayout.addView(avatar);
        } else {
            linearLayout.setGravity(Gravity.LEFT);  // 将整个 LinearLayout 左对齐
            message_btn.setBackgroundResource(R.drawable.button_shape_opposition);

            // 根据是否为AI助手判断文本对齐方式
            if (FN.equals("AI助手")) {
                // 设置AI图片
                avatar.setImageResource(R.drawable.ai);
                // 将头像裁剪为圆形
                Glide.with(this).load(R.drawable.ai).circleCrop().into(avatar);
                message_btn.setGravity(Gravity.START | Gravity.CENTER_VERTICAL); // 设置文本左对齐
                message_btn.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START); // 设置文本从视图的开始位置对齐
            } else {
            //  这里设置对方的头像，目前还没用到服务器，所以对方头像默认小青蛙，后面将数据库搭建好后，从数据库获取用户头像
                targetAvatar = BitmapFactory.decodeResource(getResources() , R.mipmap.mrtoad);
                avatar.setImageBitmap(targetAvatar);
                Glide.with(this).load(targetAvatar).circleCrop().into(avatar);
                message_btn.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            }

            linearLayout.addView(avatar);
            linearLayout.addView(message_btn);
        }

        chatLayout.addView(linearLayout);
        scrollToBottom();
    }


    private void scrollToBottom() {
        // 将输入框和按钮固定在最底下
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    //  当任意一方退出聊天页面页面，socket会断开连接。对方也会退出页面
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socket != null){
            try {
                socket.close();
                Toast.makeText(this, "已断开连接！", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

