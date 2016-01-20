package server;

import bin.BlockFeaDiag;
import crawler.DAGUtils;
import crawler.HDFSPuller;
import crawler.metadata.SparkSummary;
import crawler.metadata.Stage;
import db.DBInstance;
import db.metadata.*;
import models.Kmeans;
import models.PCA;
import parseLog.ExtractBlockFea;
import parseLog.ExtractTaskFea;
import parseLog.MatchLogSchema;
import utils.Config;
import utils.StoreUtils;

import java.util.*;

/**
 * Created by Administrator on 2015/12/15.
 */
public class SingletonHandle {
    private static SingletonHandle handle;
    private static Config config = Config.getInstance();

    public static synchronized SingletonHandle getInstance() {
        if (handle == null) {
            //init others before it
            handle = new SingletonHandle();
        }
        return handle;
    }

    public void dodiagnose(String clusterid, String appid) throws Exception {
        //判断是否已诊断过，返回or诊断
        DBInstance db = new DBInstance("diagnose");
        if (db.getDiagnoseResData(clusterid,appid).size()>0){
            db.close();
            return;
        }
        //拉取日志
        HDFSPuller.PullLog(appid);
        //提取特征
        new ExtractBlockFea().DoExtract(config.getTestLogPath()+appid+"/"+appid,false);
        new ExtractTaskFea().DoExtract(config.getTestLogPath()+appid+"/"+appid,false);
        //诊断
        new Kmeans().testBlockFea(appid);
        new Kmeans().testTaskFea(appid);
//        new PCA().testBlockFea(appid);
//        new PCA().testTaskFea(appid);
        //拉取history server
        TreeMap<Integer, String> stagemap = new TreeMap<Integer, String>();
        List<SparkSummary> summarylist = DAGUtils.getAppSummarys(appid);
        if (summarylist.size() == 1) {
            SparkSummary ss = summarylist.get(0);
            List<Stage> stagelist = DAGUtils.getJobsByAppSummary(ss);
            for (Stage stage : stagelist) {
                stagemap.put(stage.getStageId(), stage.getName());
                System.out.println(stage.getStageId() + "--" + stage.getName());
            }
        }
        //存储结果
        //展示task kmeans的诊断
        DBInstance modeldb = new DBInstance("model");
        ModelData model = modeldb.getModel(config.getTaskTag(), "kmeans");
        String[] badlist = model.getAnomalylabels();
        HashSet<String> badset = new HashSet<String>(new ArrayList<String>(Arrays.asList(badlist)));
        modeldb.close();

        DBInstance taskdb = new DBInstance("taskfea");
        ArrayList<TreeData> datalist = taskdb.getOneTaskFeaData4Tree(appid);
        HashMap<String,DiagnoseResData> diagmap = new HashMap<String, DiagnoseResData>();
        for (TreeData treeData:datalist){
            TaskIdFeaData taskIdFeaData = (TaskIdFeaData)treeData.getFeadata();
            String stageid = taskIdFeaData.getStageid();
            String label = String.valueOf(treeData.getKmeanslabel());
            String locate = stagemap.get(Integer.valueOf(stageid));
            DiagnoseResData tmp;
            if (diagmap.containsKey(stageid)){
                tmp = diagmap.get(stageid);
            }else {
                tmp = new DiagnoseResData(clusterid, appid, stageid, locate);
            }
            if (badset.contains(label))
                tmp.updateTaskBadmap(taskIdFeaData.getTaskid(),label+":anomaly");
            else
                tmp.updateTaskGoodmap(taskIdFeaData.getTaskid(), label + ":ok");
            diagmap.put(stageid,tmp);
        }
//        for(String stageid:diagmap.keySet()){
//            DiagnoseResData tmp = diagmap.get(stageid);
//            tmp.updateCnt();
//            db.saveDiagnoseResData(tmp);
//        }
//        db.close();
        //展示block kmeans的诊断
        DBInstance modeldb2 = new DBInstance("model");
        ModelData model2 = modeldb2.getModel(config.getBlockTag(), "kmeans");
        String[] badlist2 = model2.getAnomalylabels();
        HashSet<String> badset2 = new HashSet<String>(new ArrayList<String>(Arrays.asList(badlist2)));
        modeldb.close();

        DBInstance blockdb = new DBInstance("blockfea");
        ArrayList<TreeData> datalist2 = blockdb.getOneBlockFeaData4Tree(appid);
        for (TreeData treeData:datalist2){
            BlockIdFeaData blockIdFeaData = (BlockIdFeaData)treeData.getFeadata();
            HashSet<String> stageset = blockIdFeaData.getStageid();
            String label = String.valueOf(treeData.getKmeanslabel());
            for (String stageid:stageset){
                if (stageid.equals("nullstage"))
                    continue;
                String locate = stagemap.get(Integer.valueOf(stageid));
                DiagnoseResData tmp;
                if (diagmap.containsKey(stageid)){
                    tmp = diagmap.get(stageid);
                }else {
                    tmp = new DiagnoseResData(clusterid, appid, stageid, locate);
                }
                if (badset2.contains(label))
                    tmp.updateBlockBadmap(blockIdFeaData.getBlockid(), label + ":anomaly");
                else
                    tmp.updateBlockGoodmap(blockIdFeaData.getBlockid(), label + ":ok");
                diagmap.put(stageid,tmp);
            }
        }

        //完成task和blcok的诊断后，存储
        for(String stageid:diagmap.keySet()){
            DiagnoseResData tmp = diagmap.get(stageid);
            tmp.updatBlockCnt();
            tmp.updateTaskCnt();
            db.saveDiagnoseResData(tmp);
        }
        db.close();
    }
}
