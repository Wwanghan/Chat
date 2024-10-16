package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class activity_aboutMe extends AppCompatActivity {

    private TextView aboutMeTet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        aboutMeTet = findViewById(R.id.aboutMeText);
        aboutMeTet.setText("我只是很无聊，找点事做，所以开发了这个。真的随缘更新");

    }
}