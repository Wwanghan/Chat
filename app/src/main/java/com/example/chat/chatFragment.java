package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class chatFragment extends Fragment {

    private View view;
    private TextView chat;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        ImageButton introduceBtn = view.findViewById(R.id.add_friend_ui);
//        introduceBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent to_introduce = new Intent(getActivity() , addFriendUi.class);
//                startActivity(to_introduce);
//            }
//        });

        LinearLayout friend1 = view.findViewById(R.id.friend1);
        LinearLayout friend2 = view.findViewById(R.id.friend2);
        LinearLayout friend3 = view.findViewById(R.id.friend3);
        LinearLayout friend4 = view.findViewById(R.id.friend4);
        LinearLayout friend5 = view.findViewById(R.id.friend5);

        // 添加点击事件，跳转到聊天页面
        friend1.setOnClickListener(v -> openChatActivity("AI助手"));
        friend2.setOnClickListener(v -> openChatActivity("好友2"));
        friend3.setOnClickListener(v -> openChatActivity("好友3"));
        friend4.setOnClickListener(v -> openChatActivity("好友4"));
        friend5.setOnClickListener(v -> openChatActivity("好友5"));
        //暂时加二个，用做测试
    }

    private void openChatActivity(String friendName) {
        Intent intent = new Intent(getActivity(), Chat.class);
        intent.putExtra("friend_name", friendName);
        startActivity(intent);
    }
}
