package com.example.chirs.rxsimpledemo;


import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Chris on 2015/9/2.
 */
public class ProgressDialogUtil {

    @NonNull
    public static ProgressDialog showProgessDialog(Context context, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        //dialog.setIndeterminate(true);
        return dialog;
    }

    @NonNull
    public static ProgressDialog showProgessDialog(Context context, String title, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        return dialog;
    }

}
