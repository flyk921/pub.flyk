package pub.flyk.utils;

public class CommonUtil {
	
	/**
	 * 将对象转换成字符串,如果待转换对象为null则返回""
	 * @param obj 待转换的对象
	 * @return 转换后的字符串
	 */
	public static String null2String(Object obj) {
		return  null2String(obj,"");
	}

	/**
	 * 将对象转换成字符串,如果待转换对象为null则返回目标字符串
	 * @param obj 待转换对象
	 * @param s 目标字符串
	 * @return
	 */
	public static String null2String(Object obj, String s) {
		if (obj == null || "null".equalsIgnoreCase(obj.toString()) || "".equals(obj.toString().trim())) {
			return s;
		}
		return obj.toString();
	}

}
