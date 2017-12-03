package com.ut.requsetmanager.entity;

/**
 * 文件参数类，请求参数中可以包含文件，这个文件类代表一个文件
 * @author 简建鸿 2016/6/7
 * @version 1.0
 */
public class FileObject {
    private String filePath;

    public FileObject(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
