package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import parseLog.FileUtils;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.ChebyshevDistance;
import weka.core.Instance;
import weka.core.Instances;

public class Kmeans {
    public static ArrayList<String> testlist = new ArrayList<String>();
	public static void train(int k,String type,String filepath) throws Exception{
		int clusternum = k;
		SimpleKMeans kmeans = new SimpleKMeans();
		// -n 中心数
		// -init 初始化中心方式，0 = random, 1 = k-means++, 2 = canopy, 3 = farthest first
		// -o 保存顺序
		// -s 随机种子
		String[] options = {"-N",String.valueOf(clusternum),"-init","3","-O","-S","13"};
		kmeans.setOptions(options);
//		ChebyshevDistance, EuclideanDistance, FilteredDistance, ManhattanDistance, MinkowskiDistance, NormalizableDistance
//		kmeans.setDistanceFunction(new ChebyshevDistance());
		
		String inputfile = filepath+type;
		BufferedReader datafile = new BufferedReader(new FileReader(inputfile));
		Instances data = new Instances(datafile);
		kmeans.buildClusterer(data);
		
		// This array returns the cluster number (starting with 0) for each
		// instance.The array has as many elements as the number of instances
		int[] assignments = kmeans.getAssignments();

		//生成label数据
		FileWriter fw = new FileWriter("data/label_kmeans_"+type);
		fw.write("@RELATION label\n");
		String labelString = "";
		for (int i = 0; i < clusternum; i++) {
			labelString+=i+",";
		}
		fw.write("@ATTRIBUTE label {"+labelString.substring(0,labelString.length()-1)+"}\n");
		fw.write("@DATA\n");
		
		//读取事件列表
        ArrayList<String> eventlist = FileUtils.getEventList(type);
        for(int i=0;i<assignments.length;i++){
//    		System.out.println(eventlist.get(i)+"->"+assignments[i]);
			fw.write(assignments[i]+"\n");
		}
		fw.close();
		//输出评估信息
		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(kmeans);
		eval.evaluateClusterer(data);
		System.out.println("Cluster Evaluation: "+eval.clusterResultsToString());
		
		//序列化模型存储
		String modelpath ="data/model_kmeans_"+type; 
		if (new File(modelpath).exists()) {
			new File(modelpath).delete();
		}
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(modelpath));
        out.writeObject(kmeans);    //写入customer对象
        out.close();
	}
	public static void test(HashSet<Integer> badset,String type) throws Exception {
		TreeMap<String, HashMap<String, Integer>> stagestat = new TreeMap<String, HashMap<String, Integer>>();
		HashMap<String, HashMap<String, Integer>> anomalystat = new HashMap<String, HashMap<String, Integer>>();
		//反序列化模型数据
		String modelpath ="data/model_kmeans_"+type;
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(modelpath));
        SimpleKMeans model = (SimpleKMeans) in.readObject();    //读取customer对象
        in.close();
        //读取事件列表
        ArrayList<String> eventlist = FileUtils.getEventList(type);
		//测试
		String testpath = "data/feature_test_"+type;
		BufferedReader testfile = new BufferedReader(new FileReader(testpath));
		Instances testdata = new Instances(testfile);
		int i=0;
		for (Instance instance : testdata) {
			int cluster = model.clusterInstance(instance);
			String[] infoarr = eventlist.get(i).split(" ");
			//统计每个文件中所有/异常的stage比例
			String stageid="",fileid="";
			if (type=="tid") {
				fileid = infoarr[8];
				stageid = infoarr[6];
				if (stagestat.containsKey(fileid)) {
					HashMap<String, Integer> tmpmap = stagestat.get(fileid);
					if (tmpmap.containsKey(stageid)) {
						tmpmap.put(stageid, tmpmap.get(stageid)+1);
					}else {
						tmpmap.put(stageid, 1);
					}
					stagestat.put(fileid, tmpmap);
				}else {
					HashMap<String, Integer> tmpmap = new HashMap<String, Integer>();
					tmpmap.put(stageid, 1);
					stagestat.put(fileid, tmpmap);
				}
			}
			if(badset.contains(cluster)){
				System.out.println(eventlist.get(i)+"->"+cluster);
                testlist.add(eventlist.get(i));
				//统计异常的file：stage：cnt
				if (type=="tid") {
					if (anomalystat.containsKey(fileid)) {
						HashMap<String, Integer> tmpmap = anomalystat.get(fileid);
						if (tmpmap.containsKey(stageid)) {
							tmpmap.put(stageid, tmpmap.get(stageid)+1);
						}else {
							tmpmap.put(stageid, 1);
						}
						anomalystat.put(fileid, tmpmap);
					}else {
						HashMap<String, Integer> tmpmap = new HashMap<String, Integer>();
						tmpmap.put(stageid, 1);
						anomalystat.put(fileid, tmpmap);
					}
				}
			}
			i++;
		}
//		if (type=="tid") {
//			for(String eachfile:stagestat.keySet()){
//				System.out.println("检测文件："+eachfile);
//				HashMap<String, Integer> allmap = stagestat.get(eachfile);
//				HashMap<String, Integer> badmap = new HashMap<>();
//				if (anomalystat.containsKey(eachfile)) {
//					badmap = anomalystat.get(eachfile);
//				}
//				System.out.println("各个stage检测结果，格式为【id-all-anomaly】：");
//				for(String eachstage:allmap.keySet()){
//					if (badmap.containsKey(eachstage)) {
//						System.out.println("stage "+eachstage+"-"+allmap.get(eachstage)+"-"+badmap.get(eachstage));
//					}else {
//						System.out.println("stage "+eachstage+"-"+allmap.get(eachstage)+"-0");
//					}
//				}
//			}
//		}
	}
}
