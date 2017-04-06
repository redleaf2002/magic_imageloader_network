package com.leaf.magic.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/3/17.
 */

public class NetworkUitls {

    public static String instream2String(InputStream in) {
        BufferedReader bufferReader = null;
        bufferReader = new BufferedReader(new InputStreamReader(in));
        String inputLine = "";
        StringBuilder resultData = new StringBuilder();
        try {
            while ((inputLine = bufferReader.readLine()) != null) {
                resultData.append(inputLine);
            }
        } catch (IOException var5) {
            return null;
        }

        return resultData.toString();
    }

}
