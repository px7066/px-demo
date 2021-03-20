package com.github.px.common.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * <p>CSV文件导入</p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/5/8 9:13
 * @since 1.0
 */
public class CsvImportUtil {

    public static List<Map<String, String>> read(String path){
        List<Map<String, String>> list = new ArrayList<>();
        File file = new File(path);
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            String temp;
            boolean isTitle = true;
            boolean merge = false;
            int length = 0;
            Stack<String> stack = new Stack<>();
            String[] title = new String[0];
            while ((temp = randomAccessFile.readLine()) != null){
                temp = new String(temp.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                String[] strs = temp.split("\"");
                if(isTitle){
                    title = strs;
                    length = strs.length;
                    isTitle = false;
                }else {
                    if(strs.length < length){
                        if(merge){
                            String temp2 = stack.pop();
                            temp = temp2+  "\n" + temp;
                            stack.push(temp);
                            merge = false;
                        }else {
                            merge = true;
                            stack.push(temp);
                        }
                    }else {
                        stack.push(temp);
                    }
                }
            }
            for (String s : stack) {
                String[] strs = s.split("\"");
                Map<String, String> map = new HashMap<>();
                for(int i=0; i< strs.length; i++){
                    if(i % 2 == 1){
                        map.put(title[i], strs[i]);
                    }
                }
                list.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        for (Map<String, String> map : CsvImportUtil.read("C:\\Users\\70664\\Desktop\\hospital.csv")) {
            System.out.println(map.get("hospital_name"));
        }
    }
}
