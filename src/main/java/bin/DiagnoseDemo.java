package bin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import model.DecisionTree;
import model.Kmeans;
import model.PCA;
import parseLog.MatchLogSchema;
import parseSourceCode.GetSourceSchema;
import utils.StoreUtils;

public class DiagnoseDemo {
	public static void main(String[] args) throws Exception {
		String regpath="d:/regularSchemaList";
		String dictpath="d:/schemaDict";
		
		String trainlogpath = "d:/trainlogdir";
		String trainfeapath = "data/feature_";
		String testlogpath = "d:/testlogdir";
		String testfeapath = "data/feature_test_";
		
		HashMap<String, Integer> kmeansKmap = new HashMap<String, Integer>();
		kmeansKmap.put("tid", 8);
		kmeansKmap.put("blockid", 8);
		String[] typearr = {"tid"};//"tid","blockid"
		boolean flag_tfidf = false;
		
		Scanner in = new Scanner(System.in);
		System.out.println("1:parse source code");
		System.out.println("2:train kmeans model");
		System.out.println("3:kmeans diagnose");
		System.out.println("4:train pca model");
		System.out.println("5:pca diagnose");
		while(in.hasNext()){
			int choose = in.nextInt();
			switch (choose) {
			case 1:
				String codepath = "d:/spark-1.5.2/core";
				String orioutput = "d:/oriSchemaList";
				String regularoutput = "d:/regularSchemaList";
				GetSourceSchema.getSchema(codepath, orioutput, regularoutput);
				break;
			case 2:
				for (String type : typearr) {
					MatchLogSchema.init();
					MatchLogSchema.matchSchema(type,flag_tfidf,regpath, dictpath, trainlogpath, trainfeapath);
					Kmeans.train(kmeansKmap.get(type),type,trainfeapath);
					DecisionTree.train(type);
					System.out.println("please label the anomaly class,split by ',':");
					String[] badarrStrings = in.next().split(",");
					//序列化模型存储
					StoreUtils.putbadarr(type, "kmeans", badarrStrings);
					System.out.println("finish training kmeans model by "+type);
					System.out.println("press any key to go on");
					in.next();
				}
				break;
			case 3:
				for (String type : typearr) {
					MatchLogSchema.init();
					MatchLogSchema.matchSchema(type,flag_tfidf,regpath, dictpath, testlogpath, testfeapath);
					HashSet<Integer> badset = StoreUtils.getbadarr(type, "kmeans");
					Kmeans.test(badset,type);
					System.out.println("是否查看异常决策树分析（输入1为是，0为否）：");
					if (in.nextInt()==1) {
						DecisionTree.train(type);
					}
					System.out.println("finish test kmeans model by "+type);
					System.out.println("press any key to go on");
					in.next();
				}
				break;
			case 4:
				for (String type : typearr) {
					MatchLogSchema.init();
					MatchLogSchema.matchSchema(type,flag_tfidf,regpath, dictpath, trainlogpath, trainfeapath);
					PCA.train(type, trainfeapath);
				}
				break;
			case 5:
				for (String type : typearr) {
					MatchLogSchema.init();
					MatchLogSchema.matchSchema(type,flag_tfidf,regpath, dictpath, testlogpath, testfeapath);
					PCA.test(type);
					break;
				}
				break;
			default:
				System.out.println("结束");
				return;
			}
		}
	}
}
