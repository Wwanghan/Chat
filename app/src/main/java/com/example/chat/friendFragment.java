package com.example.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import Utils.DensityUtils;


public class friendFragment extends Fragment {

    private LinearLayout friendList;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_friend_fragment, container, false);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = getContext();

        friendList = view.findViewById(R.id.friend_list);

        List<Integer> avatarList = new ArrayList<>();
        List<String> names = new ArrayList<>();

        avatarList.add(R.mipmap.avatar);
        avatarList.add(R.mipmap.avatar_2);
        avatarList.add(R.mipmap.avatar_3);

        names.add("张三");
        names.add("李四");
        names.add("王五");


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++){
                LinearLayout friendItems = new LinearLayout(context);
                friendItems.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams friendItemsParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        DensityUtils.densityDP(context , 50)
                );
                friendItemsParams.setMargins(0 , DensityUtils.densityDP(context , 20) , 0 , 0);
                friendItems.setLayoutParams(friendItemsParams);
                friendItems.setGravity(Gravity.CENTER_VERTICAL);

                LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(
                        DensityUtils.densityDP(context , 50),
                        DensityUtils.densityDP(context , 50)
                );

                ImageView avatar = new ImageView(context);
                avatar.setImageResource(avatarList.get(i));
                avatar.setLayoutParams(avatarParams);
                // 使用 glide ，给头像设置圆形裁剪
                Glide.with(this).load(avatarList.get(i)).circleCrop().into(avatar);

                LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                nameParams.setMargins(DensityUtils.densityDP(context , 10) , 0 , 0 , 0);

                TextView friendName = new TextView(context);
                friendName.setLayoutParams(nameParams);
                friendName.setText(names.get(i));

                // 横线分割
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        DensityUtils.densityDP(context , 1)
                );
                lineParams.setMargins(DensityUtils.densityDP(context , 60) , 0 , 0 , 0);

                ImageView line = new ImageView(context);
                line.setLayoutParams(lineParams);
                line.setBackgroundColor(R.color.black);

                // 调整分割线的透明度
                line.setAlpha(0.2f);

                friendItems.addView(avatar);
                friendItems.addView(friendName);
                friendList.addView(friendItems);
                friendList.addView(line);
            }


        }

    }

}

