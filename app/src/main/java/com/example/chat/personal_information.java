package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


public class personal_information extends AppCompatActivity {

    private LinearLayout layoutName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        DialogUtils dialogUtils = new DialogUtils();

        layoutName = findViewById(R.id.layoutName);



        layoutName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建输入框
                EditText input = new EditText(personal_information.this);
                // 调用封装好的 dialogUtils
                dialogUtils.showConnectDialog(personal_information.this, "请输入新名字", "确认" , input ,
                        (dialog, which) -> {
                            Toast.makeText(personal_information.this , "OK" , Toast.LENGTH_SHORT).show();
                        }
                );
            }
        });


    }
}