package parseLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class TfidfUtils {
	//输入【identifier：【component：cnt】】的文档，输出各文档的idf值
	public static HashMap<String, Double> getidf(HashMap<String, HashMap<String, Integer>> docs,String type) throws Exception, IOException {
		String tfidfpath ="data/tfidf_"+type; 
		if (new File(tfidfpath).exists()) {
			//反序列化模型数据
	        ObjectInputStream in = new ObjectInputStream(new FileInputStream(tfidfpath));
	        HashMap<String, Double> tmp = (HashMap<String, Double>) in.readObject();
	        in.close();
	        return tmp;	
		}
		HashMap<String, Integer> statmap = new HashMap<String, Integer>();
		for(String docid: docs.keySet()){
			Set<String> wordtmpset = docs.get(docid).keySet();
			for (String word : wordtmpset) {
				if (statmap.containsKey(word)) {
					statmap.put(word, statmap.get(word)+1);
				}else {
					statmap.put(word, 1);
				}
			}
		}
		int docsum = docs.size();
		HashMap<String, Double> idfmap = new HashMap<String, Double>();
		for(String word: statmap.keySet()){
			double idf = Math.log((double)docsum/(1+statmap.get(word)));
			idfmap.put(word, idf);
		}
		//序列化模型存储
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tfidfpath));
		out.writeObject(idfmap);    //写入customer对象
		out.close();
		return idfmap;
	}
}
