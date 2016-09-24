package com.example.chirs.rxsimpledemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Chris on 2015/9/2.
 */
public class BaseActivity extends AppCompatActivity{
    protected ProgressDialog progressDialog;

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
