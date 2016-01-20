package models;

import db.DBInstance;
import db.metadata.BlockIdFeaData;
import db.metadata.ModelData;
import db.metadata.TaskIdFeaData;
import parseLog.FileUtils;
import utils.Config;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Kmeans {
    private static Config config = Config.getInstance();
	public void trainTaskFea(int clusternum) throws Exception{
		SimpleKMeans kmeans = new SimpleKMeans();
		// -n 中心数
		// -init 初始化中心方式，0 = random, 1 = k-means++, 2 = canopy, 3 = farthest first
		// -o 保存顺序
		// -s 随机种子
		String[] options = {"-N",String.valueOf(clusternum),"-init","3","-O","-S","13"};
		kmeans.setOptions(options);
//		ChebyshevDistance, EuclideanDistance, FilteredDistance, ManhattanDistance, MinkowskiDistance, NormalizableDistance
//		kmeans.setDistanceFunction(new CosineDistanceMeasure());
		Instances data = new DataInit().getAllTaskIdData();
        System.out.println("start train task kmeans model");
		kmeans.buildClusterer(data);

		//输出评估信息
		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(kmeans);
		eval.evaluateClusterer(data);
		System.out.println("Cluster Evaluation: "+eval.clusterResultsToString());

        System.out.println("please label the anomaly class,split by ',':");
        Scanner in = new Scanner(System.in);
        String[] badlist = in.next().split(",");
        //模型存储
        ModelData modelData = new ModelData(config.getTaskTag(),"kmeans",kmeans,badlist);
        DBInstance modeldb = new DBInstance("model");
        modeldb.saveModel(modelData);
        modeldb.close();

        //更新类别
        int[] assignments = kmeans.getAssignments();
        ArrayList<Integer> labellist = new ArrayList<Integer>();
        for(int e:assignments){
            labellist.add(e);
        }
        DBInstance db = new DBInstance(config.getTaskTag());
        ArrayList<TaskIdFeaData> datalist = db.getAllTaskFeaData();
        if (datalist.size()==labellist.size()){
            db.updateTaskFeaData(datalist,null,labellist);
        }
        db.close();
	}
	public void testTaskFea(String appid) throws Exception {
        //判断是否已test过
        DBInstance db = new DBInstance("taskfea");
//        if (db.checkDoneOneKmeansLabel(appid)){
//            db.close();
//            return;
//        }
//        db.close();
		//反序列化模型数据
        db = new DBInstance("model");
        ModelData modelData = db.getModel(config.getTaskTag(),"kmeans");
        SimpleKMeans model = (SimpleKMeans)modelData.getModel();
        db.close();
		//测试
		Instances testdata = new DataInit().getOneTaskIdData(appid);
        db = new DBInstance("taskfea");
        ArrayList<TaskIdFeaData> datalist = db.getOneTaskFeaData(appid);
        ArrayList<Integer> labellist = new ArrayList<Integer>();
		for (Instance instance : testdata) {
            int zeus = Zeus.tag_kmeans_task(instance);
			int cluster = zeus==-1?model.clusterInstance(instance):zeus;
			labellist.add(cluster);
		}
        db.updateTaskFeaData(datalist,null,labellist);
        db.close();
	}

    public void trainBlockFea(int clusternum) throws Exception{
        SimpleKMeans kmeans = new SimpleKMeans();
        // -n 中心数
        // -init 初始化中心方式，0 = random, 1 = k-means++, 2 = canopy, 3 = farthest first
        // -o 保存顺序
        // -s 随机种子
        String[] options = {"-N",String.valueOf(clusternum),"-init","3","-O","-S","13"};
        kmeans.setOptions(options);
//		ChebyshevDistance, EuclideanDistance, FilteredDistance, ManhattanDistance, MinkowskiDistance, NormalizableDistance
//		kmeans.setDistanceFunction(new ChebyshevDistance());
        Instances data = new DataInit().getAllBlockIdData();
        kmeans.buildClusterer(data);

        //输出评估信息
        ClusterEvaluation eval = new ClusterEvaluation();
        eval.setClusterer(kmeans);
        eval.evaluateClusterer(data);
        System.out.println("Cluster Evaluation: "+eval.clusterResultsToString());

        System.out.println("please label the anomaly class,split by ',':");
        Scanner in = new Scanner(System.in);
        String[] badlist = in.next().split(",");
        //模型存储
        ModelData modelData = new ModelData(config.getBlockTag(),"kmeans",kmeans,badlist);
        DBInstance modeldb = new DBInstance("model");
        modeldb.saveModel(modelData);
        modeldb.close();

        //更新类别
        int[] assignments = kmeans.getAssignments();
        ArrayList<Integer> labellist = new ArrayList<Integer>();
        for(int e:assignments){
            labellist.add(e);
        }
        DBInstance db = new DBInstance("blockfea");
        ArrayList<BlockIdFeaData> datalist = db.getAllBlockFeaData();
        if (datalist.size()==labellist.size()){
            db.updateBlockFeaData(datalist, null, labellist);
        }
        db.close();
    }
    public void testBlockFea(String appid) throws Exception {
        //判断是否已test过
        DBInstance db = new DBInstance("blockfea");
//        if (db.checkDoneOneKmeansLabel(appid)){
//            db.close();
//            return;
//        }
//        db.close();
        //反序列化模型数据
        db = new DBInstance("model");
        ModelData modelData = db.getModel(config.getBlockTag(),"kmeans");
        SimpleKMeans model = (SimpleKMeans)modelData.getModel();
        db.close();
        //测试
        Instances testdata = new DataInit().getOneBlockIdData(appid);
        db = new DBInstance("blockfea");
        ArrayList<BlockIdFeaData> datalist = db.getOneBlockFeaData(appid);
        ArrayList<Integer> labellist = new ArrayList<Integer>();
        for (Instance instance : testdata) {
            int zeus = Zeus.tag_kmeans_block(instance);
            int cluster = zeus==-1?model.clusterInstance(instance):zeus;
            labellist.add(cluster);
        }
        db.updateBlockFeaData(datalist,null,labellist);
        db.close();
    }
}
