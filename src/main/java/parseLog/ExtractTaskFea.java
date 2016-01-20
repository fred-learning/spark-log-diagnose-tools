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
import db.metadata.TaskIdFeaData;
import db.metadata.TypeSetData;
import parseSourceCode.GetLogIndex;
import utils.Config;

public class ExtractTaskFea {
    private static Config config = Config.getInstance();
    private DBInstance taskfeadb;
    private HashMap<String, ArrayList<String>> logschemamap;//日志字典 component:[reglog]

    private HashSet<String> typeSet;//存都有哪些component
    private boolean start;//tid特征中判断某任务的开始
    private String nowstage;//记录当前的tid name，即*task in *stage
    private String nowtidindex;//记录当前tid index 即tid*
    private HashMap<String, Integer> tidMap;//记录当前tid中的fea:cnt

    //初始化或者释放空间
    private void init() {
        taskfeadb = new DBInstance("taskfea");
        logschemamap = GetLogIndex.getIndex();
        typeSet = new HashSet<String>();
        start = false;
        nowstage = "";
        nowtidindex = "";
        tidMap = new HashMap<String, Integer>();
    }

    //根据tid来聚合提取特征
    private void clusterbyTID(String complexlog, String simplelog, String filename) throws IOException {
        if (simplelog.equals("executor.Executor3")) {
            start = true;
            tidMap.clear();
            nowstage = complexlog.split(" ")[5].split("\\.")[0];
            nowtidindex = complexlog.substring(0,complexlog.length()-1).split("\\(")[1];
        } else {
            if (simplelog.equals("executor.Executor7")) {
                //对于tid，如果其涉及的事件太少，则忽略
                int eventthreshold = 2;
                if (tidMap.size() > eventthreshold) {
                    TaskIdFeaData taskIdFeaData = new TaskIdFeaData(config.getClusterID(),filename,nowstage,nowtidindex,new HashMap<String, Integer>(tidMap));
//                    taskIdFeaData.print();
                    taskfeadb.saveTaskFeaData(taskIdFeaData);
                }
                start = false;
                return;
            }
        }
        if (start) {
            typeSet.add(simplelog);
            if (tidMap.containsKey(simplelog)) {
                tidMap.put(simplelog, tidMap.get(simplelog) + 1);
            } else {
                tidMap.put(simplelog, 1);
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
                            clusterbyTID(logString, simplelog,filepath);//根据tid聚类
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
//                        System.out.println("not match log---" + context);
                    }
                    return;
                } else {
//                    System.out.println("not find key---" + context);
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
    private void matchEachFile(String filepath) {
        try {
//            String[] patharr = filepath.split("\\\\");
            String[] patharr = filepath.split("/");
            String appid = patharr[patharr.length-1];
            //如果已经保存了，跳过
            DBInstance blockdb = new DBInstance("taskfea");
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
    }

    private boolean matchAllFile(String logpath) {
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
                        System.out.println("handle file:" + file2.getAbsolutePath());
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

        if (!matchAllFile(logpath)) {
            return;
        }
        taskfeadb.close();

        //保存typeset
        if (typeSet.isEmpty())
            return;
        if (trainflag) {
            DBInstance typesetdb = new DBInstance("typeset");
            TypeSetData oldtype = typesetdb.getTypeset(config.getTaskTag());
            if (oldtype!=null){
                HashSet<String> tmp = oldtype.getTypeset();
                typeSet.addAll(tmp);
            }
            TypeSetData typeSetData = new TypeSetData(config.getTaskTag(), typeSet);
            typesetdb.saveTypeset(typeSetData);
            typesetdb.close();
        }
    }
}
