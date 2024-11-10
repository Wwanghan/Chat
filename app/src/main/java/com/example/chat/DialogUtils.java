package com.example.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.LinearLayout;

public class DialogUtils {

    public static void showDialog(Context context, String category , String title, String positiveButtonText,
                                         EditText input, DialogInterface.OnClickListener onPositiveClick) {
        // 创建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        if (category.equals("input")){
            // 如果category等于input，则展示带有输入框的对话框
            // 设置输入框样式等（如果传入的input没有样式，可以在这里处理）
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(40, 20, 0, 0);
            input.setLayoutParams(lp);
            input.setBackgroundResource(R.drawable.input_style); // 自定义样式
            input.setScaleX(0.9f); // 缩小输入框大小
            input.setScaleY(0.9f);
            builder.setView(input);  // 设置输入框
        }

        builder.setTitle(title); // 设置标题
        builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
            // 触发点击事件
            onPositiveClick.onClick(dialog, which);
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.show();
    }
}



