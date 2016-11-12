# RequestManager
RequestManager是支持网络请求、数据库请求和IO请求的通用库

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
