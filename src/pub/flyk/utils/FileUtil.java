package pub.flyk.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class FileUtil {
	
	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	public static String readFile(File file) {
		if (file == null || !file.exists() || file.isDirectory()) {
			logger.warning("不能读取文件!");
			return null;
		}
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			logger.warning(file.getName() + "读取失败!");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
		return sb.toString();
	}
	public static boolean saveFile(String data,File file,boolean isAppend) {
		if (file == null || !file.exists() || file.isDirectory()) {
			logger.warning("文件不存在或者不是一个文件,无法保存!");
			return false;
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, isAppend);
			fw.write(data);
			fw.flush();
			return true;
		} catch (IOException e) {
			logger.warning(file.getName() + "保存数据(" + data + ")失败!");
		} finally{
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
		}
		return false;
	}
	public static boolean saveFile(String data,File file) {
		return saveFile(data, file,false);
	}

}
