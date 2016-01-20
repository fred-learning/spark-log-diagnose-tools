package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.Covariance;

import parseLog.FileUtils;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;

public class PCA {
	public static void train(String type, String filepath) throws Exception {
		String inputfile = filepath + type;
		BufferedReader datafile = new BufferedReader(new FileReader(inputfile));
		Instances data = new Instances(datafile);
		// System.out.println(data.get(0).numAttributes());
		// PrincipalComponents pca = new PrincipalComponents();
		// pca.buildEvaluator(data);
		// double[] values = pca.getEigenValues();
		// double[][] vectors = pca.getUnsortedEigenVectors();
		// 求spe
		int D = data.numAttributes();
		int N = data.size();
		double[][] yarray = new double[D][N];
		for (int i = 0; i < D; i++) {
			yarray[i] = data.attributeToDoubleArray(i);
		}
		// math matrix ops
		RealMatrix My = MatrixUtils.createRealMatrix(yarray);// dxn
		RealMatrix cov = new Covariance(My.transpose()).getCovarianceMatrix();
		EigenDecomposition dec = new EigenDecomposition(cov);
		double[] values = dec.getRealEigenvalues();
		double sum = 0;
		for (double e : values) {
			sum += e;
		}
		int k = 0;
		double realsum = 0;
		for (double e : values) {
			realsum += e;
			k++;
			if (realsum > sum * 0.99) {
				break;
			}
		}
		double[][] vectors = new double[k][D];
		for (int i = 0; i < k; i++) {
			RealVector vector = dec.getEigenvector(i);
			vectors[i] = vector.toArray();
		}
		RealMatrix Mpt = MatrixUtils.createRealMatrix(vectors);// kxd
		RealMatrix Mp = Mpt.transpose();// dxk
		RealMatrix Mppt = Mp.multiply(Mpt);// dxd
		RealMatrix Myy = Mppt.multiply(My);// dxn
		RealMatrix Mya = My.subtract(Myy).transpose();// nxd
		double[][] spe = Mya.getData();
		double[] dis = new double[N];
		for (int i = 0; i < N; i++) {
			double tmp = 0;
			for (int j = 0; j < D; j++) {
				tmp += spe[i][j] * spe[i][j];
			}
			dis[i] = tmp;
		}

		HashMap<String, Double> dismap = new HashMap<String, Double>();
		HashMap<String, Instance> feamap = new HashMap<String, Instance>();
		// 读取事件列表
		ArrayList<String> eventlist = FileUtils.getEventList(type);
		for (int i = 0; i < N; i++) {
			dismap.put(eventlist.get(i), dis[i]);
			feamap.put(eventlist.get(i), data.get(i));
		}
		List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(
				dismap.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Double>>() {
			// 降序排序
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}

		});

		double Qa = PCAModel.computeQa(values, k, 0.99);

		// 读取typeset
		String typesetpath = "data/typeset_" + type;
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				typesetpath));
		String[] typesetarray = (String[]) in.readObject(); // 读取customer对象
		in.close();
		// 保存异常结果
		FileWriter fw_res = new FileWriter("E:/orifeaturedir/res_"+type+".xls");
		String title = "schemaid\tdis\t";
		for (String simlog : typesetarray) {
			title += simlog + "\t";
		}
		fw_res.write(title + "\n");

		for (Entry<String, Double> mapping : list) {
//			System.out.println(mapping.getKey() + ":" + mapping.getValue());
			String res = feamap.get(mapping.getKey()).toString();
			res = res.replaceAll(",", "\t");
			fw_res.write(mapping.getKey()+"\t"+ mapping.getValue()+ "\t" +res+"\n");
			if (mapping.getValue() < Qa) {
				break;
			}
		}
		fw_res.close();
		// 序列化模型存储
		PCAModel pcaModel = new PCAModel(Mppt, Qa);
		String modelpath = "data/model_pca_" + type;
		if (new File(modelpath).exists()) {
			new File(modelpath).delete();
		}
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
				modelpath));
		out.writeObject(pcaModel); // 写入customer对象
		out.close();
		System.out.println("pca train finished");
	}

	public static void test(String type) throws Exception {
		// 反序列化模型数据
		String modelpath = "data/model_pca_" + type;
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				modelpath));
		PCAModel model = (PCAModel) in.readObject(); // 读取customer对象
		in.close();
		RealMatrix Mppt = model.Mppt;
		double Qa = model.Qa;
		// 读取事件列表
		ArrayList<String> eventlist = FileUtils.getEventList(type);
		// 测试
		String testpath = "data/feature_test_" + type;
		BufferedReader testfile = new BufferedReader(new FileReader(testpath));
		Instances testdata = new Instances(testfile);
		int D = testdata.numAttributes();
		int N = testdata.size();
		double[][] yarray = new double[D][N];
		for (int i = 0; i < D; i++) {
			yarray[i] = testdata.attributeToDoubleArray(i);
		}
		// math matrix ops
		RealMatrix My = MatrixUtils.createRealMatrix(yarray);// dxn
		RealMatrix Myy = Mppt.multiply(My);// dxn
		RealMatrix Mya = My.subtract(Myy).transpose();// nxd
		double[][] spe = Mya.getData();
		double[] dis = new double[N];
		for (int i = 0; i < N; i++) {
			double tmp = 0;
			for (int j = 0; j < D; j++) {
				tmp += spe[i][j] * spe[i][j];
			}
			dis[i] = tmp;
			if (dis[i] > Qa) {
				System.out.println("found anomaly point!");
				System.out.println(eventlist.get(i));
				String[] featarr = eventlist.get(i).split(",");
			}
		}
	}

	public static void main(String[] args) throws Exception {
		train("tid", "data/feature_");
	}
}
