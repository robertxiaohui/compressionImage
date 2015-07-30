package com.jerry.compressionimage;

import android.text.TextUtils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by zjm on 2014/11/17.
 */
public class Util {
    public static String fileMD5(String inputFile){
        // 缓冲区大小（这个可以抽出一个参数）
        int bufferSize = 256 * 1024;
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;
        try {
            // 拿到一个MD5转换器（同样，这里可以换成SHA1）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 使用DigestInputStream
            fileInputStream = new FileInputStream(inputFile);
            digestInputStream = new DigestInputStream(fileInputStream, messageDigest);
            // read的过程中进行MD5处理，直到读完文件
            byte[] buffer = new byte[bufferSize];
            while (digestInputStream.read(buffer) > 0) ;
            // 获取最终的MessageDigest
            messageDigest = digestInputStream.getMessageDigest();
            // 拿到结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            return bytesToHex(resultByteArray);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                digestInputStream.close();
            } catch (Exception e) {
            }
            try {
                fileInputStream.close();
            } catch (Exception e) {
            }
        }
    }

    public static String MD5(String str){
        try {
            // 拿到一个MD5转换器（同样，这里可以换成SHA1）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes());
            byte[] digest = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            return bytesToHex(digest);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    final protected static char[] hexArray = "0123456789abcdef".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        if (TextUtils.isEmpty(s)) {
            return null;
        }
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static byte[] gzip(byte[] in) {
        if (in == null || in.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzip = new GZIPOutputStream(out) {
                {
                    this.def.setLevel(Deflater.BEST_COMPRESSION);
                }
            };
            gzip.write(in);
            gzip.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return out.toByteArray();
    }

    public static byte[] ungzip(byte[] in,int offset, int length) {
        ByteArrayInputStream bais = new ByteArrayInputStream(in,offset,length);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            GZIPInputStream gis = new GZIPInputStream(bais);
            int count;
            int BUFFER = 4096;
            byte[] data = new byte[BUFFER];
            while ((count = gis.read(data, 0, BUFFER)) != -1) {
                baos.write(data, 0, count);
            }
            byte[] retData = baos.toByteArray();
            baos.close();
            bais.close();
            gis.close();
            return retData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

   /* public static int getOption(List<OptionDialogBuilder.Option> options,String val) {
        for (int i=0;i<options.size();i++) {
            if (options.get(i).value.equals(val)) {
                return i;
            }
         }
        return -1;
    }*/

    static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    /**
     * 判断是不是一个合法的电子邮件地址
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        if(TextUtils.isEmpty(email)) return false;
        email = email.toLowerCase();
        if(email.endsWith(".con")) return false;
        if(email.endsWith(".cm")) return false;
        if(email.endsWith("@gmial.com")) return false;
        if(email.endsWith("@gamil.com")) return false;
        if(email.endsWith("@gmai.com")) return false;
        return emailer.matcher(email).matches();
    }

    public static boolean isNum11(String s) {
        if (TextUtils.isEmpty(s)) {
            return false;
        }
        if (TextUtils.isDigitsOnly(s) && s.length() == 11) {
            return true;
        }
        return false;
    }



}
