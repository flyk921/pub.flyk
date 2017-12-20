package pub.flyk.utils;

import java.nio.charset.Charset;

public final class StringUtil {
	
	/**
	 * Used to build output as Hex
	 */
	private static final char[] DIGITS_LOWER =
		{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	/**
	 * Used to build output as Hex
	 */
	private static final char[] DIGITS_UPPER =
		{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	
	private StringUtil(){}

	public static String newStringUtf8(final byte[] bytes) {
		return newString(bytes, Charset.forName("UTF-8"));
	}
	private static String newString(final byte[] bytes, final Charset charset) {
        return bytes == null ? null : new String(bytes, charset);
    }
	public static byte[] getBytesUtf8(final String string) {
        return getBytes(string, Charset.forName("UTF-8"));
    }
	private static byte[] getBytes(final String string, final Charset charset) {
		if (string == null) {
			return null;
		}
		return string.getBytes(charset);
	}
	
	public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
	public static boolean isNotEmpty(final CharSequence cs) {
		return !isEmpty(cs);
	}
	
	public static boolean isBlank(final CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}
	public static boolean isNotBlank(final CharSequence cs) {
		return !isBlank(cs);
	}
	
	public static String toHexString(final byte[] data,final boolean toLowerCase){
		return new String(encodeHex(data, toLowerCase));
	}
	
	public static String toHexString(final byte[] data){
		return toHexString(data, false);
	}
	private static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}
	private static char[] encodeHex(final byte[] data, final char[] toDigits) {
		final int l = data.length;
		final char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}
		return out;
	}
}
