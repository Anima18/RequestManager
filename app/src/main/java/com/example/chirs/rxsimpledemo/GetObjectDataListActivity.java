package com.example.chirs.rxsimpledemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chirs.rxsimpledemo.entity.User;
import com.example.webserviceutil.WebService;
import com.example.webserviceutil.callBack.ObjectCallBack;
import com.example.webserviceutil.entity.WebServiceParam;
import com.example.webserviceutil.service.Service;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

import static android.R.attr.name;

/**
 * Created by jianjianhong on 2016/6/12.
 */
public class GetObjectDataListActivity extends BaseActivity implements View.OnClickListener {

    private Button searchBt;
    private TextView resultTv;
    private Subscription subscription;

    private final static String TAG = "GetObjectDataActivity";

    private User user1 = new User();
    private User user2 = new User();
    private int requestIndex = 0;

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
                WebService.cancel(subscription);
                Toast.makeText(GetObjectDataListActivity.this, "请求结束", Toast.LENGTH_SHORT).show();
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
        requestIndex = 0;
        List<WebServiceParam> params = new ArrayList<>();
        params.add(new WebServiceParam("http://192.168.1.103:8080/WebService/security/security_get.action?user.name="+name, Service.GET_TYPE, User.class));
        params.add(new WebServiceParam("http://192.168.1.103:8080/WebService/security/security_get.action?user.name="+name, Service.GET_TYPE, User.class));

        return WebService.getObjectInSeq(this, params, new ObjectCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                resultTv.setText(data.toString());
                if(requestIndex == 0) {
                    user1 = (User)data;
                    Toast.makeText(GetObjectDataListActivity.this, "第一个请求成功:"+user1.toString(), Toast.LENGTH_SHORT).show();
                }else if(requestIndex == 1) {
                    user2 = (User)data;
                    Toast.makeText(GetObjectDataListActivity.this, "第二个请求成功:"+user2.toString(), Toast.LENGTH_SHORT).show();
                }
                requestIndex++;
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
        });
    }
}
