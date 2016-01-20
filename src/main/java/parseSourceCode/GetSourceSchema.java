package parseSourceCode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetSourceSchema {
	private static HashSet<String> oriset = new HashSet<String>();
	private static HashSet<String> regularset = new HashSet<String>();
	
	//判断日志类型
	private static String getLogType(String type) {
		if (type == "logInfo")
			return "logInfo";
        if (type == "logDebu")
			return "logDebug";
        if (type == "logTrac")
			return "logTrace";
        if (type == "logWarn")
			return "logWarning";
        if (type == "logErro")
			return "logError";
		return "NullType";
	}
	//解析日志输出语句的正则形式
	private static String parseline(String log) {
		String ret = "";
		String context = log.substring(log.indexOf("(") + 1, log.length() - 1);
		// System.out.println(context);

		String patternString = "\"(.+?)\"";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(context);
		while (matcher.find()) {
			// System.out.println("found: " + matcher.group(1));
			ret += matcher.group(1) + "*";
		}
		// System.out.println("!!!:"+ret);
		ret = ret.replaceAll("\\$\\w+", "*");
		ret = ret.replaceAll("\\$\\{.+\\}", "*");
		ret = ret.replaceAll("%[\\w.\\d]+", "*");
		ret = ret.replace(",", "*");
		// System.out.println("###:"+ret);
		boolean nullflag = true;
		for(char tmpc : ret.toCharArray()){
			if (Character.isLetter(tmpc)) {
				nullflag = false;
			}
		}
		if (nullflag) {
			return "";
		}
		return ret;
	}

	//解析每个文件的日志模板
	private static void filter(String filepath,String orioutput,String regularoutput) throws Exception {
		FileWriter writer = new FileWriter(orioutput, true);
		FileWriter writer2 = new FileWriter(regularoutput, true);
		BufferedWriter bw = new BufferedWriter(writer);
		BufferedWriter bw2 = new BufferedWriter(writer2);

		String[] filearr = filepath.split("\\\\");
		String part1 = filearr[filearr.length - 2];
		String[] part2 = filearr[filearr.length - 1].split("\\.");
		String content = part1 + "." + part2[0];

		FileReader fr = new FileReader(filepath);
		BufferedReader br = new BufferedReader(fr);
		System.out.println("解析文件："+filepath);
		boolean foundflag = false;
		String line = null;
		String type = null;
		String orilog = null;
		String regularlog = null;
		
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("logInfo") | line.startsWith("logDebug")
					| line.startsWith("logTrace")
					| line.startsWith("logWarning")
					| line.startsWith("logError")) {
				foundflag = true;
				orilog = line;
				type = getLogType(line.substring(0, 7));
				if (line.endsWith(")")) {
					String info = type+"@@"+content + "@@" + orilog;
					if (oriset.contains(info)) {
						continue;
					}else {
						oriset.add(info);
					}
					bw.write(info);
					bw.newLine();
					bw.flush();
					regularlog = parseline(orilog);
					if (!regularlog.equals("")) {
						String reginfo = type+"@@"+content + "@@" + regularlog;
						if (regularset.contains(reginfo)) {
							continue;
						}else {
							regularset.add(reginfo);
						}
						bw2.write(reginfo);
						bw2.newLine();
						bw2.flush();
					}
					foundflag = false;
				}
			} else {
				if (foundflag) {
					if (line.endsWith(")")) {
						orilog += line;
						String info = type+"@@"+content + "@@" + orilog;
						if (oriset.contains(info)) {
							continue;
						}else {
							oriset.add(info);
						}
						bw.write(info);
						bw.newLine();
						bw.flush();
						regularlog = parseline(orilog);
						if (!regularlog.equals("")) {
							String reginfo = type+"@@"+content + "@@" + regularlog;
							if (regularset.contains(reginfo)) {
								continue;
							}else {
								regularset.add(info);
							}
							bw2.write(reginfo);
							bw2.newLine();
							bw2.flush();
						}
						foundflag = false;
					} else {
						orilog += line;
					}
				}
			}
		}
		br.close();
		fr.close();
		bw.close();
		bw2.close();
		writer.close();
		writer2.close();
	}
	
	//根据日志源码解析出日志及其正则
	public static void getSchema(String codepath,String orioutput,String regularoutput) throws Exception {
		if (new File(orioutput).exists()&&new File(regularoutput).exists()) {
			System.out.println("已经解析过，返回。");
			return;
		}
		File file = new File(codepath);
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
						filter(file2.getAbsolutePath(),orioutput,regularoutput);
					}
				}
			}
		} else {
			System.out.println("源码文件不存在!");
		}
		System.out.println("解析结束!");
	}
}
