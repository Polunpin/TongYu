package com.TongYu.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileHandle {

    /**
     * 将文件转换为字节数组
     * @param file 传入的文件
     * @return 字节数组
     */
    public static byte[] fileToByteArray(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] byteArray = new byte[(int) file.length()];
        fis.read(byteArray);
        fis.close();
        return byteArray;
    }

    /**
     * 删除文件
     * @param file 传入的文件
     * @return 是否删除成功
     */
    public static boolean deleteFile(File file) {
        if (file != null && file.exists()) {
            return file.delete();
        }
        return false;
    }

}
