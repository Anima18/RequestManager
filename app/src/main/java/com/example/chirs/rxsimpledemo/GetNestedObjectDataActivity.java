package com.example.chirs.rxsimpledemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chirs.rxsimpledemo.entity.User;
import com.example.webserviceutil.SubscriptionManager;
import com.example.webserviceutil.WebService;
import com.example.webserviceutil.callBack.ObjectCallBack;
import com.example.webserviceutil.entity.WebServiceParam;
import com.example.webserviceutil.service.Service;

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

    private EditText nameEt;
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
        nameEt = (EditText)findViewById(R.id.goAct_et);
        searchBt = (Button)findViewById(R.id.goAct_bt);
        resultTv = (TextView)findViewById(R.id.goAct_result);
    }

    public void initEvent() {
        searchBt.setOnClickListener(this);

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                WebService.cancel(subscription);
                Toast.makeText(GetNestedObjectDataActivity.this, "请求结束", Toast.LENGTH_SHORT).show();
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

    private Subscription getObjectData() {
        resultTv.setText("");
        String name = nameEt.getText().toString();
        showProgress("正在查询...");
        final WebServiceParam param = new WebServiceParam(BASE_PATH+"security/security_get.action?user.name="+name, Service.GET_TYPE, User.class);
        final WebServiceParam param2 = new WebServiceParam(BASE_PATH+"security/security_get.action?user.name="+name, Service.GET_TYPE, User.class);

        Subscription subscribe = WebService.getObjectObservable(GetNestedObjectDataActivity.this, param)
            .flatMap(new Func1<Object, Observable<?>>() {
                @Override
                public Observable<?> call(Object o) {
                    if(o == null) {
                        Log.i("GetObjectDataActivity", "第一个请求：null");
                    }else {
                        Log.i("GetObjectDataActivity", "第一个请求："+((User)o).toString());
                    }

                    return WebService.getObjectObservable(GetNestedObjectDataActivity.this, param2);
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(WebService.getObjectSubscriber(param, new ObjectCallBack<Object>() {
                @Override
                public void onSuccess(Object data) {
                    if(data == null) {
                        resultTv.setText("没有数据");
                    }else {
                        resultTv.setText(data.toString());
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
                    SubscriptionManager.removeSubscription(param);
                    SubscriptionManager.removeSubscription(param2);
                }
            }));
        SubscriptionManager.addSubscription(param2, subscribe);
        return subscribe;
    }

    private Subscription getObjectData2() {
        resultTv.setText("");
        String name = nameEt.getText().toString();
        showProgress("正在查询...");
        final WebServiceParam param = new WebServiceParam("http://192.168.1.103:8080/WebService/security/security_get.action?user.name="+name, Service.GET_TYPE, User.class);

        Subscription subscribe = WebService.getObjectObservable(GetNestedObjectDataActivity.this, param)
                .flatMap(new Func1<Object, Observable<?>>() {
                    @Override
                    public Observable<?> call(Object o) {
                        if(o == null) {
                            Log.i("GetObjectDataActivity", "第一个请求：null");
                        }else {
                            Log.i("GetObjectDataActivity", "第一个请求："+o.toString());
                        }

                        return WebService.getObjectObservable(GetNestedObjectDataActivity.this, param);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(WebService.getObjectSubscriber(param, new ObjectCallBack<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        if(data == null) {
                            resultTv.setText("没有数据");
                        }else {
                            resultTv.setText(data.toString());
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
                        //SubscriptionManager.removeSubscription(param);
                        //SubscriptionManager.removeSubscription(param2);
                    }
                }));
        //SubscriptionManager.addSubscription(param2, subscribe);
        return subscribe;
    }
}
