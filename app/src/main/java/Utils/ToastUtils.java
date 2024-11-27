package Utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Toast toast;

    /**
     * 自定义 Toast ，解决了 Toast 重叠的问题
     * @param context 上下文
     * @param message 消息内容
     */
    public static void showToast(Context context, String message) {
        if (toast != null) {
            toast.cancel(); // 取消当前显示的 Toast
        }
        toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}

