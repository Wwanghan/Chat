package com.example.chat;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static String readConfig(String fileName, String key, Context context) {
        // 检查外部存储是否可读
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {

            File configFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

            // 如果配置文件不存在，写入默认配置并直接返回默认值
            if (!configFile.exists()) {
                Log.i("toad", "Config file not found, creating default config.");
                // 默认的JSON数据
                JSONObject defaultConfig = new JSONObject();
                try {
                    defaultConfig.put("streamDelay", "50");
                    // 你可以根据实际需求继续添加默认数据

                    // 写入默认配置到文件
                    writeToFile(fileName, defaultConfig.toString(), context);

                    // 写入后直接返回该key的默认值
                    return defaultConfig.optString(key, "");  // 返回刚写入的默认值
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // 如果配置文件存在，读取文件内容
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(configFile);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                // 将读取到的内容转换为JSON对象
                String fileContent = stringBuilder.toString();
                JSONObject config = new JSONObject(fileContent);

                // 返回指定key的值
                return config.optString(key, "");  // 如果key不存在，返回空字符串
            } catch (IOException | JSONException e) {
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
        return null;
    }

    public static void updateConfig(String fileName, String key, String value, Context context) {
        // 检查外部存储是否可写
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File configFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            JSONObject config = new JSONObject();

            // 如果文件存在，先读取文件中的内容
            if (configFile.exists()) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(configFile);
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader reader = new BufferedReader(isr);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    // 将读取到的内容转换为JSON对象
                    String fileContent = stringBuilder.toString();
                    config = new JSONObject(fileContent);

                } catch (IOException | JSONException e) {
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
            }

            // 更新或添加键值对
            try {
                config.put(key, value);  // 更新或添加 key-value

                // 将更新后的内容写回文件
                writeToFile(fileName, config.toString(), context);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("toad", "外部存储不可写");
        }
    }


}
