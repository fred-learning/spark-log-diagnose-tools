package parseLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parseSourceCode.GetLogIndex;

public class MatchLogSchema {
	private static HashMap<String, HashMap<String, Integer>> clustermap;//identifier：component：cnt
	private static HashMap<String,ArrayList<String>> logschemamap;//日志字典 component:[reglog]
	private static HashSet<String> typeSet;//存都有哪些component
	private static boolean start;//tid特征中判断某任务的开始
	private static String nowpc;//记录当前的pc name
	private static String nowtidname;//记录当前的tid name，即*task in *stage
	private static String nowtidindex;//记录当前tid index 即tid*
	private static int nowfileid;//记录当前处理第几个文件
	private static HashMap<String, Integer> tidMap;//记录当前tid中的fea:cnt
	private static HashSet<String> nowtypeSet;//记录当前tid中的type
	private static boolean checkarffhead;//记录是否要写arff文件的头
	
	//初始化或者释放空间
	public static void init(){
		logschemamap = new HashMap<String, ArrayList<String>>();
		typeSet = new HashSet<String>();
		start = false;
		nowpc="";
		nowtidname="";
		nowtidindex="";
		nowfileid = 0;
		tidMap = new HashMap<String, Integer>();
		nowtypeSet = new HashSet<String>();
		checkarffhead = false;
		clustermap = new HashMap<String, HashMap<String,Integer>>();
	}
	//根据tid来聚合提取特征
	private static void clusterbyTID(String complexlog,String simplelog) {
		String stpattern = "Running (.* )\\((TID .*)\\).*";
		String endpattern = "Finished (.* \\(TID .*\\))\\. .* bytes result sent to driver.*";
		Pattern pt = Pattern.compile(stpattern);
		Matcher mt = pt.matcher(complexlog);
		if (mt.find()) {
			start = true;
			tidMap.clear();
			nowtypeSet.clear();
			nowtidname = mt.group(1);
			nowtidindex = mt.group(2);
		}else {
			pt = Pattern.compile(endpattern);
			mt = pt.matcher(complexlog);
			if (mt.find()) {
				String key = String.valueOf(nowtidindex+" "+nowtidname+nowpc+" "+"file"+nowfileid);
				//对于tid，如果其涉及的事件太少，则忽略
				int eventthreshold = 2;
				if (tidMap.size()>eventthreshold) {
					clustermap.put(key, new HashMap<String,Integer>(tidMap));
					for(String eachcomponent:nowtypeSet){
						typeSet.add(eachcomponent);
					}
				}
//				tidMap.clear();
//				nowtidname="";
//				nowtidindex="";
				start = false;
				return;
			}
		}
		if (start) {
//			typeSet.add(simplelog);
			nowtypeSet.add(simplelog);
			if (tidMap.containsKey(simplelog)) {
				tidMap.put(simplelog, tidMap.get(simplelog)+1);
			}else {
				tidMap.put(simplelog, 1);
			}
		}
	}
	//根据blockid来聚合提取特征
	private static void clusterbyblockid(String complexlog,String simplelog) {
		Pattern[] ptarr = new Pattern[1];
		ptarr[0] = Pattern.compile(".*(rdd_\\d+_\\d+).*");
//		ptarr[1] = Pattern.compile(".*(task \\d\\.\\d).*");
//		ptarr[2] = Pattern.compile(".*(stage \\d\\.\\d).*");
//		ptarr[3] = Pattern.compile(".*\\((TID \\d+)\\).*");
		for (Pattern pt : ptarr) {
			Matcher mt = pt.matcher(complexlog);
			if (mt.find()) {
				typeSet.add(simplelog);
				String blockid = "file"+nowfileid+"_"+mt.group(1);
				if (clustermap.containsKey(blockid)) {
					HashMap<String, Integer> tmpmap = clustermap.get(blockid);
					if (tmpmap.containsKey(simplelog)) {
						int cnt = tmpmap.get(simplelog);
						tmpmap.put(simplelog, ++cnt);
					}else {
						tmpmap.put(simplelog, 1);
					}
				}else {
					HashMap<String, Integer> tmpmap = new HashMap<String, Integer>();
					tmpmap.put(simplelog, 1);
					clustermap.put(blockid,tmpmap);
				}
			}
		}
	}
	//提取每行的特征
	private static boolean linematch(String type,String context,String regpath, String dictpath) {
		context = context.trim();
		if (context.equals(""))
			return true;
		if (context.startsWith("Container: container_")) {
			String[] tmparr = context.trim().split(" ");
//			nowpc = tmparr[tmparr.length-1].split("_")[0];
			nowpc = tmparr[tmparr.length-1];
		}
		String regex = "^\\d{2}/\\d{2}/\\d{2}";
		Pattern pt = Pattern.compile(regex);
		Matcher matcher = pt.matcher(context);
		if (matcher.find()) {
			try {
				String[] part = context.split(" ");
				String timestamp = context.substring(0, 17);
				String logtype = part[2];
				String component = part[3].substring(0, part[3].length() - 1);
				int reallogstart = part[0].length()+part[1].length()+part[2].length()+part[3].length()+4;
				if (reallogstart>context.length()) {
					return true;
				}
				String logString = context.substring(reallogstart, context.length());
				if (logschemamap.containsKey(component)) {
					ArrayList<String> tmpset = logschemamap.get(component);
					boolean flag = false;
					int count=0;
					for (String eachregex : tmpset) {
						count++;
						pt = Pattern.compile(eachregex.split("@@")[0]);
						matcher = pt.matcher(logString);
						if (matcher.find()) {
							String simplelog = component+count;
//							System.out.println(simplelog);
							if (type == "blockid")
								clusterbyblockid(logString, simplelog);//根据blockid聚类
                            if (type == "tid")
								clusterbyTID(logString, simplelog);//根据tid聚类
							flag = true;
							break;
						}
					}
					if (!flag) {
//						System.out.println("not match log---"+context);
//						for(String tmpcomp:logschemamap.keySet()){
//							ArrayList<String> tmpset1 = logschemamap.get(tmpcomp);
//							for (String eachregex : tmpset1) {
//								pt = Pattern.compile(eachregex.split("@@")[0]);
//								matcher = pt.matcher(logString);
//								if (matcher.find()) {
//									System.out.println("add it to the dict!");
//									FileWriter fw = new FileWriter(regpath,true);
//									String reg2 = eachregex.split("@@")[0].replaceAll("\\.\\*", "\\*").replaceAll("\\\\\\(", "(").replaceAll("\\\\\\)", ")");
//									fw.write(eachregex.split("@@")[1]+"@@"+component+"@@"+reg2+"\n");
//									fw.close();
//									logschemamap = GetLogIndex.getIndex(regpath, dictpath);
//									return false;
//								}
//							}
//						}
//						return true;
					}
					return true;
				}else {
//					System.out.println("not find key---"+context);
//					for(String tmpcomp:logschemamap.keySet()){
//						ArrayList<String> tmpset = logschemamap.get(tmpcomp);
//						for (String eachregex : tmpset) {
//							pt = Pattern.compile(eachregex.split("@@")[0]);
//							matcher = pt.matcher(logString);
//							if (matcher.find()) {
//								System.out.println("add it to the dict!");
//								FileWriter fw = new FileWriter(regpath,true);
//								String reg2 = eachregex.split("@@")[0].replaceAll("\\.\\*", "\\*");
//								fw.write(eachregex.split("@@")[1]+"@@"+component+"@@"+reg2+"\n");
//								fw.close();
////								System.out.println(logschemamap.size());
//								logschemamap = GetLogIndex.getIndex(regpath, dictpath);
////								System.out.println(logschemamap.size());
//								return false;
//							}
//						}
//					}
//					return true;
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(context);
				e.printStackTrace();
			}
		}
		return true;
	}
	
	//提取每个文件的特征
	private static void matchEachFile(String type,String filepath,String regpath, String dictpath) {
		try {
			nowfileid++;
			FileReader fr = new FileReader(filepath);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				int i = 0;
				while(i<2&&!linematch(type,line,regpath,dictpath)){
					i++;
				};
			}
			br.close();
			fr.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	//入口，根据正则模板和字典，对日志目录进行特征提取，输出特征
	public static void matchSchema(String type,Boolean checktfidf,String regpath, String dictpath, String logpath,String outputpath) throws Exception {
		if (new File(outputpath+type).exists()) {
			new File(outputpath+type).delete();
		}
		logschemamap = GetLogIndex.getIndex();
		if (logschemamap.isEmpty()) {
			System.out.println("error:fail to get log schema map!");
			return;
		}
		
		File file = new File(logpath);
		if (file.exists()) {
			LinkedList<File> list = new LinkedList<File>();
			list.add(file);
			while (!list.isEmpty()) {
				File temp_file = list.removeFirst();
				File[] files = temp_file.listFiles();
				for (File file2 : files) {
					if (file2.isDirectory()) {
						list.add(file2);
					} else {
						System.out.println("handle one file");
						matchEachFile(type,file2.getAbsolutePath(),regpath,dictpath);
					}
				}
			}
		} else {
			System.out.println("日志文件不存在!");
		}
		//保存或者读取typeset,将其转为array，保证顺序
		String[] typesetarray = new String[typeSet.size()]; 
		typeSet.toArray(typesetarray);
		String typesetpath ="data/typeset_"+type;
		if (!new File(typesetpath).exists()) {
			//序列化模型存储
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(typesetpath));
			out.writeObject(typesetarray);    //写入customer对象
			out.close();	
		}else {
			//反序列化模型数据
	        ObjectInputStream in = new ObjectInputStream(new FileInputStream(typesetpath));
	        typesetarray = (String[]) in.readObject();    //读取customer对象
	        in.close();
		}
		
		//计算idf
		HashMap<String, Double> idfMap = TfidfUtils.getidf(clustermap,type);
//		for(String tmpeachcomp:idfMap.keySet()){
//			System.out.println(tmpeachcomp+"--"+idfMap.get(tmpeachcomp));
//		}
		//生成特征数据
		FileWriter fw = new FileWriter(outputpath+type);
		if (!checkarffhead) {
			fw.write("@RELATION sparkDiagnoseSet\n");
			for(String word:typesetarray){
				fw.write("@ATTRIBUTE "+word+" numeric\n");
			}
			fw.write("@DATA\n");
			checkarffhead = true;
		}
		//保存原始特征
		FileWriter fw_ori = new FileWriter("d:/feature_"+type+".xls");
		String title="schemaid\t";
		for(String simlog:typesetarray){
			title+=simlog+"\t";
		}
		fw_ori.write(title+"\n");
		//保存event list
		FileWriter fw_event = new FileWriter("data/eventlist_"+type);
		
		for (String eachid : clustermap.keySet()) {
			fw_event.write(eachid+"\n");
			HashMap<String, Integer> tmpmap = clustermap.get(eachid);
			int wordscnt = 0;
			for(String word:tmpmap.keySet()){
				wordscnt+=tmpmap.get(word);
			}
			String fea_tfidf ="";
			String fea_ori = "";
			for(String simlog:typesetarray){
				if (tmpmap.containsKey(simlog)) {
					if (checktfidf) {
						double tfidf = idfMap.get(simlog)*((double)tmpmap.get(simlog)/wordscnt);
						//用idf提高某些组件权重，但不用tf，避免某一组件比例太高，导致其他组件比例太低，spe值无参考
//						double tfidf = idfMap.get(simlog)*tmpmap.get(simlog);
						fea_tfidf+=String.format("%.4f", tfidf)+",";
					}else {
						fea_tfidf+=tmpmap.get(simlog)+",";
					}
					fea_ori+=tmpmap.get(simlog)+"\t";
				}else {
					fea_tfidf+="0,";
					fea_ori+="0\t";
				}
			}
//			System.out.println(eachid+":"+fea_tfidf);
			fw_ori.write(eachid+"\t"+fea_ori+"\n");
			fw.write(fea_tfidf.substring(0,fea_tfidf.length()-1)+"\n");
		}
		fw.close();
		fw_ori.close();
		fw_event.close();
        //结束存储

        init();
	}
}
