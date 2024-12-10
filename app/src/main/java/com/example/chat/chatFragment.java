package com.example.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import Animations.ViewTransitionAnimator;

public class chatFragment extends Fragment {

    private View view;
    private TextView chat;
    private EditText searchAnything;
    private ScrollView friendList;
    private TextView cancelSearch;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // 修改导航栏颜色
        Window window = getActivity().getWindow();
        window.setNavigationBarColor(getResources().getColor(R.color.background_nav));

        searchAnything = view.findViewById(R.id.search_anything);
        friendList = view.findViewById(R.id.home_scrollView);
        cancelSearch = view.findViewById(R.id.cancel_search);
        LinearLayout friend1 = view.findViewById(R.id.friend1);

        // 添加点击事件，跳转到聊天页面
        friend1.setOnClickListener(v -> openChatActivity("AI助手"));
        //暂时加二个，用做测试

        searchAnything.setOnTouchListener((v, event) -> {
            // 展示键盘
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchAnything, InputMethodManager.SHOW_FORCED);

            // 执行动画，谈出好友列表，显示取消文本按钮
            ViewTransitionAnimator.hideViewWithAnimation(friendList , -50f , 200 , null);
            ViewTransitionAnimator.showViewWithAnimation(cancelSearch , 50f , 200);

            return false;
        });

        cancelSearch.setOnClickListener(v -> {
            ViewTransitionAnimator.hideViewWithAnimation(cancelSearch , 50f , 200 , null);
            ViewTransitionAnimator.showViewWithAnimation(friendList , -50f , 200);

            searchAnything.clearFocus();

            // 隐藏键盘
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchAnything.getWindowToken(), 0);

            if (searchAnything.getText().toString() != null){
                searchAnything.setText("");
            }
        });
    }

    private void openChatActivity(String friendName) {
        Intent intent = new Intent(getActivity(), Chat.class);
        intent.putExtra("friend_name", friendName);
        startActivity(intent);
    }
}
