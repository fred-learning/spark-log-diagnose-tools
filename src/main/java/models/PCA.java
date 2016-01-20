package models;

import db.DBInstance;
import db.metadata.BlockIdFeaData;
import db.metadata.ModelData;
import db.metadata.TaskIdFeaData;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.hadoop.mapred.TaskID;
import parseLog.FileUtils;
import utils.Config;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class PCA {
    private static Config config = Config.getInstance();
	public void trainBlockFea() throws Exception {
		Instances data = new DataInit().getAllBlockIdData();
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
		double Qa = model.PCAModel.computeQa(values, k, 0.99);
        //模型存储
        PCAModel pcaModel = new PCAModel(Mppt, Qa);
        String[] badlist = {"1"};
        ModelData modelData = new ModelData(config.getBlockTag(),"pca",pcaModel,badlist);
        DBInstance modeldb = new DBInstance("model");
        modeldb.saveModel(modelData);
        modeldb.close();

        //更新类别
        ArrayList<Integer> labellist = new ArrayList<Integer>();
        for(int i=0;i<dis.length;i++){
            if (dis[i]>=Qa)
                labellist.add(1);
            else
                labellist.add(0);
        }
        DBInstance db = new DBInstance("blockfea");
        ArrayList<BlockIdFeaData> datalist = db.getAllBlockFeaData();
        if (datalist.size()==labellist.size()){
            db.updateBlockFeaData(datalist, labellist ,null);
        }
        db.close();
	}

	public static void testBlockFea(String appid) throws Exception {
        //判断是否已test过
        DBInstance db = new DBInstance("blockfea");
        if (db.checkDoneOneKmeansLabel(appid)){
            db.close();
            return;
        }
        db.close();
        //反序列化模型数据
        db = new DBInstance("model");
        ModelData modelData = db.getModel(config.getBlockTag(),"pca");
        db.close();
        PCAModel model = (PCAModel)modelData.getModel();
		RealMatrix Mppt = model.Mppt;
		double Qa = model.Qa;
		// 测试
		Instances testdata = new DataInit().getOneBlockIdData(appid);
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
        ArrayList<Integer> labellist = new ArrayList<Integer>();
        for (int i = 0; i < N; i++) {
			double tmp = 0;
			for (int j = 0; j < D; j++) {
				tmp += spe[i][j] * spe[i][j];
			}
			dis[i] = tmp;
            if (dis[i]>=Qa)
                labellist.add(1);
            else
                labellist.add(0);
		}
        db = new DBInstance("blockfea");
        ArrayList<BlockIdFeaData> datalist = db.getOneBlockFeaData(appid);
        db.updateBlockFeaData(datalist,labellist,null);
        db.close();
	}

    public void trainTaskFea() throws Exception {
        Instances data = new DataInit().getAllTaskIdData();
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
        double Qa = model.PCAModel.computeQa(values, k, 0.99);
        //模型存储
        PCAModel pcaModel = new PCAModel(Mppt, Qa);
        String[] badlist = {"1"};
        ModelData modelData = new ModelData(config.getTaskTag(),"pca",pcaModel,badlist);
        DBInstance modeldb = new DBInstance("model");
        modeldb.saveModel(modelData);
        modeldb.close();

        //更新类别
        ArrayList<Integer> labellist = new ArrayList<Integer>();
        for(int i=0;i<dis.length;i++){
            if (dis[i]>=Qa)
                labellist.add(1);
            else
                labellist.add(0);
        }
        DBInstance db = new DBInstance("taskfea");
        ArrayList<TaskIdFeaData> datalist = db.getAllTaskFeaData();
        if (datalist.size()==labellist.size()){
            db.updateTaskFeaData(datalist, labellist, null);
        }
        db.close();
    }

    public static void testTaskFea(String appid) throws Exception {
        //判断是否已test过
        DBInstance db = new DBInstance("taskfea");
        if (db.checkDoneOneKmeansLabel(appid)){
            db.close();
            return;
        }
        db.close();
        //反序列化模型数据
        db = new DBInstance("model");
        ModelData modelData = db.getModel(config.getTaskTag(),"pca");
        PCAModel model = (PCAModel)modelData.getModel();
        RealMatrix Mppt = model.Mppt;
        double Qa = model.Qa;
        // 测试
        Instances testdata = new DataInit().getOneTaskIdData(appid);
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
        ArrayList<Integer> labellist = new ArrayList<Integer>();
        for (int i = 0; i < N; i++) {
            double tmp = 0;
            for (int j = 0; j < D; j++) {
                tmp += spe[i][j] * spe[i][j];
            }
            dis[i] = tmp;
            if (dis[i]>=Qa)
                labellist.add(1);
            else
                labellist.add(0);
        }
        db = new DBInstance("taskfea");
        ArrayList<TaskIdFeaData> datalist = db.getOneTaskFeaData(appid);
        db.updateTaskFeaData(datalist,labellist,null);
        db.close();
    }
}
