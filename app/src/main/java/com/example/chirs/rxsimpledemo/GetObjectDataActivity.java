package com.example.chirs.rxsimpledemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chirs.rxsimpledemo.entity.ResultData;
import com.example.webserviceutil.WebService;
import com.example.webserviceutil.callBack.ObjectCallBack;
import com.example.webserviceutil.entity.WebServiceParam;
import com.example.webserviceutil.service.Service;

import rx.Subscription;

import static android.R.attr.name;

;

/**
 * Created by jianjianhong on 2016/6/12.
 */
public class GetObjectDataActivity extends BaseActivity implements View.OnClickListener {

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
                WebService.cancel(subscription);
                Toast.makeText(GetObjectDataActivity.this, "请求结束", Toast.LENGTH_SHORT).show();
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
        WebServiceParam param = new WebServiceParam("http://192.168.1.103:8080/WebService/security/security_list.action?user.name="+name, Service.GET_TYPE, ResultData.class);
        return WebService.getData(GetObjectDataActivity.this, param, new ObjectCallBack<ResultData>() {
            @Override
            public void onSuccess(ResultData data) {
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
            }
        });
    }
}
