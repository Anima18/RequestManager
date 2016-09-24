package com.example.chirs.rxsimpledemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chirs.rxsimpledemo.entity.User;
import com.example.webserviceutil.WebService;
import com.example.webserviceutil.callBack.ObjectCallBack;
import com.example.webserviceutil.entity.WebServiceParam;
import com.example.webserviceutil.service.Service;

import rx.Subscription;

;

/**
 * Created by jianjianhong on 2016/6/12.
 */
public class GetObjectDataActivity extends BaseActivity implements View.OnClickListener {

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
        String name = nameEt.getText().toString();
        showProgress("正在查询...");
        WebServiceParam param = new WebServiceParam("http://192.168.1.103:8080/WebService/security/security_get.action?user.name="+name, Service.GET_TYPE, User.class);
        return WebService.getObject(GetObjectDataActivity.this, param, new ObjectCallBack<User>() {
            @Override
            public void onSuccess(User data) {
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
