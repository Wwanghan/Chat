package com.example.chat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import Utils.SPDataUtils;

public class personal_information extends AppCompatActivity {

    private LinearLayout layoutName;
    private ImageButton exitPage;
    private TextView userName;
    private TextView userIpAddress;
    private ImageView userAvatar;
    private TextView myUid;
    private static final int REQUEST_CODE_PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        DialogUtils dialogUtils = new DialogUtils();

        layoutName = findViewById(R.id.layoutName);
        exitPage = findViewById(R.id.exitPage);
        userName = findViewById(R.id.userName);
        userIpAddress = findViewById(R.id.userIpAddress);
        userAvatar = findViewById(R.id.userAvatar);
        myUid = findViewById(R.id.myUid);
        // 设置头像透明背景
        userAvatar.setBackgroundColor(Color.TRANSPARENT);

        myUid.setText("UID: " + ((dataHub) getApplication()).getUID());
        userName.setText(((dataHub) getApplication()).getName());
        userIpAddress.setText(((dataHub) getApplication()).getIpAddress());

        // 从 SharedPreferences 加载之前保存的头像，如果之前用户有修改过头像，那么可以直接读取并设置用户自定义选择的头像
        // 如果从 SharedPreferences 读取不到数据，那么表示用户没有自定义选择头像，则使用默认的青蛙头像
        String avatarUriString = SPDataUtils.getStorageInformation(getBaseContext() , "avatarUri");
        Log.i("toad", "onCreate: " + avatarUriString);
        if (avatarUriString != null) {
            Uri avatarUri = Uri.parse(avatarUriString);
            try {
                userAvatar.setImageURI(avatarUri);
                Glide.with(this).load(avatarUri).circleCrop().into(userAvatar);
            } catch (SecurityException e) {
                Log.e("MainActivity", "Uri 权限失效: " + e.getMessage());
            }
        }else {
            userAvatar.setImageResource(R.mipmap.mrtoad);
            Glide.with(this).load(R.mipmap.mrtoad).circleCrop().into(userAvatar);
        }


        layoutName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建输入框
                EditText input = new EditText(personal_information.this);
                // 调用封装好的 dialogUtils
                dialogUtils.showDialog(personal_information.this, "input" , "请输入新名字", "确认" , input ,
                        (dialog, which) -> {
                            String newName = input.getText().toString();
                            userName.setText(newName);
                            Utils.SPDataUtils.storageInformation(getBaseContext() , "userName" , newName);
                            ((dataHub) getApplication()).setName(newName);
                            Toast.makeText(getBaseContext() , "名字修改成功" , Toast.LENGTH_SHORT).show();
                        }
                );
            }
        });

        // 用户点击按钮后，会调用本地相册，用户可以自己选择头像
        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentChangeAvatar = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentChangeAvatar.setType("image/*");
                intentChangeAvatar.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentChangeAvatar, REQUEST_CODE_PICK_IMAGE);
            }
        });



        exitPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // 持久化 URI 权限
                getContentResolver().takePersistableUriPermission(selectedImageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);

                // 将用户自己选择的头像，保存 Uri 到 SharedPreferences，方便下一次读取
                Utils.SPDataUtils.storageInformation(getBaseContext() , "avatarUri" , selectedImageUri.toString());
                // 显示图片
                userAvatar.setImageURI(selectedImageUri);
                // 设置图片等比例缩放
                userAvatar.setScaleType(ImageButton.ScaleType.FIT_CENTER);
                userAvatar.setAdjustViewBounds(true);  // 调整视图边界以保持图片比例

//                将图片上传数据中心，并裁剪图片为圆形
                ((dataHub) getApplication()).setAvatar(selectedImageUri);
                Glide.with(this).load(((dataHub) getApplication()).getAvatar()).circleCrop().into(userAvatar);
            }
        }
    }
}