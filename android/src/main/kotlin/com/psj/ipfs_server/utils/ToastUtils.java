package com.psj.ipfs_server.utils;

import android.content.Context;
import android.widget.Toast;


public class ToastUtils {

    private static ToastUtils toastUtils;
    public Toast toast;

    public static ToastUtils getInstance() {
        if (toastUtils==null) {
            toastUtils = new ToastUtils();
        }
        return toastUtils;
    }

    public Toast getToast(Context context) {
        if (toast==null) {
            toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
        }
        return toast;
    }

    public static void show(String str,Context context) {
        Toast t = getInstance().getToast(context);
        t.setText(str);
        t.show();
    }
}
