package com.example.chat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import Constants.MessageConstants;
import Utils.MyDatabaseUtils;
import Utils.SPDataUtils;
import Utils.ToastUtils;

public class personal_information extends AppCompatActivity {

    private ImageButton exitPage;
    private TextView userName;
    private ImageButton changeName;
    private ImageView userAvatar;
    private TextView myUid;
    private TextView createTime;
    private TextView phoneNumber;
    private Button buttonSignOut;
    private static final int REQUEST_CODE_PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        DialogUtils dialogUtils = new DialogUtils();

        exitPage = findViewById(R.id.exitPage);
        userName = findViewById(R.id.user_name);
        userAvatar = findViewById(R.id.userAvatar);
        createTime = findViewById(R.id.create_time);
        phoneNumber = findViewById(R.id.user_phone_number);
        buttonSignOut = findViewById(R.id.button_sign_out);
        changeName = findViewById(R.id.change_name);
        myUid = findViewById(R.id.myUid);

        // 设置头像透明背景
        userAvatar.setBackgroundColor(Color.TRANSPARENT);

        myUid.setText("UID: " + ((dataHub) getApplication()).getUID());
        userName.setText(((dataHub) getApplication()).getName());
        phoneNumber.setText(((dataHub) getApplication()).getPhoneNumber());
        createTime.setText(((dataHub) getApplication()).getCreate_time());

        // 从 SharedPreferences 加载之前保存的头像，如果之前用户有修改过头像，那么可以直接读取并设置用户自定义选择的头像
        // 如果从 SharedPreferences 读取不到数据，那么表示用户没有自定义选择头像，则使用默认的青蛙头像
        String avatarUriString = SPDataUtils.getStorageInformation(getBaseContext() , "avatarUri");
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

        /**
         * 用户自定义设置名字
         */
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建输入框
                EditText input = new EditText(personal_information.this);
                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                // 调用封装好的 dialogUtils
                dialogUtils.showDialog(personal_information.this, "input" , "请输入新名字", "确认" , input ,
                    (dialog, which) -> {
                        // 修改名字分为两种情况
                        // 1. 用户未登陆，此时只是在本地保存新的名字
                        // 2. 用户已经登陆，此时需要将最新的名字更新数据库保存
                        if (((dataHub) getApplication()).getIsLogin().equals("true")){
                            // 更新数据库，先在数据库中修改完成对应用户名，确保数据库已经修改完毕，等回调再在本地修改保存。
                            // 防止先在本地修改后，数据库连接失败的情况
                            MyDatabaseUtils.updateUserNameByUID((input.getText().toString()) , ((dataHub) getApplication()).getUID() , new MyDatabaseUtils.ResultCallback<ArrayList<String>>() {
                                @Override
                                public void onSuccess(ArrayList<String> result) {
                                    runOnUiThread(() -> {
                                        ((dataHub) getApplication()).setName(input.getText().toString());
                                        SPDataUtils.storageInformation(getBaseContext() , "userName" , input.getText().toString());
                                        userName.setText(input.getText().toString());
                                        ToastUtils.showToast(getBaseContext() , MessageConstants.NAME_CHANGE_SUCCESS);
                                    });
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    runOnUiThread(() -> {
                                        ToastUtils.showToast(getBaseContext() , MessageConstants.DATABASE_CONNECTION_FAILED);
                                    });
                                }
                            });

                        } else {
                            // 用户未登陆的情况只修改本地，不涉及数据库操作
                            ((dataHub) getApplication()).setName(input.getText().toString());
                            SPDataUtils.storageInformation(getBaseContext() , "userName" , input.getText().toString());
                            userName.setText(input.getText().toString());
                            ToastUtils.showToast(getBaseContext() , MessageConstants.NAME_CHANGE_SUCCESS);
                        }

                    }
                );
            }
        });

        /**
         * 用户自定义设置头像
         */
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

        /**
         * 退出登陆
         */
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 若果用户本就是未登陆，提示用户 “您未登陆，无需退出”
                if (((dataHub) getApplication()).getIsLogin().equals("false")) {
                    ToastUtils.showToast(getBaseContext() , MessageConstants.NO_NEED_TO_QUIT);
                    return;
                }

                SPDataUtils.storageInformation(getBaseContext() , "isLogin" , "false");
                SPDataUtils.storageInformation(getBaseContext() , "UID" , "uidNull");
                SPDataUtils.storageInformation(getBaseContext() , "userName" , "userNull");
                SPDataUtils.storageInformation(getBaseContext() , "phoneNumber" , "null");
                SPDataUtils.storageInformation(getBaseContext() , "create_time" , "null");
                ((dataHub) getApplication()).setIsLogin("false");
                ((dataHub) getApplication()).setUID("uidNull");
                ((dataHub) getApplication()).setName("userNull");
                ((dataHub) getApplication()).setPhoneNumber("null");
                ((dataHub) getApplication()).setCreate_time("null");
                ToastUtils.showToast(getBaseContext() , MessageConstants.LOGOUT_SUCCESS);
                finish();
            }
        });
    }

    /**
     * 获取用户自定义的头像，并保存到 SharedPreferences 中
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
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