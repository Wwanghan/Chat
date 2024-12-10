package Animations;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class ViewTransitionAnimator {

    // 隐藏 View 的动画
    public static void hideViewWithAnimation(View view, float translateY, long duration, Runnable endAction) {
        if (view.getVisibility() == View.GONE) return; // 防止重复执行
        view.animate()
                .alpha(0f) // 渐隐效果
                .translationY(translateY) // Y轴偏移
                .setDuration(duration) // 动画持续时间
                .setInterpolator(new DecelerateInterpolator()) // 平滑插值器
                .withEndAction(() -> {
                    view.setVisibility(View.GONE); // 动画结束后隐藏
                    view.setAlpha(1f); // 恢复透明度
                    if (endAction != null) endAction.run(); // 执行回调
                })
                .start();
    }

    // 显示 View 的动画
    public static void showViewWithAnimation(View view, float startTranslateY, long duration) {
        if (view.getVisibility() == View.VISIBLE) return; // 防止重复执行
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f); // 初始透明度
        view.setTranslationY(startTranslateY); // 初始偏移
        view.animate()
                .alpha(1f) // 渐显效果
                .translationY(0f) // 回到原位置
                .setDuration(duration) // 动画持续时间
                .setInterpolator(new DecelerateInterpolator()) // 平滑插值器
                .start();
    }
}

