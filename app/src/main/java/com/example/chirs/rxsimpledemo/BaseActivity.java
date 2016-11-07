package com.example.chirs.rxsimpledemo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Chris on 2015/9/2.
 */
public class BaseActivity extends AppCompatActivity{
    protected ProgressDialog progressDialog;
    protected final static String BASE_PATH = "http://192.168.60.176:8080/webService/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = ProgressDialogUtil.showProgessDialog(BaseActivity.this, "");
    }

    public void showProgress(String message) {
        if(progressDialog == null) {
            progressDialog = ProgressDialogUtil.showProgessDialog(BaseActivity.this, message);
        }else {
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    public void hideProgress() {
        progressDialog.cancel();
    }

    public void updataProgress(String message, int progress) {
        progressDialog.setMessage(message);
        progressDialog.setProgress(progress);
    }

}
