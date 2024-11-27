package Utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chat.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyDatabaseUtils {

    // 接口前缀
    private static final String API_URL = "http://49.233.248.218:9999/sql_api";
    private static final Logger log = LoggerFactory.getLogger(MyDatabaseUtils.class);
    private static OkHttpClient client = new OkHttpClient();

    private static ArrayList<String> queryDataByPhoneNumber = new ArrayList<>();

    /**
     * 用于执行 sql 命令
     * @param command 传递一个 sql 命令
     * @param status 一共有四种状态 add , delete , change , query，对应增删改查
     * @param callback 回调函数
     */
    public static void executeCommand(String command, String status, Callback callback) {
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

    public static void queryByPhoneNumber(String phoneNumber, ResultCallback<ArrayList<String>> callback) {
        ArrayList<String> queryDataByPhoneNumber = new ArrayList<>();

        String command = String.format("SELECT * FROM user WHERE phoneNumber = '%s'", phoneNumber);
        String status = "query";
        executeCommand(command, status, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onFailure(e); // 通知回调请求失败
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray queryData = jsonObject.getJSONArray("data");

                    for (int i = 0; i < queryData.length(); i++) {
                        JSONArray innerArray = queryData.getJSONArray(i);
                        for (int j = 0; j < innerArray.length(); j++) {
                            queryDataByPhoneNumber.add(innerArray.getString(j));
                        }
                    }

                    // 通知回调成功，并返回结果
                    callback.onSuccess(queryDataByPhoneNumber);

                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailure(e);
                }
            }
        });
    }

    // 定义回调接口
    public interface ResultCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }
}
