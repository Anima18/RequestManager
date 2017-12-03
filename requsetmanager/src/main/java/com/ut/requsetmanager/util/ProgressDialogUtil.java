package com.ut.requsetmanager.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.ut.requsetmanager.request.NetworkRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianjianhong on 2017/1/21.
 */

public class ProgressDialogUtil {
    private ProgressDialog progressDialog;
    private Context mContext;
    private List<NetworkRequest> requestList;
    private static int style = ProgressDialog.STYLE_SPINNER;

    public ProgressDialogUtil(Context context) {
        this(context, style);
    }

    public ProgressDialogUtil(Context context, int style) {
        this.mContext = context;
        this.style = style;
        this.requestList = new ArrayList<>();
        initProgressDialog();
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(style);
        //progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);

        /*progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                NetworkRequest.cancelByTag("normal");
            }
        });*/
    }

    public void addRequest(NetworkRequest request) {
        this.requestList.add(request);
    }

    public List<NetworkRequest> getRequests() {
        return this.requestList;
    }

    public void showProgress(String message) {
        if(progressDialog == null) {
            initProgressDialog();
        }else {
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    public void hideProgress() {
        if(progressDialog != null) {
            progressDialog.cancel();
            progressDialog.dismiss();
        }
    }

    public void updateProgress(String message, int progress) {
        if(progressDialog != null) {
            progressDialog.setMessage(message);
            progressDialog.setProgress(progress);
        }
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }
}
