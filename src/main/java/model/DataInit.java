package model;

import db.DBInstance;
import db.metadata.TaskIdFeaData;
import db.metadata.TypeSetData;
import utils.Config;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Administrator on 2015/12/18.
 */
public class DataInit {
    private static Config config = Config.getInstance();
    public Instances getBlockIdData() throws IOException {
        //从数据库读取
        DBInstance db = new DBInstance("blockfea");
        ArrayList<TaskIdFeaData> datalist = db.getAllTaskFeaData();
        db.close();
        db = new DBInstance("typeset");
        TypeSetData typeSetData = db.getTypeset("blockid");
        db.close();
        HashSet<String> typeSet = typeSetData.getTypeset();
        //生成文件头
        ArrayList<Attribute> attrlist = new ArrayList<Attribute>();
        for (String word : typeSet) {
            Attribute attr = new Attribute(word);
            attrlist.add(attr);
        }
        Instances instances_block = new Instances("block",attrlist,datalist.size());
        for (TaskIdFeaData task : datalist) {
            HashMap<String, Integer> tmpmap = task.getMap();
            int wordscnt = 0;
            for (String word : tmpmap.keySet()) {
                wordscnt += tmpmap.get(word);
            }
            String fea_tfidf = "";
            String fea_ori = "";
            Instance oneinstance = new DenseInstance(typeSet.size());
            int index=0;
            for (String simlog : typeSet) {
                if (tmpmap.containsKey(simlog)) {
                    oneinstance.setValue(index,tmpmap.get(simlog));
                } else {
                    oneinstance.setValue(index,0);
                }
                index++;
            }
            instances_block.add(oneinstance);
        }

        return instances_block;
//        FileWriter fw = new FileWriter("data/feature_blockid");
//        fw.write("@RELATION sparkDiagnoseSet\n");
//        for (String word : typeSet) {
//            fw.write("@ATTRIBUTE " + word + " numeric\n");
//        }
//        fw.write("@DATA\n");
//        //保存原始特征
//        FileWriter fw_ori = new FileWriter("d:/feature_block_" + ".xls");
//        String title = "schemaid\t";
//        for (String simlog : typeSet) {
//            title += simlog + "\t";
//        }
//        fw_ori.write(title + "\n");
//        //保存event list
//        FileWriter fw_event = new FileWriter("data/eventlist_blockid");
//
//        for (TaskIdFeaData task : datalist) {
//            fw_event.write(task.getTaskid() + "\n");
//            HashMap<String, Integer> tmpmap = task.getMap();
//            int wordscnt = 0;
//            for (String word : tmpmap.keySet()) {
//                wordscnt += tmpmap.get(word);
//            }
//            String fea_tfidf = "";
//            String fea_ori = "";
//            for (String simlog : typeSet) {
//                if (tmpmap.containsKey(simlog)) {
//                    fea_tfidf += tmpmap.get(simlog) + ",";
//                    fea_ori += tmpmap.get(simlog) + "\t";
//                } else {
//                    fea_tfidf += "0,";
//                    fea_ori += "0\t";
//                }
//            }
////			System.out.println(eachid+":"+fea_tfidf);
//            fw_ori.write(task.getTaskid() + "\t" + fea_ori + "\n");
//            fw.write(fea_tfidf.substring(0, fea_tfidf.length() - 1) + "\n");
//        }
//        fw.close();
//        fw_ori.close();
//        fw_event.close();
//        //结束存储
    }

    public void getTaskIdData() throws IOException {
        //从数据库读取
        DBInstance db = new DBInstance("taskfea");
        ArrayList<TaskIdFeaData> datalist = db.getAllTaskFeaData();
        db.close();
        db = new DBInstance("typeset");
        TypeSetData typeSetData = db.getTypeset("taskid");
        db.close();
        HashSet<String> typeSet = typeSetData.getTypeset();
        //生成文件头
        FileWriter fw = new FileWriter("data/feature_tid");
        fw.write("@RELATION sparkDiagnoseSet\n");
        for (String word : typeSet) {
            fw.write("@ATTRIBUTE " + word + " numeric\n");
        }
        fw.write("@DATA\n");
        //保存原始特征
        FileWriter fw_ori = new FileWriter("d:/feature_task_" + ".xls");
        String title = "schemaid\t";
        for (String simlog : typeSet) {
            title += simlog + "\t";
        }
        fw_ori.write(title + "\n");
        //保存event list
        FileWriter fw_event = new FileWriter("data/eventlist_tid");

        for (TaskIdFeaData task : datalist) {
            fw_event.write(task.getTaskid() + "\n");
            HashMap<String, Integer> tmpmap = task.getMap();
            int wordscnt = 0;
            for (String word : tmpmap.keySet()) {
                wordscnt += tmpmap.get(word);
            }
            String fea_tfidf = "";
            String fea_ori = "";
            for (String simlog : typeSet) {
                if (tmpmap.containsKey(simlog)) {
                    fea_tfidf += tmpmap.get(simlog) + ",";
                    fea_ori += tmpmap.get(simlog) + "\t";
                } else {
                    fea_tfidf += "0,";
                    fea_ori += "0\t";
                }
            }
//			System.out.println(eachid+":"+fea_tfidf);
            fw_ori.write(task.getTaskid() + "\t" + fea_ori + "\n");
            fw.write(fea_tfidf.substring(0, fea_tfidf.length() - 1) + "\n");
        }
        fw.close();
        fw_ori.close();
        fw_event.close();
        //结束存储
    }
}
