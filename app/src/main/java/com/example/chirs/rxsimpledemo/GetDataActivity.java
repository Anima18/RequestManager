package com.example.chirs.rxsimpledemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chirs.rxsimpledemo.entity.DataObject;
import com.example.chirs.rxsimpledemo.entity.User;
import com.example.requestmanager.NetworkRequest;
import com.example.requestmanager.callBack.DataCallBack;
import com.google.gson.reflect.TypeToken;

import rx.Subscription;

/**
 * Created by jianjianhong on 2016/6/12.
 */
public class GetDataActivity extends BaseActivity implements View.OnClickListener {

    private Button searchBt;
    private TextView resultTv;
    private Subscription subscription;

    private final static String TAG = "GetDataActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_object);
        initView();
        initEvent();
    }

    public void initView() {
        searchBt = (Button)findViewById(R.id.goAct_bt);
        resultTv = (TextView)findViewById(R.id.goAct_result);
    }

    public void initEvent() {
        searchBt.setOnClickListener(this);

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                NetworkRequest.cancel(subscription);
                //Toast.makeText(GetDataActivity.this, "请求结束", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goAct_bt:
                subscription = getObjectData();
                break;
        }
    }

    private Subscription getObjectData() {
        resultTv.setText("");
        showProgress("正在查询...");
        return NetworkRequest.create()
        .setContext(this)
        .setUrl("http://192.168.1.103:8080/webService/userInfo/getAllUserInfoLayer.action")
        .setDataType(new TypeToken<DataObject<User>>(){}.getType())
        .setMethod(NetworkRequest.GET_TYPE)
        .send(new DataCallBack<DataObject<User>>() {
            @Override
            public void onSuccess(DataObject<User> data) {
                if(data == null) {
                    resultTv.setText("没有数据");
                }else {
                    Log.i("WebService", data.data.rows.get(0).toString());
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
        });
    }
}
