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
1. getObject, 获取单个对象请求
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
2. getCollection 获取对象集合对象
```

```
