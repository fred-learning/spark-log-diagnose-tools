package parseLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import db.DBInstance;
import db.metadata.BlockIdFeaData;
import db.metadata.TypeSetData;
import parseSourceCode.GetLogIndex;
import utils.Config;

public class ExtractBlockFea {
    private static  Config config = Config.getInstance();

    private HashMap<String, ArrayList<String>> logschemamap;//日志字典 component:[reglog]
    private HashMap<String, BlockIdFeaData> feamap;//identifier：feadata
    private HashSet<String> typeSet;//存都有哪些component
    private String nowstage;//记录当前的stage

    //初始化
    private void init() {
        logschemamap = GetLogIndex.getIndex();
        typeSet = new HashSet<String>();
        nowstage = "nullstage";
        feamap = new HashMap<String, BlockIdFeaData>();
    }

    private void puttomap(String complexlog, String simplelog,String filename){
        if (simplelog.equals("executor.Executor3")) {
            nowstage = complexlog.split(" ")[5].split("\\.")[0];
        } else {
            if (simplelog.equals("executor.Executor7")) {
                nowstage="nullstage";
                return;
            }else {
                Pattern pt = Pattern.compile(".*(rdd_\\d+_\\d+).*");
                Matcher mt = pt.matcher(complexlog);
                if (mt.find()) {
//                    System.out.print(".");
                    typeSet.add(simplelog);
                    String blockid = mt.group(1);
                    if (feamap.containsKey(blockid)) {
                        BlockIdFeaData data = feamap.get(blockid);
                        data.addFea(nowstage,simplelog);
                        feamap.put(blockid,data);
                    } else {
                        BlockIdFeaData data = new BlockIdFeaData(config.getClusterID(),filename,blockid);
                        data.addFea(nowstage,simplelog);
                        feamap.put(blockid, data);
                    }
                }
            }
        }
    }
    //提取每行的特征
    private void linematch(String context, String filepath) {
        context = context.trim();
        if (context.equals(""))
            return;
        String regex = "^\\d{2}/\\d{2}/\\d{2}";
        Pattern pt = Pattern.compile(regex);
        Matcher matcher = pt.matcher(context);
        if (matcher.find()) {
            try {
                String[] part = context.split(" ");
                String timestamp = context.substring(0, 17);
                String logtype = part[2];
                String component = part[3].substring(0, part[3].length() - 1);
                int reallogstart = part[0].length() + part[1].length() + part[2].length() + part[3].length() + 4;
                if (reallogstart > context.length()) {
                    return;
                }
                String logString = context.substring(reallogstart, context.length());
                if (logschemamap.containsKey(component)) {
                    ArrayList<String> tmpset = logschemamap.get(component);
                    boolean flag = false;
                    int count = 0;
                    for (String eachregex : tmpset) {
                        count++;
                        pt = Pattern.compile(eachregex.split("@@")[0]);
                        matcher = pt.matcher(logString);
                        if (matcher.find()) {
                            String simplelog = component + count;
//                            System.out.println(simplelog);
                            puttomap(logString,simplelog,filepath);
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
//						System.out.println("not match log---"+context);
                    }
                } else {
//					System.out.println("not find key---"+context);
                }

            } catch (Exception e) {
                // TODO: handle exception
                System.out.println(context);
                e.printStackTrace();
            }
        }
        return;
    }

    //提取每个文件的特征
    private void matchEachFile(String filepath) throws IOException {
        System.out.println("extract file:"+filepath);
        try {
//            String[] patharr = filepath.split("\\\\");
            String[] patharr = filepath.split("/");
            String appid = patharr[patharr.length-1];
            //如果已经保存了，跳过
            DBInstance blockdb = new DBInstance("blockfea");
            if(blockdb.checkOneFeaData(appid)){
                System.out.println(appid+" existed,skipped");
                return;
            }
            FileReader fr = new FileReader(filepath);
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                linematch(line, appid);
            }
            br.close();
            fr.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        //将特征存储到数据库中
        DBInstance feadb = new DBInstance("blockfea");
        for (String eachid : feamap.keySet()) {
            BlockIdFeaData tmpdata = feamap.get(eachid);
//            tmpdata.print();
            feadb.saveBlockFeaData(tmpdata);
        }
        feamap.clear();
        feadb.close();
    }

    private boolean matchAllFile(String logpath) throws IOException {
        File file = new File(logpath);
        if (file.exists()) {
            if (file.isFile()){
                matchEachFile(file.getAbsolutePath());
                return true;
            }
            LinkedList<File> list = new LinkedList<File>();
            list.add(file);
            while (!list.isEmpty()) {
                File temp_file = list.removeFirst();
                File[] files = temp_file.listFiles();
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        list.add(file2);
                    } else {
                        System.out.println("handle file:"+file2.getAbsolutePath());
                        matchEachFile(file2.getAbsolutePath());
                    }
                }
            }
        } else {
            System.out.println("日志文件不存在!");
            return false;
        }
        return true;
    }

    //入口，根据正则模板和字典，对日志目录进行特征提取，输出特征
    public void DoExtract(String logpath,boolean trainflag) throws Exception {
        init();
        if (logschemamap.isEmpty()) {
            System.out.println("error:fail to get log schema map!");
            return;
        }
        if (!matchAllFile(logpath)){
            return;
        }
        System.out.println("finished extracting feature");

        //保存typeset
        if (trainflag) {
            DBInstance typesetdb = new DBInstance("typeset");
            TypeSetData oldtype = typesetdb.getTypeset(config.getBlockTag());
            if (oldtype!=null){
                HashSet<String> tmp = oldtype.getTypeset();
                typeSet.addAll(tmp);
            }
            TypeSetData typeSetData = new TypeSetData(config.getBlockTag(), typeSet);
            typesetdb.saveTypeset(typeSetData);
            typesetdb.close();
        }
    }
}
