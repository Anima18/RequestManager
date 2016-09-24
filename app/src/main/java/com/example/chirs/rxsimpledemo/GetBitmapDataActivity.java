package com.example.chirs.rxsimpledemo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.webserviceutil.WebService;

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
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WebService.cancel(urlList.get(position));
            }
        });
    }

    public List<String> initData() {
        List<String> urlList = new ArrayList<>();
        urlList.add("http://192.168.1.103:8080/WebService/file/doughnut.png");
        urlList.add("http://192.168.1.103:8080/WebService/file/darts.png");
        urlList.add("http://192.168.1.103:8080/WebService/file/life-saver.png");
        urlList.add("http://192.168.1.103:8080/WebService/file/beer-cap.png");
        urlList.add("http://192.168.1.103:8080/WebService/file/compass.png");
        urlList.add("http://192.168.1.103:8080/WebService/file/clock.png");
        urlList.add("http://192.168.1.103:8080/WebService/file/beer-cap-dribble.png");
        urlList.add("http://192.168.1.103:8080/WebService/file/1.png");
        urlList.add("http://192.168.1.103:8080/WebService/file/2.png");
        urlList.add("http://192.168.1.103:8080/WebService/file/3.png");
        urlList.add("http://192.168.1.103:8080/WebService/file/4.png");
        urlList.add("http://192.168.1.103:8080/WebService/file/5.png");
        urlList.add("http://192.168.1.103:8080/WebService/file/6.png");
        urlList.add("http://192.168.1.103:8080/WebService/file/7.png");
        return urlList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebService.cancel(GetBitmapDataActivity.this);
    }
}
