package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

public class StoreUtils {
	public static void putbadarr(String type, String modelname, String[] arr)
			throws Exception {
		HashSet<Integer> badset = new HashSet<Integer>();
		for (String onebad : arr) {
			badset.add(Integer.valueOf(onebad));
		}
		String putpath = "data/badset_" + modelname + "_" + type;
		if (new File(putpath).exists()) {
			new File(putpath).delete();
		}
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
				putpath));
		out.writeObject(badset); // 写入customer对象
		out.close();
	}

	public static HashSet<Integer> getbadarr(String type, String modelname) throws Exception {
		// 反序列化模型数据
		String path = "data/badset_" + modelname + "_" + type;
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				path));
		HashSet<Integer> badarr = (HashSet<Integer>) in.readObject(); // 读取customer对象
		in.close();
		return badarr;
	}
}
