package com.example.chat;// RegisterFragment.java
import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import Utils.BmobUtils;
import Utils.MyDatabaseUtils;
import Utils.SHA256Utils;
import Utils.ToastUtils;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class registerFragment extends Fragment {
    // 短信发送限制规则是1/分钟，5/小时，10/天。即对于一个应用来说，一天给同一手机号发送短信不能超过10条，
    // 一小时给同一手机号发送短信不能超过5条，一分钟给同一手机号发送短信不能超过1条。

    // 发送验证码的倒计时
    private static final int COUNTDOWN_TIME = 60;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        DialogUtils dialogUtils = new DialogUtils();

        EditText registerPhoneNumber = view.findViewById(R.id.registerPhoneNumber);
        EditText verifyCode = view.findViewById(R.id.verifyCode);
        Button sendVerifyCodeButton = view.findViewById(R.id.sendVerifyCodeButton);
        EditText registerUserName = view.findViewById(R.id.registerUserName);
        EditText registerPassword = view.findViewById(R.id.registerPassword);
        Button registerButton = view.findViewById(R.id.registerButton);

        // 设置手机号和验证码输入框只能输入0-9的数字
        registerPhoneNumber.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        verifyCode.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

        // 初始化Bmob
        BmobUtils.Init(getContext());

        // 如果用户已经设置了用户名，那么用户名框默认显示用户之前设置的，节省用户时间
        if (!((dataHub) getActivity().getApplication()).getName().equals("userNull")){
            registerUserName.setText(((dataHub) getActivity().getApplication()).getName());
        }


        /**
         * 发送验证码
         */
        sendVerifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断手机号是否为空
                if (registerPhoneNumber.getText().toString().isEmpty()){
                    ToastUtils.showToast(getContext() , "手机号不能为空");
                    return;
                }

                // 判断手机号是否是一个合法的手机号
                if (!BmobUtils.isValidPhoneNumber(registerPhoneNumber.getText().toString())){
                    ToastUtils.showToast(getContext() , "请输入一个合法的手机号");
                    return;
                }

                BmobUtils.sendCode(getContext() , registerPhoneNumber.getText().toString());

                sendVerifyCodeButton.setEnabled(false);
                sendVerifyCodeButton.setBackgroundResource(R.drawable.send_button_unenabled);


//                bmobApp.sendCode();
                new CountDownTimer(COUNTDOWN_TIME * 1000 , 1000){
                    int timeleft = COUNTDOWN_TIME;


                    @Override
                    public void onTick(long millisUntilFinished) {
                        sendVerifyCodeButton.setText("发送中 " + timeleft + "s");
                        timeleft -= 1;
                    }

                    @Override
                    public void onFinish() {
                        sendVerifyCodeButton.setEnabled(true);
                        sendVerifyCodeButton.setBackgroundResource(R.drawable.send_button_style);
                        sendVerifyCodeButton.setText("发送验证码");
                    }
                }.start();
            }
        });

        /**
         * 验证验证码，验证成功后将信息保存到数据库
         */
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断手机号是否已注册，复用 queryByPhoneNumber 方法
                // 如果查询结果不为空，则说明该手机号已注册
                MyDatabaseUtils.queryByPhoneNumber(registerPhoneNumber.getText().toString(), new MyDatabaseUtils.ResultCallback<ArrayList<String>>() {
                    @Override
                    public void onSuccess(ArrayList<String> result) {
                        if (!(result == null || result.isEmpty())){
                            getActivity().runOnUiThread(() -> {
                                ToastUtils.showToast(getContext() , "该手机号已注册！注册失败！");
                            });
                            return;
                        }

                        // 判断手机号是否合法
                        if (!BmobUtils.isValidPhoneNumber(registerPhoneNumber.getText().toString())){
                            getActivity().runOnUiThread(() -> {
                                ToastUtils.showToast(getContext() , "请输入一个合法的手机号！");
                            });
                            return;
                        }

                        // 判断用户是否输入验证码
                        if (verifyCode.getText().toString().isEmpty()){
                            getActivity().runOnUiThread(() -> {
                                ToastUtils.showToast(getContext() , "验证码不能为空！");
                            });
                            return;
                        }
                        // 判断用户是否输入了手机号，用户名，密码，信息不能有空
                        if (registerPhoneNumber.getText().toString().isEmpty() || registerUserName.getText().toString().isEmpty()|| registerPassword.getText().toString().isEmpty()){
                            getActivity().runOnUiThread(() -> {
                                ToastUtils.showToast(getContext() , "信息不能有空！");
                            });
                            return;
                        }

                        // 实现注册逻辑
                        BmobUtils.verifyCode(getContext(), registerPhoneNumber.getText().toString(), verifyCode.getText().toString(), new BmobUtils.VerifyCallback() {
                            @Override
                            public void onSuccess() {
                                // 短信验证成功
                                String command = String.format("INSERT INTO user (userName , PhoneNumber , Password , create_time) VALUES('%s' , '%s' , '%s' , NOW())" , registerUserName.getText().toString() , registerPhoneNumber.getText().toString() , SHA256Utils.encrypt(registerPassword.getText().toString()));
                                String status = "add";
                                MyDatabaseUtils.executeCommand(command, status, new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        // 获取宿主 Activity
                                        Activity activity = getActivity();
                                        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                                            activity.runOnUiThread(() -> {
                                                ToastUtils.showToast(activity , "注册失败，请求数据库异常！");
                                            });
                                        }
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        // 获取宿主 Activity
                                        Activity activity = getActivity();
                                        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                                            Log.i("toad", "onResponse: " + response.body().string());
                                            activity.runOnUiThread(() -> {
                                                // 用户注册成功后将上方输入框的内容全部清空，以防用户多点造成多次注册
                                                registerPhoneNumber.setText("");
                                                verifyCode.setText("");
                                                registerUserName.setText("");
                                                registerPassword.setText("");

                                                if (getActivity() instanceof LAR_mainActivity) {
                                                    ((LAR_mainActivity) getActivity()).switchToLoginFragment(); // 通知 Activity 切换页面
                                                }

                                                getActivity().runOnUiThread(() -> {
                                                    ToastUtils.showToast(activity , "注册成功！");
                                                });
                                            });
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                // 短信验证失败
                                getActivity().runOnUiThread(() -> {
                                    ToastUtils.showToast(getContext() , "短信验证失败");
                                });
                            }
                        });

                    }

                    @Override
                    public void onFailure(Exception e) {
                        getActivity().runOnUiThread(() -> {
                            ToastUtils.showToast(getContext() , "数据库连接失败");
                        });
                    }
                });
            }
        });

        return view;
    }
}
