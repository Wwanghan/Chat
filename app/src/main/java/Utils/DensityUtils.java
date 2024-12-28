package Utils;

import android.content.Context;

public class DensityUtils {
    // 将 px 转换为 dp
    public static int densityDP(Context context, int n) {
        return (int) (n * context.getResources().getDisplayMetrics().density + 0.5f);
    }

}
