package com.example.kingz.parsejson.util;

import java.io.InputStream;
import java.io.Reader;

/**
 * Created by kingz on 9/12/2016.
 */
public class IOUtils {
    public static void closeQuietly(InputStream in)  {
        try {
            in.close();
        }catch (Exception e) {

        }
    }

    public static void closeQuietly(Reader reader)  {
        try {
            reader.close();
        }catch (Exception e) {

        }
    }
}
