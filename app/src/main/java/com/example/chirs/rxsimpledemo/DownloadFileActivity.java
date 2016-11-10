package com.example.chirs.rxsimpledemo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.requestmanager.NetworkRequest;
import com.example.requestmanager.callBack.ProgressCallBack;

import rx.Subscription;

/**
 * Created by jianjianhong on 2016/6/12.
 */
public class DownloadFileActivity extends BaseActivity implements View.OnClickListener {

    private Button searchBt;
    private Subscription subscription;
    private TextView resultTv;

    private final static String TAG = "PostCollectionData";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_collection);
        initView();
        initEvent();
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    public void initView() {
        searchBt = (Button)findViewById(R.id.gcAct_bt);
        resultTv = (TextView)findViewById(R.id.gcAct_result);
    }

    public void initEvent() {
        searchBt.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gcAct_bt:
                subscription = getCollectionData();
                break;
        }
    }

    private Subscription getCollectionData() {
        showProgress("正在查询...");
        resultTv.setText("");

        return new NetworkRequest.Builder()
                .lifecycleProvider(this)
                .url(BASE_PATH + "file/LuaDemo.rar")
                .dataClass(Boolean.class)
                .param("fileName", "LuaDemo.rar")
                .method(NetworkRequest.POST_TYPE)
                .downloadFile(new ProgressCallBack<Boolean>() {
                    @Override
                    public void onProgress(String fileName, int progress) {
                        updataProgress(fileName, progress);
                    }

                    @Override
                    public void onSuccess(Boolean data) {
                        resultTv.setText("下载成功！");
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        resultTv.setText("code："+ code +", message:"+message);
                    }

                    @Override
                    public void onCompleted() {
                        hideProgress();
                    }
                });
    }
}
