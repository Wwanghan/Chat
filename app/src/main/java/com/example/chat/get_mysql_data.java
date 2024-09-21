package com.example.chat;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class get_mysql_data {

    BufferedReader reader = null;

    // 获取musql数据的接口类，调用querySqlData，给定两个参数：数据库库名和执行的sql语句，函数会返回对于的结果

    public String querySqlData(String database, String query_command) {
        String sql_result = "";
        try {
            HttpURLConnection connection = null;

            String url = "http://49.233.248.218:9999/MnNnZCxCxycdpcYget_data?query=" + URLEncoder.encode(query_command, "utf-8") + "&database=" + database;
            URL requestUrl = new URL(url);
            HttpURLConnection HttpURLConnection = (java.net.HttpURLConnection) requestUrl.openConnection();
            HttpURLConnection.setRequestProperty("x-api-key" , BuildConfig.API_KEY);
            Log.i("toad", "querySqlData: " + BuildConfig.API_KEY);
            HttpURLConnection.setRequestMethod("GET");
            HttpURLConnection.setConnectTimeout(5000);
            HttpURLConnection.connect();

            //获取流
            InputStream inputStream = HttpURLConnection.getInputStream();

            //将二进制流包装
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuilder stringBuilder = new StringBuilder();

            // 拼接数据
            while ((line = reader.readLine()) != null) {

                stringBuilder.append(line);
                stringBuilder.append("\n");
            }

            if (stringBuilder.length() == 0) {
                return null;
            }

            // 不转码直接拿数据
            // sql_result = stringBuilder.toString();

            // 将结果转成utf-8
            sql_result = new String(stringBuilder.toString().getBytes("ISO-8859-1"), "utf-8");

        } catch (Exception e) {
            Log.i("toad", "querySqlData: " + e.getMessage());
            e.printStackTrace();

        }
        return sql_result;
    }
}
