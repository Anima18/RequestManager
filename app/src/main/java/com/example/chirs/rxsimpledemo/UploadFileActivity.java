package com.example.chirs.rxsimpledemo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chirs.rxsimpledemo.entity.DataObject;
import com.example.chirs.rxsimpledemo.entity.User;
import com.example.requestmanager.NetworkRequest;
import com.example.requestmanager.callBack.ProgressCallBack;
import com.example.requestmanager.entity.FileObject;

import java.io.File;
import java.util.Map;

import rx.Subscription;

/**
 * Created by jianjianhong on 2016/6/12.
 */
public class UploadFileActivity extends BaseActivity implements View.OnClickListener {

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

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                NetworkRequest.cancel(subscription);
                //Toast.makeText(UploadFileActivity.this, "请求结束", Toast.LENGTH_SHORT).show();
            }
        });
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

        String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RxJava/";
        File file = new File(basePath);
        String[] fileNameArray = file.list();

        Map<String, Object> param = new ArrayMap<>();
        param.put("user.name", "Anima18");
        param.put("user.password", "123456");
        for(String fileName : fileNameArray) {
            Log.d(TAG, fileName);
            param.put(fileName, new FileObject(basePath + fileName));
        }
        return NetworkRequest.create(new ProgressCallBack<DataObject<User>>() {
            @Override
            public void onProgress(String fileName, int progress) {
                updataProgress(fileName, progress);
            }

            @Override
            public void onSuccess(DataObject<User> data) {
                resultTv.setText(data.toString());
            }

            @Override
            public void onFailure(int code, String message) {
                resultTv.setText("code："+ code +", message:"+message);
            }

            @Override
            public void onCompleted() {
                hideProgress();
            }
        })
        .setUrl(BASE_PATH + "security/security_uploadList.action")
        .setContext(this)
        .setParam(param)
        .setDataClass(DataObject.class)
        .uploadFile();
    }
}
