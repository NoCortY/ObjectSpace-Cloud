package cn.objectspace.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {
    private static Base64.Encoder encoder = Base64.getEncoder();
    private static Base64.Decoder decoder = Base64.getDecoder();

    public static String encode(String content) {
        return encoder.encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] encode(byte[] content) {
        return encoder.encode(content);
    }

    public static String decode(String content) {
        return new String(decoder.decode(content), StandardCharsets.UTF_8);
    }

   /* public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(Base64Util.encode("abc".getBytes()));
        System.out.println(Base64Util.decode("YWJj"));
    }*/
}
