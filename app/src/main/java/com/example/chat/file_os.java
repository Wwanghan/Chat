package com.example.chat;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class file_os {

    // 写入文件
    public static void writeToFile(String fileName, String data, Context context) {
        // 检查外部存储是否可写
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileOutputStream fos = null;
            try {
                // 获取外部存储的下载目录
                File externalFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

                // 创建文件输出流
                fos = new FileOutputStream(externalFile);
                fos.write(data.getBytes());

                Log.i("toad", "writeToFile: ok, file path: " + externalFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Log.e("toad", "外部存储不可写");
        }
    }

    // 从指定文件中读取文件内容返回一个文本字符串
    public static String readFromFile(String fileName, Context context) {
        StringBuilder stringBuilder = new StringBuilder();

        // 检查外部存储是否可读
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            FileInputStream fis = null;
            try {
                // 获取外部存储的下载目录
                File externalFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

                // 检查文件是否存在
                if (!externalFile.exists()) {
                    Log.e("toad", "文件不存在: " + externalFile.getAbsolutePath());
                    return null;
                }

                // 创建文件输入流
                fis = new FileInputStream(externalFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                Log.i("toad", "readFromFile: 文件读取成功: " + externalFile.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Log.e("toad", "外部存储不可读");
        }

        return stringBuilder.toString();
    }
}
