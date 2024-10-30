package com.example.chat;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class myDatabase {

    private static final String API_URL = "http://49.233.248.218:9999/sql_api";
    private OkHttpClient client;

    public myDatabase(){
        client = new OkHttpClient();
    }

    public void executeCommand(String command, String status, Callback callback) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String json = String.format("{\"command\":\"%s\", \"status\":\"%s\"}", command, status);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("API-Key", BuildConfig.SQL_API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

}
