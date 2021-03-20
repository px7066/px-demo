package com.github.px.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class SerializeUtil {
    /**
     * 序列化转为 byte[]
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * 反序列化
     * @param bytes 字节
     * @param tClass 目标class
     * @param <T> 目标类型
     * @return 目标
     */
    public static <T>T deserialize(byte[] bytes, Class<T> tClass) {
        if(bytes == null || bytes.length == 0){
            return null;
        }
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return tClass.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (bais != null) {
                    bais.close();
                }
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }
}