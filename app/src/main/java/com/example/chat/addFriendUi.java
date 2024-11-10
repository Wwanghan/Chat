package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class addFriendUi extends AppCompatActivity {

    private EditText searchFriendInput;
    private Button searchFriendBtn;
    private LinearLayout searchResultArea;

    private Handler handler;

    private int UID;
    private String NAME;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_ui);

        searchFriendInput = findViewById(R.id.searchFriendInput);
        searchFriendBtn = findViewById(R.id.searchFriendBtn);
        searchResultArea = findViewById(R.id.searchResultArea);

        searchFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                获取输入框的内容，赋值给friendUid变量，之后还需判断这个变量是否为空
//                如果变量不为空，则执行searchFriend逻辑函数，搜索对应好友并显示在搜索框下方
//                如果变量为空，则表示输入框为空，则不做任何事，并提醒用户输入框内容为空
                String friendUid = searchFriendInput.getText().toString();
                if (!TextUtils.isEmpty(friendUid)){
//                    searchFriend(friendUid);
                }else {
                    Toast.makeText(addFriendUi.this  , "输入的内容为空" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    执行搜索好友的逻辑
//    private void searchFriend(String friendUid) {
//        get_mysql_data myDB = new get_mysql_data();
//        handler = new Handler(Looper.getMainLooper());
//
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                String result = myDB.executeSql("Chat" ,"select * from Users where UID = " + friendUid , "query");
////                Log.i("toad", "result = " + result);
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
////                      给NAME和UID设置一个初始值。如果result为空，也就是没有通过UID或名字找到对应的好友
////                      那么result的结果是[]，那么下面也就不会进入for循环，NAME和UID的值也就不会更新。
////                      所以，下面两行代码是必要的
//                        NAME = null;
//                        UID = -1;
//                        try {
//                            JSONArray jsonArray = new JSONArray(result);
//
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                UID = jsonObject.getInt("UID");
//                                NAME = jsonObject.getString("NAME");
//                            }
////                          判断是否找到了数据，如果搜索的结果为空，那么NAME一定为Null
//                            if (NAME == null){
//                                get_card("未找到好友" , -1);
//                            }else {
//                                get_card(NAME, UID);
//                            }
//
//                        } catch (JSONException e){
//                            throw new RuntimeException(e);
//                        }
//                    }
//                });
//            }
//        }).start();
//    }

    public void get_card(String name , int Uid){
        Log.i("toad", "get_card: name = " + name + "uid = " + Uid);
//        判断当前布局中的控件数量，如果大于0，表示输入框下方肯定有控件
//        则删除当前所有控件，再生成新的控件
        if (searchResultArea.getChildCount() > 0){
            searchResultArea.removeAllViews();
        }

        searchResultArea.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                200 // 高度为 100dp
        ));
        searchResultArea.setOrientation(LinearLayout.HORIZONTAL);

        // 创建 ImageView
        ImageView avatar = new ImageView(this);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(150, 150); // 宽高为 60dp
        avatarParams.setMargins(20, 20, 0, 0); // 左边距 10dp, 上边距 20dp
        avatar.setLayoutParams(avatarParams);
        avatar.setImageResource(R.mipmap.mrtoad);

        // 创建第一个 TextView (用于显示名字)
        TextView friendName = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        nameParams.setMargins(30, -20, 0, 0); // 左边距 10dp, 上边距 30dp
        friendName.setLayoutParams(nameParams);
        friendName.setText(name);

        // 添加按钮
        Button addFriend_btn = new Button(this);
        LinearLayout.LayoutParams addFriend_btn_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        addFriend_btn_params.setMargins(0 , 20 , 20 , 0);
        addFriend_btn.setLayoutParams(addFriend_btn_params);

        addFriend_btn.setText("添加好友");


        // 将创建的控件添加到 LinearLayout 中
        searchResultArea.addView(avatar);
        searchResultArea.addView(friendName);
        searchResultArea.addView(addFriend_btn);
    }
}