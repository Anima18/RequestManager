package com.example.chirs.rxsimpledemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chirs.rxsimpledemo.entity.DataObject;
import com.example.chirs.rxsimpledemo.entity.ObjectShowData;
import com.example.chirs.rxsimpledemo.entity.User;
import com.example.requestmanager.NetworkRequest;
import com.example.requestmanager.callBack.DataCallBack;
import com.example.requestmanager.entity.WebServiceParam;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by jianjianhong on 2016/6/12.
 */
public class GetSeqDataActivity extends BaseActivity implements View.OnClickListener {

    private Button searchBt;
    private TextView resultTv;

    private final static String TAG = "GetDataActivity";

    @NonNull
    private ObjectShowData showObject = new ObjectShowData();
    @NonNull
    private DataObject<User> dataObject = new DataObject<>();

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

    }

    @Override
    public void onClick(@NonNull View v) {
        switch (v.getId()) {
            case R.id.goAct_bt:
                getObjectData();
                break;
        }
    }

    private void getObjectData() {
        resultTv.setText("");
        showProgress("正在查询...");
        List<WebServiceParam> params = new ArrayList<>();
        params.add(new WebServiceParam(BASE_PATH + "userInfo/getAllUserInfoLayer.action", NetworkRequest.GET_TYPE, new TypeToken<DataObject<User>>(){}.getType()));
        params.add(new WebServiceParam(BASE_PATH + "userInfo/getAllUserInfo.action", NetworkRequest.GET_TYPE, new TypeToken<DataObject<User>>(){}.getType()));

        new NetworkRequest.Builder(this)
        .params(params)
        .getSeqData(new DataCallBack<List<Object>>() {
            @Override
            public void onSuccess(@NonNull List<Object> dataList) {
                resultTv.setText("顺序请求成功");
                Toast.makeText(GetSeqDataActivity.this, "请求成功，请求数量："+dataList.size(), Toast.LENGTH_SHORT).show();
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
