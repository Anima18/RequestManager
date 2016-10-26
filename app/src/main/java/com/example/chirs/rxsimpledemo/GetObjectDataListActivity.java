package com.example.chirs.rxsimpledemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chirs.rxsimpledemo.entity.DataObject;
import com.example.requestmanager.NetworkRequest;
import com.example.requestmanager.callBack.DataCallBack;
import com.example.requestmanager.entity.WebServiceParam;
import com.example.requestmanager.service.Service;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by jianjianhong on 2016/6/12.
 */
public class GetObjectDataListActivity extends BaseActivity implements View.OnClickListener {

    private EditText nameEt;
    private Button searchBt;
    private TextView resultTv;
    private Subscription subscription;

    private final static String TAG = "GetDataActivity";

    private DataObject<Object> dataObject = new DataObject<>();
    private DataObject<Object> dataObject2 = new DataObject<>();
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
                NetworkRequest.cancel(subscription);
                //Toast.makeText(GetObjectDataListActivity.this, "请求结束", Toast.LENGTH_SHORT).show();
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
        requestIndex = 0;
        List<WebServiceParam> params = new ArrayList<>();
        params.add(new WebServiceParam("http://192.168.1.103:8080/WebService/security/security_list.action", Service.GET_TYPE, DataObject.class));
        params.add(new WebServiceParam("http://192.168.1.103:8080/WebService/security/security_list.action",
                Service.GET_TYPE, DataObject.class));

        return NetworkRequest.create(new DataCallBack<Object>() {
            @Override
            public void onSuccess(Object data) {
                if(requestIndex == 0) {
                    dataObject = (DataObject<Object>)data;
                    //Log.i("WebService", showObject.getData().get(0).getLtfield().toString());
                    Toast.makeText(GetObjectDataListActivity.this, "第一个请求成功", Toast.LENGTH_SHORT).show();
                }else if(requestIndex == 1) {
                    dataObject2 = (DataObject<Object>)data;
                    //Log.i("WebService", dataObject.data.rows.get(0).get("appname"));
                    Toast.makeText(GetObjectDataListActivity.this, "第二个请求成功", Toast.LENGTH_SHORT).show();
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
        })
        .setContext(this)
        .setWebServerParamList(params)
        .getObjectInSeq();
    }
}
