package com.hzease.tomeet.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Key on 2016/12/21 13:53
 * email: MrKey.K@gmail.com
 * description:
 */

public class KeyboardUtils {

    /**
     * 打开键盘
     * @param context
     * @param editText
     */
    public static void openKeyboard(Context context, View editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, 0);
    }
    public static void hideKeyboard(EditText editText, InputMethodManager inputMethodManager) {
        editText.clearFocus();
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 关闭键盘，自动选择editText
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(),0);
    }
}
