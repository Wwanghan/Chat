package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class personal_information extends AppCompatActivity {

    private LinearLayout layoutName;
    private ImageButton exitPage;
    private TextView perUserName;
    private TextView perIpAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        DialogUtils dialogUtils = new DialogUtils();

        file_os fs = new file_os();

        layoutName = findViewById(R.id.layoutName);
        exitPage = findViewById(R.id.exitPage);
        perUserName = findViewById(R.id.per_userName);
        perIpAddress = findViewById(R.id.per_ipAddress);

        perUserName.setText(((dataHub) getApplication()).getName());
        perIpAddress.setText(((dataHub) getApplication()).getIpAddress());

        layoutName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建输入框
                EditText input = new EditText(personal_information.this);
                // 调用封装好的 dialogUtils
                dialogUtils.showConnectDialog(personal_information.this, "请输入新名字", "确认" , input ,
                        (dialog, which) -> {
                            String newName = input.getText().toString();
                            perUserName.setText(newName);
                            ((dataHub) getApplication()).setName(newName);
                            fs.updateConfig("chatConfig.conf" , "Name" , newName , getBaseContext());
                            Toast.makeText(getBaseContext() , "名字修改成功" , Toast.LENGTH_SHORT).show();

                        }
                );
            }
        });

        exitPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}