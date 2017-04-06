package com.leaf.magic.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/3/20.
 */

public class IoUtils {

    public static void readAndCloseStream(InputStream is) {
        byte[] bytes = new byte[1024];

        try {
            while (is.read(bytes, 0, 1024) != -1) {
                ;
            }
        } catch (IOException var6) {
            ;
        } finally {
            closeSilently(is);
        }
    }

    public static void closeSilently(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception var2) {
            ;
        }
    }

}
