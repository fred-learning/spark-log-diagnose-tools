package models;

import db.DBInstance;
import db.metadata.BlockIdFeaData;
import db.metadata.TaskIdFeaData;
import db.metadata.TreeData;
import db.metadata.TypeSetData;
import utils.Config;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2015/12/18.
 */
public class DataInit {
    private static Config config = Config.getInstance();
    public Instances getAllTaskIdData4KmeansTree() throws IOException {
        //从数据库读取
        DBInstance db = new DBInstance("taskfea");
        ArrayList<TreeData> datalist = db.getAllTaskFeaData4Tree();
        db.close();
        db = new DBInstance("typeset");
        TypeSetData typeSetData = db.getTypeset(config.getTaskTag());
        db.close();
        HashSet<String> typeSet = typeSetData.getTypeset();
        //生成文件头
        ArrayList<Attribute> attrlist = new ArrayList<Attribute>();
        for (String word : typeSet) {
            Attribute attr = new Attribute(word);
            attrlist.add(attr);
        }
        List labelattr = new ArrayList();
        int k = Integer.valueOf(config.getKmeansTask());
        for(int i=0;i<k;i++){
            labelattr.add(String.valueOf(i));
        }
        attrlist.add(new Attribute("label",labelattr));

        Instances instances_block = new Instances(config.getTaskTag(),attrlist,datalist.size());
        for (TreeData treeData : datalist) {
            if (treeData.getKmeanslabel()==-1)
                continue;
            TaskIdFeaData task = (TaskIdFeaData)treeData.getFeadata();
            HashMap<String, Integer> tmpmap = task.getMap();
            int wordscnt = 0;
            for (String word : tmpmap.keySet()) {
                wordscnt += tmpmap.get(word);
            }
            Instance oneinstance = new DenseInstance(typeSet.size()+1);
            int index=0;
            for (String simlog : typeSet) {
                if (tmpmap.containsKey(simlog)) {
                    oneinstance.setValue(index,tmpmap.get(simlog));
                } else {
                    oneinstance.setValue(index,0);
                }
                index++;
            }
            oneinstance.setValue(index,treeData.getKmeanslabel());
            instances_block.add(oneinstance);
        }

        return instances_block;
    }
    public Instances getAllTaskIdData4PCATree() throws IOException {
        //从数据库读取
        DBInstance db = new DBInstance("taskfea");
        ArrayList<TreeData> datalist = db.getAllTaskFeaData4Tree();
        db.close();
        db = new DBInstance("typeset");
        TypeSetData typeSetData = db.getTypeset(config.getTaskTag());
        db.close();
        HashSet<String> typeSet = typeSetData.getTypeset();
        //生成文件头
        ArrayList<Attribute> attrlist = new ArrayList<Attribute>();
        for (String word : typeSet) {
            Attribute attr = new Attribute(word);
            attrlist.add(attr);
        }
        List labelattr = new ArrayList();
        labelattr.add("0");
        labelattr.add("1");
        attrlist.add(new Attribute("label",labelattr));

        Instances instances_block = new Instances(config.getTaskTag(),attrlist,datalist.size());
        for (TreeData treeData : datalist) {
            if (treeData.getPcalabel()==-1)
                continue;
            TaskIdFeaData task = (TaskIdFeaData)treeData.getFeadata();
            HashMap<String, Integer> tmpmap = task.getMap();
            int wordscnt = 0;
            for (String word : tmpmap.keySet()) {
                wordscnt += tmpmap.get(word);
            }
            Instance oneinstance = new DenseInstance(typeSet.size()+1);
            int index=0;
            for (String simlog : typeSet) {
                if (tmpmap.containsKey(simlog)) {
                    oneinstance.setValue(index,tmpmap.get(simlog));
                } else {
                    oneinstance.setValue(index,0);
                }
                index++;
            }
            oneinstance.setValue(index,treeData.getPcalabel());
            instances_block.add(oneinstance);
        }

        return instances_block;
    }
    public Instances getAllBlockIdData4KmeansTree() throws IOException {
        //从数据库读取
        DBInstance db = new DBInstance("blockfea");
        ArrayList<TreeData> datalist = db.getAllBlockFeaData4Tree();
        db.close();
        db = new DBInstance("typeset");
        TypeSetData typeSetData = db.getTypeset(config.getBlockTag());
        db.close();
        HashSet<String> typeSet = typeSetData.getTypeset();
        //生成文件头
        ArrayList<Attribute> attrlist = new ArrayList<Attribute>();
        for (String word : typeSet) {
            Attribute attr = new Attribute(word);
            attrlist.add(attr);
        }
        List labelattr = new ArrayList();
        int k = Integer.valueOf(config.getKmeansBlock());
        for(int i=0;i<k;i++){
            labelattr.add(String.valueOf(i));
        }
        attrlist.add(new Attribute("label",labelattr));

        Instances instances_block = new Instances(config.getBlockTag(),attrlist,datalist.size());
        for (TreeData treeData : datalist) {
            if (treeData.getKmeanslabel()==-1)
                continue;
            BlockIdFeaData task = (BlockIdFeaData)treeData.getFeadata();
            HashMap<String, Integer> tmpmap = task.getMap();
            int wordscnt = 0;
            for (String word : tmpmap.keySet()) {
                wordscnt += tmpmap.get(word);
            }
            Instance oneinstance = new DenseInstance(typeSet.size()+1);
            int index=0;
            for (String simlog : typeSet) {
                if (tmpmap.containsKey(simlog)) {
                    oneinstance.setValue(index,tmpmap.get(simlog));
                } else {
                    oneinstance.setValue(index,0);
                }
                index++;
            }
            oneinstance.setValue(index,treeData.getKmeanslabel());
            instances_block.add(oneinstance);
        }

        return instances_block;
    }
    public Instances getAllBlockIdData4PCATree() throws IOException {
        //从数据库读取
        DBInstance db = new DBInstance("blockfea");
        ArrayList<TreeData> datalist = db.getAllBlockFeaData4Tree();
        db.close();
        db = new DBInstance("typeset");
        TypeSetData typeSetData = db.getTypeset(config.getBlockTag());
        db.close();
        HashSet<String> typeSet = typeSetData.getTypeset();
        //生成文件头
        ArrayList<Attribute> attrlist = new ArrayList<Attribute>();
        for (String word : typeSet) {
            Attribute attr = new Attribute(word);
            attrlist.add(attr);
        }
        List labelattr = new ArrayList();
        labelattr.add("0");
        labelattr.add("1");
        attrlist.add(new Attribute("label",labelattr));

        Instances instances_block = new Instances(config.getBlockTag(),attrlist,datalist.size());
        for (TreeData treeData : datalist) {
            if (treeData.getPcalabel()==-1)
                continue;
            BlockIdFeaData task = (BlockIdFeaData)treeData.getFeadata();
            HashMap<String, Integer> tmpmap = task.getMap();
            int wordscnt = 0;
            for (String word : tmpmap.keySet()) {
                wordscnt += tmpmap.get(word);
            }
            Instance oneinstance = new DenseInstance(typeSet.size()+1);
            int index=0;
            for (String simlog : typeSet) {
                if (tmpmap.containsKey(simlog)) {
                    oneinstance.setValue(index,tmpmap.get(simlog));
                } else {
                    oneinstance.setValue(index,0);
                }
                index++;
            }
            oneinstance.setValue(index,treeData.getPcalabel());
            instances_block.add(oneinstance);
        }

        return instances_block;
    }
    public Instances getAllBlockIdData() throws IOException {
        //从数据库读取
        DBInstance db = new DBInstance("blockfea");
        ArrayList<BlockIdFeaData> datalist = db.getAllBlockFeaData();
        db.close();
        db = new DBInstance("typeset");
        TypeSetData typeSetData = db.getTypeset(config.getBlockTag());
        db.close();
        HashSet<String> typeSet = typeSetData.getTypeset();
        //生成文件头
        ArrayList<Attribute> attrlist = new ArrayList<Attribute>();
        for (String word : typeSet) {
            Attribute attr = new Attribute(word);
            attrlist.add(attr);
        }
        Instances instances_block = new Instances(config.getBlockTag(),attrlist,datalist.size());
        for (BlockIdFeaData task : datalist) {
            HashMap<String, Integer> tmpmap = task.getMap();
            int wordscnt = 0;
            for (String word : tmpmap.keySet()) {
                wordscnt += tmpmap.get(word);
            }
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
    }

    public Instances getAllTaskIdData() throws IOException {
        //从数据库读取
        DBInstance db = new DBInstance("taskfea");
        ArrayList<TaskIdFeaData> datalist = db.getAllTaskFeaData();
        db.close();
        db = new DBInstance("typeset");
        TypeSetData typeSetData = db.getTypeset(config.getTaskTag());
        db.close();
        HashSet<String> typeSet = typeSetData.getTypeset();
        //生成文件头
        ArrayList<Attribute> attrlist = new ArrayList<Attribute>();
        for (String word : typeSet) {
            Attribute attr = new Attribute(word);
            attrlist.add(attr);
        }
        Instances instances_task = new Instances(config.getTaskTag(),attrlist,datalist.size());
        for (TaskIdFeaData task : datalist) {
            HashMap<String, Integer> tmpmap = task.getMap();
            int wordscnt = 0;
            for (String word : tmpmap.keySet()) {
                wordscnt += tmpmap.get(word);
            }
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
            instances_task.add(oneinstance);
        }

        return instances_task;
    }
    public Instances getOneBlockIdData(String appid) throws IOException {
        //从数据库读取
        DBInstance db = new DBInstance("blockfea");
        ArrayList<BlockIdFeaData> datalist = db.getOneBlockFeaData(appid);
        db.close();
        db = new DBInstance("typeset");
        TypeSetData typeSetData = db.getTypeset(config.getBlockTag());
        db.close();
        HashSet<String> typeSet = typeSetData.getTypeset();
        //生成文件头
        ArrayList<Attribute> attrlist = new ArrayList<Attribute>();
        for (String word : typeSet) {
            System.out.println(word);
            Attribute attr = new Attribute(word);
            attrlist.add(attr);
        }
        Instances instances_block = new Instances(config.getBlockTag(),attrlist,datalist.size());
        for (BlockIdFeaData task : datalist) {
            HashMap<String, Integer> tmpmap = task.getMap();
            int wordscnt = 0;
            for (String word : tmpmap.keySet()) {
                wordscnt += tmpmap.get(word);
            }
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
    }

    public Instances getOneTaskIdData(String appid) throws IOException {
        //从数据库读取
        DBInstance db = new DBInstance("taskfea");
        ArrayList<TaskIdFeaData> datalist = db.getOneTaskFeaData(appid);
        db.close();
        db = new DBInstance("typeset");
        TypeSetData typeSetData = db.getTypeset(config.getTaskTag());
        db.close();
        HashSet<String> typeSet = typeSetData.getTypeset();
        //生成文件头
        ArrayList<Attribute> attrlist = new ArrayList<Attribute>();
        for (String word : typeSet) {
            System.out.println(word);
            Attribute attr = new Attribute(word);
            attrlist.add(attr);
        }
        Instances instances_block = new Instances(config.getTaskTag(),attrlist,datalist.size());
        for (TaskIdFeaData task : datalist) {
            HashMap<String, Integer> tmpmap = task.getMap();
            int wordscnt = 0;
            for (String word : tmpmap.keySet()) {
                wordscnt += tmpmap.get(word);
            }
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
    }
}
