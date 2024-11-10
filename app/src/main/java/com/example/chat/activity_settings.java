package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


public class activity_settings extends AppCompatActivity {

    private EditText editDelay;
    private ImageButton exitPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editDelay = findViewById(R.id.editDelay);
        exitPage = findViewById(R.id.exitPage);

        // 创建 SpannableString 来设置 hint
        SpannableString hint = new SpannableString(((dataHub) getApplication()).getDelay() + "ms");
        // 设置 hint 大小比例（例如设置为默认字体大小的 0.8 倍）
        hint.setSpan(new RelativeSizeSpan(0.7f), 0, hint.length(), 0);
        editDelay.setHint(hint);

        // 这里自定义过滤规则，比如只能输入0-9
        String digits = "0123456789";
        editDelay.setKeyListener(DigitsKeyListener.getInstance(digits));


        exitPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 在用户退出当前页面后，先是判断用户有无修改内容，如果没有，那么不做任何事
        // 如果用户修改了数值，则将用户自定义修改的值更新到配置文件中去，并动态修改数据
        if (!editDelay.getText().toString().trim().isEmpty()){
            SPDataUtils.storageInformation(getBaseContext() , "streamDelay" , editDelay.getText().toString());
            ((dataHub) getApplication()).setDelay(Integer.parseInt(editDelay.getText().toString()));
        }

    }
}