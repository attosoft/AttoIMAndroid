package cn.id0755.im.utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import cn.id0755.sdk.android.config.ImApplication;

public class ToastUtil {

    public static Handler UI_HANDLER = new Handler(Looper.getMainLooper());

    public static void show(String text) {
        Toast toast = Toast.makeText(ImApplication.getInstance(),text,Toast.LENGTH_SHORT);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            toast.show();
        } else {
            UI_HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    toast.show();
                }
            });
        }
    }
}
