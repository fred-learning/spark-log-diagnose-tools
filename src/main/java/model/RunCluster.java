package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class RunCluster {
	public static void main(String[] args) throws Exception {
		int clusternum = 6;
		SimpleKMeans kmeans = new SimpleKMeans();
		// -n 中心数
		// -init 初始化中心方式，3最远点
		// -o 保存顺序
		// -s 随机种子
		String[] options = {"-N",String.valueOf(clusternum),"-init","3","-O","-S","13"};
		kmeans.setOptions(options);
		
		
		String inputfile = "data/feature.arff";
		BufferedReader datafile = new BufferedReader(new FileReader(inputfile));
		Instances data = new Instances(datafile);
		
		kmeans.buildClusterer(data);

		// This array returns the cluster number (starting with 0) for each
		// instance.The array has as many elements as the number of instances
		int[] assignments = kmeans.getAssignments();

		//生成label数据
		FileWriter fw = new FileWriter("data/label.arff");
		fw.write("@RELATION label\n");
		String labelString = "";
		for (int i = 0; i < clusternum; i++) {
			labelString+=i+",";
		}
		fw.write("@ATTRIBUTE label {"+labelString.substring(0,labelString.length()-1)+"}\n");
		fw.write("@DATA\n");
		
		int i = 0;
		for (int clusterNum : assignments) {
			System.out.printf("Instance %d -> Cluster %d \n", i, clusterNum);
			fw.write(clusterNum+"\n");
			i++;
		}
		fw.close();
		
		//输出评估信息
		String testpath = "data/feature_test.arff";
		BufferedReader testfile = new BufferedReader(new FileReader(testpath));
		Instances testdata = new Instances(testfile);
		
		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(kmeans);
		eval.evaluateClusterer(testdata);
		double[] assig = eval.getClusterAssignments();
		for (double d : assig) {
			System.out.println(d);
		}
		System.out.println("Cluster Evaluation: "+eval.clusterResultsToString());
	}
}
