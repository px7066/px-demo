package com.github.px.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>zip压缩</p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/4/29 9:50
 * @since 1.0
 */
@Slf4j
public class ZipFilesUtils {
    public static void zip(List<String> paths, String zipName, String directory) {
        if (StringUtils.isEmpty(directory)) {
            throw new IllegalArgumentException("directory:" + directory);
        }
        if (CollectionUtils.isEmpty(paths)) {
            return;
        }
        createDirectory(directory);
        zipName = StringUtils.isEmpty(zipName) ? NameGeneratorUtil.generateByTime(null) : zipName;
        FileOutputStream fileOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        FileInputStream inputStream = null;
        File zipFile = new File(directory + File.separator + zipName);
        try {
            byte[] buff = new byte[1024];
            fileOutputStream = new FileOutputStream(zipFile);
            zipOutputStream = new ZipOutputStream(fileOutputStream);
            for (String path : paths) {
                File file = new File(path);
                if (file.exists()) {
                    zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                    inputStream = new FileInputStream(file);
                    int len;
                    while ((len = inputStream.read(buff)) > 0) {
                        zipOutputStream.write(buff, 0, len);
                    }
                    zipOutputStream.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (zipOutputStream != null) {
                    zipOutputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createDirectory(String directory) {
        File directoryFile = new File(directory);
        directoryFile.mkdirs();
    }

    /**
     * 压缩文件，至指定位置
     *
     * @param inputMap  待压缩的文件，key为文件路径，value为在压缩包中的位置
     * @param zipName         压缩后的名字
     * @param outputDirectory 压缩文件存放目录
     * @return 压缩文件路径
     */
    public static String compressFiles(Map<String, String> inputMap, String zipName, String outputDirectory) {

        if (StringUtils.isEmpty(outputDirectory)) {
            throw new IllegalArgumentException("directory:" + outputDirectory);
        }
        if (ObjectUtils.isEmpty(inputMap)) {
            return null;
        }

        createDirectory(outputDirectory);
        zipName = StringUtils.isEmpty(zipName) ? NameGeneratorUtil.generateByTime(null) : zipName;
        FileOutputStream fileOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        FileInputStream inputStream = null;
        File zipFile = new File(outputDirectory + File.separator + zipName);

        try {

            fileOutputStream = new FileOutputStream(zipFile);
            zipOutputStream = new ZipOutputStream(fileOutputStream);

            byte[] buffer = new byte[1024];
            for (Map.Entry<String, String> entry : inputMap.entrySet()) {
                String filePath = entry.getKey();
                String zipEntryPath = entry.getValue();
                File file = new File(filePath);
                zipOutputStream.putNextEntry(new ZipEntry(zipEntryPath));
                inputStream = new FileInputStream(file);
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, len);
                }
                zipOutputStream.closeEntry();
                inputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (zipOutputStream != null) {
                    zipOutputStream.close();
                }
            }catch (IOException e){
            }
        }

        return zipFile.getAbsolutePath();

    }


}
