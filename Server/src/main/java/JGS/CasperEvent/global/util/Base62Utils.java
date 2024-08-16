package JGS.CasperEvent.global.util;

public class Base62Utils {
    private static final String BASE62_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String encode(long number) {
        if (number == 0) return Character.toString(BASE62_CHARS.charAt(0));

        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int reminder = (int) (number % 62);
            sb.append(BASE62_CHARS.charAt(reminder));
            number /= 62;
        }
        return sb.reverse().toString();
    }

    public static long decode(String str){
        long result = 0;
        for (int i = 0; i < str.length(); i++) {
            result = result * 62 + BASE62_CHARS.indexOf(str.charAt(i));
        }
        return result;
    }

    public static String encode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            int value = b & 0xFF;
            sb.append(encode(value));
        }
        return sb.toString();
    }

    public static byte[] decodeToBytes(String str) {
        int length = str.length() / 2;
        byte[] data = new byte[length];
        for (int i = 0; i < length; i++) {
            String part = str.substring(i * 2, i * 2 + 2);
            data[i] = (byte) decode(part);
        }
        return data;
    }
}
