package pub.flyk.utils;

import java.nio.charset.Charset;

public final class StringUtil {
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
}
