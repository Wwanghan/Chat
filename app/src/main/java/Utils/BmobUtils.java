package Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.chat.BuildConfig;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class BmobUtils {

    private static Boolean isSuccess = false;

    public static void Init(Context context){
        Bmob.initialize(context , BuildConfig.BMOB_APPLICATION_KEY);
    }

    // 发送短信
    public static void sendCode(Context context , String phoneNumber){
        BmobSMS.requestSMSCode(phoneNumber, "Chat_Codes", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null){
                    Toast.makeText(context , "短信发送成功 " + integer , Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(context , "发送验证码失败：" + e.getErrorCode()  , Toast.LENGTH_SHORT).show();
                    Log.e("toad", "SEND_CODE_ERROR: " + e.getMessage());
                }
            }
        });
    }

    // 验证短信
    // 验证验证码的方法
    public static void verifyCode(Context context, String phoneNumber, String code, VerifyCallback callback) {
        BmobSMS.verifySmsCode(phoneNumber, code, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    // 验证成功
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } else {
                    // 验证失败
                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                    // 显示失败的 Toast 提示
                    ToastUtils.showToast(context, "短信验证失败：" + e.getMessage());
                }
            }
        });
    }

    public interface VerifyCallback{
        void onSuccess();  // 验证成功
        void onFailure(String errorMessage);  // 验证失败
    }


    // 校验手机号是否合法
    public static boolean isValidPhoneNumber(String phoneNumber) {
        // 使用正则表达式校验手机号
        // 这里只是一个常见的校验规则，可以根据实际情况调整
        String regex = "^(1[3-9])\\d{9}$";
        return phoneNumber != null && phoneNumber.matches(regex);
    }

}
