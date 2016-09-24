package com.example.chirs.rxsimpledemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chirs.rxsimpledemo.entity.ActivityClass;
import com.example.webserviceutil.OkHttp.OkHttpUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class MainActivity extends BaseActivity {

    private List<ActivityClass> activityClassList;
    private List<String> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        urlList = initData();
    }

    private void initView() {
        ListView dataLv = (ListView) findViewById(R.id.data_lv);
        if(dataLv == null) {
            return;
        }

        ListDataAdapter adapter = new ListDataAdapter(this, getActivityClassList());
        dataLv.setAdapter(adapter);
        dataLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityClass ac = activityClassList.get(position);
                if(ac != null && ac.getActivityClass() != null) {
                    Intent intent = new Intent(MainActivity.this, ac.getActivityClass());
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this, "没有实现", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<ActivityClass> getActivityClassList() {
        activityClassList = new ArrayList<>();

        activityClassList.add(new ActivityClass("get方式获取单个对象", GetObjectDataActivity.class));
        activityClassList.add(new ActivityClass("get方式获取对象对象集合", GetCollectionDataActivity.class));
        activityClassList.add(new ActivityClass("获取图片资源", GetBitmapDataActivity.class));
        activityClassList.add(new ActivityClass("post方式获取单个对象", PostObjectDataActivity.class));
        activityClassList.add(new ActivityClass("提交多个文件", PostCollectionDataActivity.class));

        return activityClassList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_getbitmap, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.okAct_download:
                download();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void download() {
        showProgress("正在下载中");
        final String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RxJava/";
        createDir(basePath);
        for(final String url : urlList) {
            new AsyncTask<String, Void, String>()  {
                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if(urlList.indexOf(s) == (urlList.size() -1)) {
                        hideProgress();
                        Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                protected String doInBackground(String... params) {
                    String url = params[0];
                    String filename = url.substring(url.lastIndexOf("/"));
                    Call call;
                    try {
                        call = OkHttpUtils.get(MainActivity.this, url);
                        Response response = call.execute();
                        File downloadedFile = new File(basePath, filename);
                        BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
                        sink.writeAll(response.body().source());
                        sink.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return url;
                }
            }.execute(url);

        }
    }

    private static boolean createDir(String destDirName) {
        boolean flag = false;
        File dir = new File(destDirName);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                flag = true;
            }
        }else {
            flag = true;
        }
        return flag;
    }

    private List<String> initData() {
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
        urlList.add("http://192.168.1.103:8080/WebService/file/Kalimba.mp3");
        urlList.add("http://192.168.1.103:8080/WebService/file/OkHttp发布.rar");
        urlList.add("http://192.168.1.103:8080/WebService/file/app-debug.apk");
        return urlList;
    }
}
