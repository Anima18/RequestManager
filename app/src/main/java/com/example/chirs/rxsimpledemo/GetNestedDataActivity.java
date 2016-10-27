package com.example.chirs.rxsimpledemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.example.chirs.rxsimpledemo.entity.DataObject;
import com.example.chirs.rxsimpledemo.entity.ObjectShowData;
import com.example.chirs.rxsimpledemo.entity.Utyybanben;
import com.example.requestmanager.NetworkRequest;
import com.example.requestmanager.callBack.DataCallBack;
import com.example.requestmanager.service.Service;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

;

/**
 * Created by jianjianhong on 2016/6/12.
 */
public class GetNestedDataActivity extends BaseActivity implements View.OnClickListener {

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
                NetworkRequest.cancel(subscription);
                //Toast.makeText(GetNestedDataActivity.this, "请求结束", Toast.LENGTH_SHORT).show();
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
        String name = nameEt.getText().toString();
        showProgress("正在查询...");

        return NetworkRequest.create()
                .setUrl("http://192.168.60.242:8080/scs/mobile/getdtshowobject.do?username=&pwd=&projectcode=utyingyongbanben&showobjectcode=utyybanben")
                .setMethod(Service.GET_TYPE)
                .setDataClass(ObjectShowData.class)
                //.setDataType(new TypeToken<Utyybanben>(){}.getType())
                .request()
                .flatMap(new Func1<ObjectShowData, Observable<?>>() {
                    @Override
                    public Observable<?> call(ObjectShowData o) {
                        Log.i("WebSerivec", o.toString());
                        return NetworkRequest.create().setUrl("http://192.168.60.242:8080/scs/mobile/getdtobjectdata.do?&username=&pwd=&projectcode=utyingyongbanben&objectcode=utyybanben&pagesize=5&pagenum=1&sort=&condi=")
                                .setMethod(Service.GET_TYPE)
                                //.setDataClass(DataObject.class)
                                .setDataType(new TypeToken<DataObject<Utyybanben>>(){}.getType())
                                .request().getObservable();
                    }
                })
                .response(new DataCallBack<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        Log.i("WebSerivec", data.toString());
                        /*if(data == null) {
                            resultTv.setText("没有数据");
                        }else {
                            List<Utyybanben> utyybanbens = (List<Utyybanben>)data.data.rows;
                            Log.i("GetDataActivity", utyybanbens.get(0).getAppname());
                            resultTv.setText(utyybanbens.toString());
                        }*/
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
