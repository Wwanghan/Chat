package com.example.chat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class
myFragment extends Fragment {

    file_os fs = new file_os();  // new一个我自己写的文件类

    String info_content;  // 用于存放读取的文件信息
    private View view;
    private TextView chat;
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView uid = view.findViewById(R.id.UID);
        uid.setText(info_content);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        动态请求读写权限
        if (ContextCompat.checkSelfPermission(getActivity() , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(getActivity() , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity() , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE} , 1);
        }else {
            getUserUid();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            // 检查请求结果是否包含读写权限
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意了读写权限请求，执行文件操作
                getUserUid();
            } else {
                Log.i("toad", "onRequestPermissionsResult: no");
            }
        }
    }

    public void getUserUid(){
        // 最开始先执行这儿的代码，使用readFromFile读取文件时，这个函数先会判断本地Download下到底有没有这个文件
        // 如果有，那么最后会返回文件内容，info_content就可以接收到文件中的数据。
        // 如果找不到要读的文件，则返回null，那么下面的if语句成立，于是先创建文件，再读取文件。
        info_content = fs.readFromFile("chat_personalInformation.txt" , getActivity());
        if (info_content == null){
            fs.writeToFile("chat_personalInformation.txt" , "10000" , getActivity());
            info_content = fs.readFromFile("chat_personalInformation.txt" , getActivity());
        }
    }
}
