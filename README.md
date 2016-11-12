# RequestManager
RequestManager是网络请求通用库。网络请求通常的问题是如何有效的在后台线程中工作，然后如何方便在UI线程中更新UI。  
AsybcTask是Android提供处理耗时任务而不会阻塞UI线程的工具：  
```
new AsyncTask<ActivityClass, Void, List<Void>>() {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected List<ActivityClass> doInBackground(Void... params) {
    }
    @Override
    protected void onPostExecute(List<ActivityClass> activityClasses) {
        super.onPostExecute(activityClasses);
    }
}.execute();

```
这段代码很简单，但随着业务逻辑的复杂，会遇到一些问题：  

1. 错误处理  
    如果在doInBackground方法中发生了异常，也只能在异常代码加上try/catch进行捕获，然后在onPostExecute方法处理。上面的做法是无法识别出那种exception，除非对返回的数据加上异常封装。
2. 生命周期  
    当数据返回给client端时，如果页面已经不在了，那么就无法去绘制UI，很有可能会导致意向不到的问题。因此，为了解决这个问题，一个好的思路就是当页面离开时，自动断开网络请求数据的处理过程，即数据返回后不再进行任何处理。
3. 组合请求  
    需要做一些连续的网络请求，每一个请求都需要基于前一个请求的结果。或者是，我们想做一些并发的网络请求，然后把结果合并在一起发送到UI线程，但是， AsyncTask没法做到。  
4. 可阅读性  
    就组合多个WebService来，使用AsyncTask会造成大量代码，会把业务逻辑掩埋在里面。想看清楚业务逻辑不是很轻松的事情。

##网络请求
- Get请求
- Post请求
- 图片请求
- 上传文件
- 下载文件
- 顺序请求
- 嵌套请求
- 并发请求

## simple
- Get请求  
```
NetworkRequest.Builder(this)
.url(url)
.dataType(new TypeToken<Bean>(){}.getType())
.method(NetworkRequest.GET_TYPE)
.call(new DataCallBack<Bean>() {
    @Override
    public void onSuccess(Bean data) {}

    @Override
    public void onFailure(int code, String message) {}

    @Override
    public void onCompleted() {}
});
```
- Post请求  
```
NetworkRequest.Builder(this)
.url(url)
.dataType(new TypeToken<Bean>(){}.getType())
.method(NetworkRequest.POST_TYPE)
.call(new DataCallBack<Bean>() {
    @Override
    public void onSuccess(Bean data) {}

    @Override
    public void onFailure(int code, String message) {}

    @Override
    public void onCompleted() {}
});
```
- 图片请求  
```
NetworkRequest.builder(this).url(url).getBitMap(new BitmapCallBack(){
    @Override
    public void onSuccess(String url, Bitmap bitmap) {}

    @Override
    public void onFailure(int code, String message) {}

    @Override
    public void onCompleted() {}
});
```
- 上传文件  
```
NetworkRequest.builder(this)
.trl(url)
.param(param)
.method(NetworkRequest.POST_TYPE)
.dataType(new TypeToken<DataObject<User>>(){}.getType())
.uploadFile(new ProgressCallBack<DataObject<User>>() {
    @Override
    public void onProgress(String fileName, int progress) {}

    @Override
    public void onSuccess(DataObject<User> data) {}

    @Override
    public void onFailure(int code, String message) {}

    @Override
    public void onCompleted() {}
});
```
- 下载文件  
```
NetworkRequest.builder(this).url(BASE_PATH + "file/LuaDemo.rar")
.dataClass(Boolean.class)
.param("fileName", "LuaDemo.rar")
.method(NetworkRequest.POST_TYPE)
.downloadFile(new ProgressCallBack<Boolean>() {
  @Override
  public void onProgress(String fileName, int progress) {}

  @Override
  public void onSuccess(Boolean data) {}

  @Override
  public void onFailure(int code, String message) {}

  @Override
  public void onCompleted() {}
});
```
- 顺序请求
```
NetworkRequest.builder(this)
.params(params)
.getSeqData(new DataCallBack<List<Object>>() {
  @Override
  public void onSuccess(List<Object> dataList) {}

  @Override
  public void onFailure(int code, String message) {}

  @Override
  public void onCompleted() {}
});
```
- 嵌套请求
```
NetworkRequest.builder(this)
.url(url)
.method(NetworkRequest.GET_TYPE)
.dataType(new TypeToken<DataObject<User>>() {}.getType())
.request()
.flatMap(new Func1<DataObject<User>, Observable<?>>() {
    @Override
    public Observable<?> call(DataObject<User> o) {
        Log.i("WebService", "嵌套请求一成功");
        return NetworkRequest.builder(this)
                .url(url)
                .method(NetworkRequest.GET_TYPE)
                .dataType(new TypeToken<DataObject<User>>() {}.getType())
                .request();
    }
})
.flatMap(new Func1<DataObject<User>, Observable<?>>() {
    @Override
    public Observable<?> call(DataObject<User> o) {
        Log.i("WebService", "嵌套请求二成功");
        return NetworkRequest.builder(this)
                .url(url)
                .method(NetworkRequest.GET_TYPE)
                .dataType(new TypeToken<DataObject<User>>() {}.getType())
                .request();
    }
})
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
    }

    @Override
    public void onNext(DataObject<User> o) {
        Log.i("WebService", "最后请求成功");
    }
});
```
- 并发请求
```
Observable<DataObject<User>> observable1 = NetworkRequest.builder(this).url(url)
        .method(NetworkRequest.GET_TYPE)
        .dataType(new TypeToken<DataObject<User>>(){}.getType())
        .request();

Observable<DataObject<User>> observable2 = NetworkRequest.builder(this).url(url)
        .method(NetworkRequest.GET_TYPE)
        .dataType(new TypeToken<DataObject<User>>(){}.getType())
        .request();

Observable<DataObject<User>> observable3 = NetworkRequest.builder(this).url(url)
      .method(NetworkRequest.GET_TYPE)
        .dataType(new TypeToken<DataObject<User>>(){}.getType())
      .request();
Observable.zip(observable1, observable2, observable3, new Func3<DataObject<User>, DataObject<User>, DataObject<User>, DataObject<User>>() {
      @Override
      public DataObject<User> call(DataObject<User> userDataObject, DataObject<User> userDataObject2, DataObject<User> userDataObject3) {
          return userDataObject;
      }
  }).subscribeOn(Schedulers.io())
   .observeOn(AndroidSchedulers.mainThread())
  .subscribe(new Subscriber<DataObject<User>>() {
      @Override
      public void onCompleted() {}

      @Override
      public void onError(Throwable e) {
      }

      @Override
      public void onNext(DataObject<User> userDataObject) {
      }
  });
```
