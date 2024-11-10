package com.example.chat;// RegisterFragment.java
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class registerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        EditText registerUserName = view.findViewById(R.id.registerUserName);
        EditText registerAccount = view.findViewById(R.id.registerAccount);
        EditText registerPassword = view.findViewById(R.id.registerPassword);
        Button registerButton = view.findViewById(R.id.registerButton);

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
