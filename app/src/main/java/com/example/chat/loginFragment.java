package com.example.chat;// LoginFragment.java
import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import Utils.BmobUtils;
import Utils.MyDatabaseUtils;
import Utils.SHA256Utils;
import Utils.SPDataUtils;
import Utils.ToastUtils;

public class loginFragment extends Fragment {

    private static final Logger log = LoggerFactory.getLogger(loginFragment.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText loginAccount = view.findViewById(R.id.loginAccount);
        EditText loginPassword = view.findViewById(R.id.loginPassword);
        Button loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断输入框是否有空
                if (loginAccount.getText().toString().isEmpty() || loginPassword.getText().toString().isEmpty()){
                    ToastUtils.showToast(getContext() , "输入框不能有空");
                    return;
                }

                // 判断手机号是否合法
                if (!BmobUtils.isValidPhoneNumber(loginAccount.getText().toString())){
                    ToastUtils.showToast(getContext() , "请输入一个合法的手机号");
                    return;
                }

                MyDatabaseUtils.queryByPhoneNumber(loginAccount.getText().toString(), new MyDatabaseUtils.ResultCallback<ArrayList<String>>() {
                    @Override
                    public void onSuccess(ArrayList<String> result) {
                        Log.i("toad", "onSuccess: " + "result = " + result);
                        // 判断手机号是否注册
                        if (result == null || result.isEmpty()){
                            getActivity().runOnUiThread(() -> {
                                ToastUtils.showToast(getContext() , "该手机号未注册");
                            });
                            return;
                        }

                        // 判断密码是否正确
                        if (SHA256Utils.encrypt(loginPassword.getText().toString()).equals(result.get(3))){
                            // 将 islogin 设置成 true，告知程序已登陆
                            ((dataHub) getActivity().getApplication()).setIsLogin("true");
                            SPDataUtils.storageInformation(getContext() , "isLogin" , "true");

                            // 将数据上传到数据中心，用户注册完可以立马看到数据
                            ((dataHub) getActivity().getApplication()).setUID(result.get(0));
                            ((dataHub) getActivity().getApplication()).setName(result.get(1));

                            // 将用户信息保存在本地
                            SPDataUtils.storageInformation(getContext() , "UID" , result.get(0));
                            SPDataUtils.storageInformation(getContext() , "userName" , result.get(1));

                            // 登陆成功后，清空上方输入框里的内容
                            loginAccount.setText("");
                            loginPassword.setText("");

                            // 登陆成功自动退出登陆页面，提升用户体验
                            requireActivity().setResult(Activity.RESULT_OK);
                            requireActivity().finish(); // 关闭当前 Activity

                            getActivity().runOnUiThread(() -> {ToastUtils.showToast(getContext() , "登陆成功！");});


                        }else {
                            getActivity().runOnUiThread(() -> {ToastUtils.showToast(getContext() , "密码错误");});
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.i("toad", "onFailure: " + e);
                    }
                });
            }
        });

        return view;
    }
}
