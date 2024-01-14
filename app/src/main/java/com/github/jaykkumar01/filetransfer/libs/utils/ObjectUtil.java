package com.github.jaykkumar01.filetransfer.libs.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

public class ObjectUtil {


    public static int count;

    public static String preserveString(String input) {
        return Arrays.toString(input.getBytes(StandardCharsets.UTF_8));
    }
    public static String restoreString(byte[] bytes){
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String preserveBytes(byte[] byteArray) {
        return Arrays.toString(byteArray);
    }

    public static byte[] restoreBytes(byte[] preservedBytes) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            GZIPInputStream gzipStream = new GZIPInputStream(new ByteArrayInputStream(preservedBytes));
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = gzipStream.read(buffer)) != -1) {
                byteStream.write(buffer, 0, bytesRead);
            }
            byte[] b = byteStream.toByteArray();
            return b;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}
