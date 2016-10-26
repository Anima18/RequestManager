package com.example.chirs.rxsimpledemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chirs.rxsimpledemo.entity.DataObject;
import com.example.chirs.rxsimpledemo.entity.User;
import com.example.requestmanager.NetworkRequest;
import com.example.requestmanager.callBack.DataCallBack;

import rx.Subscription;

/**
 * 获取
 * Created by jianjianhong on 2016/6/12.
 */
public class PostDataActivity extends BaseActivity implements View.OnClickListener {

    private Button searchBt;
    private Subscription subscription;
    private TextView resultTv;

    private final static String TAG = "PostDataActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_collection);
        initView();
        initEvent();
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
                //Toast.makeText(PostDataActivity.this, "请求结束", Toast.LENGTH_SHORT).show();
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

        return NetworkRequest.create(new DataCallBack<DataObject<User>>() {
            @Override
            public void onSuccess(DataObject<User> data) {
                if(data == null) {
                    resultTv.setText("没有数据");
                }else {
                    //Log.i("WebService", data.data.get(0).get("userName"));
                    resultTv.setText(data.toString());
                }
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
        .setContext(this)
        .setUrl("http://192.168.1.103:8080/WebService/security/security_list.action")
        .setDataClass(DataObject.class)
        .postData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkRequest.cancel(this);
    }
}
