package com.TongYu.util;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCompressor {

    public static File compressImage(MultipartFile multipartFile, String extension, double scale, double outputQuality) throws IOException {
        // 将 MultipartFile 转换为临时 File
        File inputFile = convertToTempFile(multipartFile, extension);
        File outputFile = File.createTempFile("compressed_", "." + extension);

        // 使用 Thumbnails 进行压缩
        Thumbnails.of(inputFile).scale(scale)  // 控制缩放比例
                .outputQuality(outputQuality)  // 控制图片质量
                .toFile(outputFile);

        // 删除原始文件
        deleteFile(inputFile);

        return outputFile;
    }

    private static File convertToTempFile(MultipartFile file, String extension) throws IOException {
        File tempFile = File.createTempFile("temp_", "." + extension);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        return tempFile;
    }

    private static boolean deleteFile(File file) {
        return file != null && file.exists() && file.delete();
    }
}
