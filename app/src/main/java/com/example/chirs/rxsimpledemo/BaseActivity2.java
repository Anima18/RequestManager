package com.example.chirs.rxsimpledemo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by Chris on 2015/9/2.
 */
public class BaseActivity2 extends AppCompatActivity {
    protected ProgressDialog progressDialog;
    protected final static String BASE_PATH = "http://192.168.60.139:8080/webService/";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = ProgressDialogUtil.showProgessDialog(BaseActivity2.this, "");

    }


    public void showProgress(String message) {
        if(progressDialog == null) {
            progressDialog = ProgressDialogUtil.showProgessDialog(BaseActivity2.this, message);
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
