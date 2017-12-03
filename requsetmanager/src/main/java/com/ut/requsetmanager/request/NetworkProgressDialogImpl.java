package com.ut.requsetmanager.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by jianjianhong on 2017/11/21.
 */

public class NetworkProgressDialogImpl implements NetworkProgressDialog {

    private final static String TAG = "NetworkProgressDialog";

    private Context context;
    private ProgressDialog progressDialog;


    public NetworkProgressDialogImpl (Context context, int style, NetworkRequestImpl request){
        this.context = context;
        initProgressDialog(style, request);
    }

    private void initProgressDialog(final int style, final NetworkRequestImpl request) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStyle(style);
                //progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setOnCancelListener(request);
            }
        });
    }

    @Override
    public void showProgress(final String message) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage(message);
                progressDialog.show();
            }
        });
    }

    @Override
    public void hideProgress() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.cancel();
            }
        });
    }

    @Override
    public void updateProgress(final String message, final int progress) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage(message);
                progressDialog.setProgress(progress);
            }
        });
    }
}
