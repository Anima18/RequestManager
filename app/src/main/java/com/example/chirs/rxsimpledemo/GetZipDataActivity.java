package com.example.chirs.rxsimpledemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chirs.rxsimpledemo.entity.DataObject;
import com.example.chirs.rxsimpledemo.entity.User;
import com.example.requestmanager.NetworkRequest;
import com.google.gson.reflect.TypeToken;
import com.trello.rxlifecycle.android.ActivityEvent;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * Created by jianjianhong on 2016/6/12.
 */
public class GetZipDataActivity extends BaseActivity implements View.OnClickListener {

    private Button searchBt;
    private TextView resultTv;

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

        Observable<DataObject<User>> observable1 = new NetworkRequest.Builder()
                .url(BASE_PATH + "userInfo/getAllUserInfoLayer.action")
                .method(NetworkRequest.GET_TYPE)
                .dataType(new TypeToken<DataObject<User>>(){}.getType())
                .request();

        Observable<DataObject<User>> observable2 = new NetworkRequest.Builder()
                .url(BASE_PATH + "userInfo/getAllUserInfo.action")
                .method(NetworkRequest.GET_TYPE)
                .dataType(new TypeToken<DataObject<User>>(){}.getType())
                .request();

        Observable<DataObject<User>> observable3 = new NetworkRequest.Builder()
                .url(BASE_PATH + "userInfo/getAllUserInfo.action")
                .method(NetworkRequest.GET_TYPE)
                .dataType(new TypeToken<DataObject<User>>(){}.getType())
                .request();
        Observable.zip(observable1, observable2, observable3, new Func3<DataObject<User>, DataObject<User>, DataObject<User>, DataObject<User>>() {
            @Override
            public DataObject<User> call(DataObject<User> userDataObject, DataObject<User> userDataObject2, DataObject<User> userDataObject3) {
                Log.i("WebService", "第一个请求："+ userDataObject.data.rows.get(0).toString());
                Log.i("WebService", "第二个请求："+ userDataObject2.data.rows.get(0).toString());
                Log.i("WebService", "第三个请求："+ userDataObject3.data.rows.get(0).toString());
                return userDataObject;
            }
        })
        .compose(this.<DataObject<User>>bindUntilEvent(ActivityEvent.PAUSE))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<DataObject<User>>() {
            @Override
            public void onCompleted() {
                hideProgress();
            }

            @Override
            public void onError(Throwable e) {
                Log.i("WebService", e.getMessage());
                resultTv.setText(e.getMessage());
            }

            @Override
            public void onNext(DataObject<User> userDataObject) {
                resultTv.setText("并发请求成功");
            }
        });
    }
}
