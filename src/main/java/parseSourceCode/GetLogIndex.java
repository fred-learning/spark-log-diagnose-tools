package parseSourceCode;

import utils.Config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class GetLogIndex {
    private static  Config config = Config.getInstance();

    //根据日志模板正则list输出dict
	public static HashMap<String, ArrayList<String>> getIndex() {
        String regpath = config.getTemplateRegPath();
        String dictpath = config.getDictPath();
		HashMap<String, ArrayList<String>> res = new HashMap<String, ArrayList<String>>();
		try {
			InputStream is = new FileInputStream(new File(regpath));
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String context;
			while ((context = reader.readLine()) != null && !context.equals("")) {
				if (context.startsWith("//")) {
					continue;
				}
				context = context.replaceAll("\\*", ".*");
				context = context.replaceAll("\\(", "\\\\(");
				context = context.replaceAll("\\)", "\\\\)");
				context = context.replaceAll("\\+", "\\\\+");
				String[] arr = context.split("@@");
				String key = arr[1];
				String value = arr[2] + "@@" + arr[0];
				if (res.containsKey(key)) {
					ArrayList<String> tmpset = res.get(key);
					tmpset.add(value);
					res.put(key, tmpset);
				} else {
					ArrayList<String> tmpset = new ArrayList<String>();
					tmpset.add(value);
					res.put(key, tmpset);
				}
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(dictpath));
			for (String eachkey : res.keySet()) {
				ArrayList<String> tmpset = res.get(eachkey);
				int index = 0;
				for (String eachorilog : tmpset) {
					index++;
					bw.write(eachkey + index + "@@" + eachorilog);
					bw.newLine();
					bw.flush();
				}
			}
			bw.close();
			reader.close();
			is.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return res;
	}
}
