package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import com.example.chat.R.id;
import android.widget.TextView;
import android.widget.LinearLayout;


import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private  chatFragment chatFragment;
    private  myFragment myFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatFragment = new chatFragment();
        myFragment = new myFragment();

        // 默认显示聊天 Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, chatFragment).commit();

        RadioGroup rgGroup = findViewById(R.id.rg_group);

        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment selectedFragment = null;
                if (checkedId == id.chat_nav) {
                    selectedFragment = chatFragment;
                } else if (checkedId == id.my_nav) {
                    selectedFragment = myFragment;
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, selectedFragment).commit();
                }
            }
        });
    }
}