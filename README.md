# rxjava-okhttp
rxjava-okHttp是封装RxJava和OkHttp的网络组件，目的在于让开发者方便发送网络请求，调用者只需要关注请求参数和处理响应。

- getObject 获取单个对象请求
- getCollection 获取对象集合对象
- getBitMap 获取图片资源
- getObjectInSeq  顺序获取对象请求
- getObjectObservable 嵌套获取对象请求
- uploadFile  文件上传请求
- cancel 取消请求

## simple
- getObject, 获取单个对象请求
```
WebServiceParam param = new WebServiceParam(BASE_PATH + "security/security_get.action?user.name="+name, Service.GET_TYPE, User.class);
WebService.getObject(this, param, new ObjectCallBack<User>() {
    @Override
    public void onSuccess(User data) {
        if(data == null) {
            resultTv.setText("没有数据");
        }else {
            resultTv.setText(data.toString());
        }
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
```

- getCollection 获取对象集合对象
```
WebServiceParam param = new WebServiceParam("http://192.168.1.103:8080/WebService/security/security_list.action", Service.GET_TYPE, User.class);
WebService.getCollection(GetCollectionDataActivity.this, param, new CollectionCallBack<Object>() {
    @Override
    public void onSuccess(List<Object> data) {
        if(data == null || data.size() == 0) {
            resultTv.setText("没有数据");
        }else {
            resultTv.setText(data.toString());
        }
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
```

- getBitMap 获取图片资源
```
WebService.getBitMap(context, url, new BitmapCallBack() {
    @Override
    public void onSuccess(String url, Bitmap bitmap) {
        if(bitmap == null) {
            holder.image.setImageResource(R.drawable.not_found);
        }else {
            holder.image.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onFailure(int code, String message) {
        holder.image.setImageResource(R.drawable.not_found);
    }

    @Override
    public void onCompleted() {

    }
});
```

- getObjectInSeq  顺序获取对象请求
```
requestIndex = 0;
List<WebServiceParam> params = new ArrayList<>();
params.add(new WebServiceParam("http://192.168.60.242:8080/scs/mobile/getdtshowobject.do?username=&pwd=&projectcode=utyingyongbanben&showobjectcode=utyybanben", Service.GET_TYPE, ObjectShowData.class));
params.add(new WebServiceParam("http://192.168.60.242:8080/scs/mobile/getdtobjectdata.do?&username=&pwd=&projectcode=utyingyongbanben&objectcode=utyybanben&pagesize=5&pagenum=1&sort=&condi=",
        Service.GET_TYPE, DataObject.class));
WebService.getObjectInSeq(this, params, new ObjectCallBack<Object>() {
    @Override
    public void onSuccess(Object data) {
        if(requestIndex == 0) {
            Toast.makeText(GetObjectDataListActivity.this, "第一个请求成功", Toast.LENGTH_SHORT).show();
        }else if(requestIndex == 1) {
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
});
}
```

- getObjectObservable 嵌套获取对象请求
```
final WebServiceParam param = new WebServiceParam(BASE_PATH+"security/security_get.action?user.name="+name, Service.GET_TYPE, User.class);
final WebServiceParam param2 = new WebServiceParam(BASE_PATH+"security/security_get.action?user.name="+name, Service.GET_TYPE, User.class);

Subscription subscribe = WebService.getObjectObservable(GetNestedObjectDataActivity.this, param)
    .flatMap(new Func1<Object, Observable<?>>() {
        @Override
        public Observable<?> call(Object o) {
            if(o == null) {
                Log.i("GetObjectDataActivity", "第一个请求：null");
            }else {
                Log.i("GetObjectDataActivity", "第一个请求："+((User)o).toString());
            }

            return WebService.getObjectObservable(GetNestedObjectDataActivity.this, param2);
        }
    })
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(WebService.getObjectSubscriber(param, new ObjectCallBack<Object>() {
        @Override
        public void onSuccess(Object data) {
            if(data == null) {
                resultTv.setText("没有数据");
            }else {
                resultTv.setText(data.toString());
            }
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
    }));
```

- uploadFile  文件上传请求
```
WebServiceParam param = new WebServiceParam(BASE_PATH + "security/security_uploadList.action", Service.POST_TYPE, User.class);
param.addParam("user.name", "Anima18");
param.addParam("user.password", "123456");
for(String fileName : fileNameArray) {
    Log.d(TAG, fileName);
    param.addParam(fileName, new FileObject(basePath + fileName));
}
return WebService.uploadFile(PostCollectionDataActivity.this, param, new ProgressCollectionCallBack<User>() {
    @Override
    public void onProgress(String fileName, int progress) {
        updataProgress(fileName, progress);
    }

    @Override
    public void onSuccess(List<User> data) {
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
```
