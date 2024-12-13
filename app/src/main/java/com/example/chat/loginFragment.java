package com.example.chat;
import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import Constants.MessageConstants;
import Utils.BmobUtils;
import Utils.MyDatabaseUtils;
import Utils.SHA256Utils;
import Utils.SPDataUtils;
import Utils.ToastUtils;

public class loginFragment extends Fragment {

    private static final Logger log = LoggerFactory.getLogger(loginFragment.class);

    private EditText loginAccount;
    private EditText loginPassword;
    private Button loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginAccount = view.findViewById(R.id.loginAccount);
        loginPassword = view.findViewById(R.id.loginPassword);
        loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断输入框是否有空
                if (loginAccount.getText().toString().isEmpty() || loginPassword.getText().toString().isEmpty()){
                    ToastUtils.showToast(getContext() , MessageConstants.INPUT_CANNOT_EMPTY);
                    return;
                }

                // 判断手机号是否合法
                if (!BmobUtils.isValidPhoneNumber(loginAccount.getText().toString())){
                    ToastUtils.showToast(getContext() , MessageConstants.PLEASE_ENTER_VALID_PHONE_NUMBER);
                    loginAccount.setText("");
                    return;
                }

                MyDatabaseUtils.queryByPhoneNumber(loginAccount.getText().toString(), new MyDatabaseUtils.ResultCallback<ArrayList<String>>() {
                    @Override
                    public void onSuccess(ArrayList<String> result) {
                        // 判断手机号是否注册
                        if (result == null || result.isEmpty()){
                            getActivity().runOnUiThread(() -> {
                                ToastUtils.showToast(getContext() , MessageConstants.PHONE_NUMBER_IS_NOT_REGISTERED);
                                loginAccount.setText("");
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
                            ((dataHub) getActivity().getApplication()).setPhoneNumber(result.get(2));
                            ((dataHub) getActivity().getApplication()).setCreate_time(result.get(6));

                            // 将用户信息保存在本地
                            SPDataUtils.storageInformation(getContext() , "UID" , result.get(0));
                            SPDataUtils.storageInformation(getContext() , "userName" , result.get(1));
                            SPDataUtils.storageInformation(getContext() , "phoneNumber" , result.get(2));
                            SPDataUtils.storageInformation(getContext() , "create_time" , result.get(6));

                            // 登陆成功后，清空上方输入框里的内容
                            loginAccount.setText("");
                            loginPassword.setText("");

                            // 登陆成功自动退出登陆页面，提升用户体验
                            requireActivity().setResult(Activity.RESULT_OK);
                            requireActivity().finish(); // 关闭当前 Activity

                            getActivity().runOnUiThread(() -> {ToastUtils.showToast(getContext() , MessageConstants.LOGIN_SUCCESS);});


                        }else {
                            getActivity().runOnUiThread(() -> {ToastUtils.showToast(getContext() , MessageConstants.PASSWORD_ERROR);});
                            loginPassword.setText("");
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
