package com.example.chat;// RegisterFragment.java
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;

public class registerFragment extends Fragment {

    // 发送验证码的倒计时
    private static final int COUNTDOWN_TIME = 60;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        EditText registerUserName = view.findViewById(R.id.registerUserName);
        EditText registerAccount = view.findViewById(R.id.registerAccount);
        EditText registerPassword = view.findViewById(R.id.registerPassword);
        Button sendCodeButton = view.findViewById(R.id.sendCodeButton);
        Button registerButton = view.findViewById(R.id.registerButton);

        // 如果用户已经设置了用户名，那么用户名框默认显示用户之前设置的，节省用户时间
        if (!((dataHub) getActivity().getApplication()).getName().equals("userNull")){
            registerUserName.setText(((dataHub) getActivity().getApplication()).getName());
        }

        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCodeButton.setEnabled(false);
                sendCodeButton.setBackgroundResource(R.drawable.send_button_unenabled);
                new CountDownTimer(COUNTDOWN_TIME * 1000 , 1000){
                    int timeleft = COUNTDOWN_TIME;

                    @Override
                    public void onTick(long millisUntilFinished) {
                        sendCodeButton.setText("发送中 " + timeleft + "s");
                        timeleft -= 1;
                    }

                    @Override
                    public void onFinish() {
                        sendCodeButton.setEnabled(true);
                        sendCodeButton.setBackgroundResource(R.drawable.send_button_style);
                        sendCodeButton.setText("发送验证码");
                    }
                }.start();
            }
        });

        registerButton.setOnClickListener(v -> {
            // 注册逻辑
            String userName = registerUserName.getText().toString();
            String account = registerAccount.getText().toString();
            String password = registerPassword.getText().toString();
            Toast.makeText(getActivity(), "注册中... 昵称: " + userName, Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
