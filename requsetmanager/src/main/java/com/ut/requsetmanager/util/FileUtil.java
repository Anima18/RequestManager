package com.ut.requsetmanager.util;

import java.io.File;

/**
 * Created by jianjianhong on 2017/1/3.
 */

public class FileUtil {

    public static boolean createDir(String destDirName) {
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

    public static void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    delete(f);
                }
            }
        }
        file.delete();
    }
}
