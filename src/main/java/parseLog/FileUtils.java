package parseLog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class FileUtils {
	//获取identifier名字列表
	public static ArrayList<String> getEventList(String type) throws Exception {
		ArrayList<String> res = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader("data/eventlist_"+type));
		String line = null;
		while((line=br.readLine())!=null){
			res.add(line.trim());
		}
		return res;
	}
}
