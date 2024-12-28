package com.example.chat;// RegisterFragment.java
import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import Animations.ViewTransitionAnimator;
import Constants.MessageConstants;
import Utils.BmobUtils;
import Utils.GeneralUtils;
import Utils.MyDatabaseUtils;
import Utils.SHA256Utils;
import Utils.ToastUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class registerFragment extends Fragment {
    // 短信发送限制规则是1/分钟，5/小时，10/天。即对于一个应用来说，一天给同一手机号发送短信不能超过10条，
    // 一小时给同一手机号发送短信不能超过5条，一分钟给同一手机号发送短信不能超过1条。

    private TextView userNameErrorText;
    private EditText registerPhoneNumber;
    private EditText verifyCode;
    private Button sendVerifyCodeButton;
    private EditText registerUserName;
    private EditText registerPassword;
    private Button registerButton;
    private TextView phoneNumberErrorText;

    // 发送验证码的倒计时
    private static final int COUNTDOWN_TIME = 60;
    private CountDownTimer countDownTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        DialogUtils dialogUtils = new DialogUtils();
        Handler handler = new Handler();

        registerPhoneNumber = view.findViewById(R.id.registerPhoneNumber);
        verifyCode = view.findViewById(R.id.verifyCode);
        sendVerifyCodeButton = view.findViewById(R.id.sendVerifyCodeButton);
        registerUserName = view.findViewById(R.id.registerUserName);
        registerPassword = view.findViewById(R.id.registerPassword);
        registerButton = view.findViewById(R.id.registerButton);
        phoneNumberErrorText = view.findViewById(R.id.phone_number_error_register);
        userNameErrorText = view.findViewById(R.id.user_name_error);

        // 设置手机号和验证码输入框只能输入0-9的数字
        registerPhoneNumber.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        verifyCode.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

        // 限制输入长度
        registerPhoneNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        registerUserName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        registerPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});

        // 初始化Bmob
        BmobUtils.Init(getContext());

        // 如果用户已经设置了用户名，那么用户名框默认显示用户之前设置的，节省用户时间
        if (!((dataHub) getActivity().getApplication()).getName().equals("userNull")){
            registerUserName.setText(((dataHub) getActivity().getApplication()).getName());
        }

        /**
         * 监听手机号输入，并判断是否合法，并提示用户
         */
        registerPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if (!registerPhoneNumber.getText().toString().isEmpty()){
                        if (!BmobUtils.isValidPhoneNumber(registerPhoneNumber.getText().toString())){
                            phoneNumberErrorText.setText(MessageConstants.PLEASE_ENTER_VALID_PHONE_NUMBER);
                            ViewTransitionAnimator.showViewWithAnimation(phoneNumberErrorText, -30f, 300);
                        }else {
                            MyDatabaseUtils.queryByPhoneNumber(registerPhoneNumber.getText().toString(), new MyDatabaseUtils.ResultCallback<ArrayList<String>>() {
                                @Override
                                public void onSuccess(ArrayList<String> result) {
                                    if (!(result == null || result.isEmpty())){
                                        getActivity().runOnUiThread(() -> {
                                            phoneNumberErrorText.setText(MessageConstants.PHONE_NUMBER_IS_ALREADY_REGISTERED);
                                            ViewTransitionAnimator.showViewWithAnimation(phoneNumberErrorText, -30f, 300);
                                        });
                                    }else {
                                        getActivity().runOnUiThread(() -> {
                                            ViewTransitionAnimator.hideViewWithAnimation(phoneNumberErrorText, -30f, 300 , null);
                                        });
                                    }
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    Log.i("toad", "onFailure: " + e);
                                    getActivity().runOnUiThread(() -> ToastUtils.showToast(getContext(), MessageConstants.DATABASE_CONNECTION_FAILED));
                                }
                            });
                        }
                    }else {
                        ViewTransitionAnimator.hideViewWithAnimation(phoneNumberErrorText, -30f, 300 , null);
                    }
                }

            }
        });

        /**
         * 监听用户名输入，并判断是否合法，并提示用户
         */
        registerUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if (!registerUserName.getText().toString().isEmpty()){
                        checkUserName(registerUserName.getText().toString());
                    } else {
                        ViewTransitionAnimator.hideViewWithAnimation(userNameErrorText, -30f, 300 , null);
                    }
                }
            }
        });

        /**
         * 发送验证码
         */
        sendVerifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断手机号是否为空
                if (registerPhoneNumber.getText().toString().isEmpty()){
                    phoneNumberErrorText.setText(MessageConstants.PHONE_NUMBER_CANNOT_BE_EMPTY);
                    ViewTransitionAnimator.showViewWithAnimation(phoneNumberErrorText, -30f, 300);
                    handler.postDelayed(() -> ViewTransitionAnimator.hideViewWithAnimation(phoneNumberErrorText, -30f, 300 , null) , 2000);
                    return;
                }

                // 判断手机号是否是一个合法的手机号
                if (!BmobUtils.isValidPhoneNumber(registerPhoneNumber.getText().toString())){
                    phoneNumberErrorText.setText(MessageConstants.PLEASE_ENTER_VALID_PHONE_NUMBER);
                    ViewTransitionAnimator.showViewWithAnimation(phoneNumberErrorText, -30f, 300);
                    handler.postDelayed(() -> ViewTransitionAnimator.hideViewWithAnimation(phoneNumberErrorText, -30f, 300 , null) , 2000);
                    return;
                }

                // 判断手机号是否已注册
                CountDownLatch latch = new CountDownLatch(1); // 创建一个计数器
                final Boolean[] isRegister = {false};
                MyDatabaseUtils.queryByPhoneNumber(registerPhoneNumber.getText().toString(), new MyDatabaseUtils.ResultCallback<ArrayList<String>>() {
                    @Override
                    public void onSuccess(ArrayList<String> result) {
                        if (!(result == null || result.isEmpty())){
                            getActivity().runOnUiThread(() -> {
                                phoneNumberErrorText.setText(MessageConstants.PHONE_NUMBER_IS_ALREADY_REGISTERED);
                                ViewTransitionAnimator.showViewWithAnimation(phoneNumberErrorText, -30f, 300);
                            });
                            isRegister[0] = true;
                            latch.countDown(); // 回调完成，计数器减1
                        } else {
                            getActivity().runOnUiThread(() -> {ViewTransitionAnimator.hideViewWithAnimation(phoneNumberErrorText, -30f, 300 , null);});
                            isRegister[0] = false;
                            latch.countDown();
                        }

                    }
                    @Override
                    public void onFailure(Exception e) {
                        getActivity().runOnUiThread(() -> {ToastUtils.showToast(getContext(), MessageConstants.DATABASE_CONNECTION_FAILED + e);});
                        latch.countDown();
                    }
                });

                try {
                    latch.await(); // 阻塞当前线程，直到计数器为0
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!isRegister[0]){
                    BmobUtils.sendCode(getContext() , registerPhoneNumber.getText().toString());

                    sendVerifyCodeButton.setEnabled(false);
                    sendVerifyCodeButton.setBackgroundResource(R.drawable.send_button_unenabled);

                    countDownTimer = new CountDownTimer(COUNTDOWN_TIME * 1000 , 1000){
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
            }
        });

        /**
         * 现实登陆，校验信息无误并验证验证码成功后将信息保存到数据库
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
                                ToastUtils.showToast(getContext() , MessageConstants.PHONE_NUMBER_IS_ALREADY_REGISTERED);
                            });
                            return;
                        }

                        // 判断手机号是否合法
                        if (!BmobUtils.isValidPhoneNumber(registerPhoneNumber.getText().toString())){
                            getActivity().runOnUiThread(() -> {
                                ToastUtils.showToast(getContext() , MessageConstants.PLEASE_ENTER_VALID_PHONE_NUMBER);
                            });
                            return;
                        }

                        // 判断用户是否输入验证码
                        if (verifyCode.getText().toString().isEmpty()){
                            getActivity().runOnUiThread(() -> {
                                ToastUtils.showToast(getContext() , MessageConstants.CODE_CANNOT_BE_EMPTY);
                            });
                            return;
                        }

                        // 判断用户名是否合法
                        int checkUserNameResultCode = checkUserName(registerUserName.getText().toString());
                        if (checkUserName(registerUserName.getText().toString()) == -2){
                            getActivity().runOnUiThread(() -> {
                                ToastUtils.showToast(getContext() , MessageConstants.USERNAME_ALREADY_EXISTS);
                            });
                            return;
                        } else if (checkUserName(registerUserName.getText().toString()) == -3) {
                            getActivity().runOnUiThread(() -> {
                                ToastUtils.showToast(getContext() , MessageConstants.USERNAME_CANNOT_CONTAIN_SPECIAL_CHARACTERS);
                            });
                            return;
                        }

                        // 判断用户是否输入了手机号，用户名，密码，信息不能有空
                        if (registerPhoneNumber.getText().toString().isEmpty() || registerUserName.getText().toString().isEmpty()|| registerPassword.getText().toString().isEmpty()){
                            getActivity().runOnUiThread(() -> {
                                ToastUtils.showToast(getContext() , MessageConstants.INPUT_CANNOT_EMPTY);
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
                                                ToastUtils.showToast(activity , MessageConstants.DATABASE_CONNECTION_FAILED);
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
                                                    ToastUtils.showToast(activity , MessageConstants.REGISTER_SUCCESS);
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
                                    ToastUtils.showToast(getContext() , MessageConstants.CODE_ERROR);
                                });
                            }
                        });

                    }

                    @Override
                    public void onFailure(Exception e) {
                        getActivity().runOnUiThread(() -> {
                            ToastUtils.showToast(getContext() , MessageConstants.DATABASE_CONNECTION_FAILED);
                        });
                    }
                });
            }
        });

        return view;
    }

    /**
     * 检查用户名是否合法且唯一
     * @param userName
     * @return
     */
    private int checkUserName(String userName){
        final int[] isSuccess = {0};

        if (GeneralUtils.containsSpecialCharacters(userName)){
            userNameErrorText.setText(MessageConstants.USERNAME_CANNOT_CONTAIN_SPECIAL_CHARACTERS);
            ViewTransitionAnimator.showViewWithAnimation(userNameErrorText , -30f , 300);
            return -3;
        }

        CountDownLatch latch = new CountDownLatch(1); // 创建一个计数器
        MyDatabaseUtils.checkUserNameExists(userName, new MyDatabaseUtils.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result){
                    getActivity().runOnUiThread(() -> {
                        userNameErrorText.setText(MessageConstants.USERNAME_ALREADY_EXISTS);
                        ViewTransitionAnimator.showViewWithAnimation(userNameErrorText , -30f , 300);
                    });
                    latch.countDown();
                } else {
                    getActivity().runOnUiThread(() -> ViewTransitionAnimator.hideViewWithAnimation(userNameErrorText , -30f , 300 , null));
                    isSuccess[0] = 1;
                    latch.countDown();
                }

            }

            @Override
            public void onFailure(Exception e) {
                ToastUtils.showToast(getContext(), MessageConstants.DATABASE_CONNECTION_FAILED + e);
                latch.countDown();
            }
        });

        try {
            latch.await(); // 阻塞当前线程，直到计数器为0
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (isSuccess[0] == 1){
            return 1;
        } else {
            return -2;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 防止在倒计时过程中，Fragment 被暂停或销毁时，导致倒计时继续进行
        if (countDownTimer != null) {
            countDownTimer.cancel();
            sendVerifyCodeButton.setText("发送验证码");
            sendVerifyCodeButton.setEnabled(true);
        }
    }
}
