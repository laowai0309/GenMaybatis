package com.bbx.mybatisUtils;

import java.io.File;

/**
 * 问卷操作工具类
 */
public class FileUtils {
    static public void mkdir(String path) {
        File file = new File(path);
        if(!file.exists()){
            file.mkdir();
        }
    }
}
