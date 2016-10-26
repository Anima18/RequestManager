package com.example.chirs.rxsimpledemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chirs.rxsimpledemo.entity.DataObject;
import com.example.requestmanager.NetworkRequest;
import com.example.requestmanager.callBack.DataCallBack;
import com.example.requestmanager.service.Service;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

;

/**
 * Created by jianjianhong on 2016/6/12.
 */
public class GetNestedObjectDataActivity extends BaseActivity implements View.OnClickListener {

    private Button searchBt;
    private TextView resultTv;
    private Subscription subscription;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_object);
        initView();
        initEvent();
    }

    private void initView() {
        searchBt = (Button)findViewById(R.id.goAct_bt);
        resultTv = (TextView)findViewById(R.id.goAct_result);
    }

    public void initEvent() {
        searchBt.setOnClickListener(this);

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                NetworkRequest.cancel(subscription);
                //Toast.makeText(GetNestedObjectDataActivity.this, "请求结束", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goAct_bt:
                subscription = getObjectData2();
                break;
        }
    }

    private Subscription getObjectData2() {
        resultTv.setText("");
        showProgress("正在查询...");

        return NetworkRequest.create().setUrl("http://192.168.1.103:8080/WebService/security/security_list.action")
                .setMethod(Service.GET_TYPE)
                .setDataClass(DataObject.class)
                .request()
                .flatMap(new Func1<Object, Observable<?>>() {
                    @Override
                    public Observable<?> call(Object o) {
                        if(o == null) {
                            Log.i("GetDataActivity", "第一个请求：null");
                        }else {
                            Log.i("GetDataActivity", "第一个请求："+o.toString());
                        }

                        return NetworkRequest.create().setUrl("http://192.168.1.103:8080/WebService/security/security_list.action")
                                .setMethod(Service.GET_TYPE)
                                .setDataClass(DataObject.class)
                                .request();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(NetworkRequest.response(null, new DataCallBack<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        if(data == null) {
                            resultTv.setText("没有数据");
                        }else {
                            DataObject dataObject = (DataObject)data;
                            //List<Map<String, String>> utyybanbens = (List<Map<String, String>>)dataObject.data.rows;
                            //Log.i("GetDataActivity", utyybanbens.get(0).get("appname"));
                            resultTv.setText(dataObject.toString());
                        }
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        String errorMessage = "code："+ code +", message:"+message;
                        resultTv.setText(errorMessage);
                    }

                    @Override
                    public void onCompleted() {
                        hideProgress();
                    }
                }));
    }
}
