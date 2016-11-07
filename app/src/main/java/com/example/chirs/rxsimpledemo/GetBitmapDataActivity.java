package com.example.chirs.rxsimpledemo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.requestmanager.NetworkRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianjianhong on 2016/6/13.
 */
public class GetBitmapDataActivity extends BaseActivity {

    private ListView gridView;

    private List<String> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_bitmap);
        urlList = initData();
        initView();
    }

    public void initView() {
        gridView = (ListView)findViewById(R.id.gbAct_gridView);
        BitmapAdapter adapter = new BitmapAdapter(GetBitmapDataActivity.this, urlList);
        gridView.setAdapter(adapter);
       /* gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NetworkRequest.cancel(urlList.get(position));
            }
        });*/
    }

    public List<String> initData() {
        List<String> urlList = new ArrayList<>();
        urlList.add(BASE_PATH + "file/doughnut.png");
        urlList.add(BASE_PATH + "file/darts.png");
        urlList.add(BASE_PATH + "file/life-saver.png");
        urlList.add(BASE_PATH + "file/beer-cap.png");
        urlList.add(BASE_PATH + "file/compass.png");
        urlList.add(BASE_PATH + "file/clock.png");
        urlList.add(BASE_PATH + "file/beer-cap-dribble.png");
        urlList.add(BASE_PATH + "file/1.png");
        urlList.add(BASE_PATH + "file/2.png");
        urlList.add(BASE_PATH + "file/3.png");
        urlList.add(BASE_PATH + "file/4.png");
        urlList.add(BASE_PATH + "file/5.png");
        urlList.add(BASE_PATH + "file/6.png");
        urlList.add(BASE_PATH + "file/7.png");
        return urlList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //NetworkRequest.cancelAll(GetBitmapDataActivity.this);
    }
}
