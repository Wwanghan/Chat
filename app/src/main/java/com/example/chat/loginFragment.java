package com.example.chat;// LoginFragment.java
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class loginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText loginAccount = view.findViewById(R.id.loginAccount);
        EditText loginPassword = view.findViewById(R.id.loginPassword);
        Button loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            // 登录逻辑
            String account = loginAccount.getText().toString();
            String password = loginPassword.getText().toString();
            Toast.makeText(getActivity(), "登录中... 账号: " + account, Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
