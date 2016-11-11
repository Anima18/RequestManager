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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goAct_bt:
                getObjectData();
                break;
        }
    }

    private void getObjectData() {
        resultTv.setText("");
        showProgress("正在查询...");

        new NetworkRequest.Builder(this)
            .url(BASE_PATH + "userInfo/getAllUserInfoLayer.action")
            .dataType(new TypeToken<DataObject<User>>(){}.getType())
            .method(NetworkRequest.GET_TYPE)
            .call(new DataCallBack<DataObject<User>>() {
                @Override
                public void onSuccess(DataObject<User> data) {
                    Log.i("WebService", data.data.rows.get(0).getName());
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
            });
    }
}
