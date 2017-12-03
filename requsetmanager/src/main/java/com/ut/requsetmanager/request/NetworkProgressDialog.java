package com.ut.requsetmanager.request;

/**
 * Created by jianjianhong on 2017/11/21.
 */

public interface NetworkProgressDialog {

    void showProgress(String message);

    void hideProgress();

    void updateProgress(String message, int progress);
}
