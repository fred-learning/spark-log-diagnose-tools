package models;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.io.Serializable;

public class PCAModel implements Serializable {

	private static final long serialVersionUID = 1L;
	RealMatrix Mppt;//p*pt矩阵
	double Qa;
	public PCAModel(RealMatrix mppt, double qa){
		Mppt = mppt;
		Qa  = qa;
	}
	//计算q-statistic spe阀值
	public static double computeQa(double[] values,int k,double alpha) {
		double sum = 0;
		for (double d : values) {
			sum+=d;
		}
		double sd= new StandardDeviation().evaluate(values);
		double mean = sum/values.length;
		
		
		double theta1 = 0,theta2 = 0,theta3 = 0,h0,ca;
		for (int i = k; i < values.length; i++) {
			theta1+=values[i];
			theta2+=Math.pow(values[i], 2);
			theta3+=Math.pow(values[i], 3);
		}
		h0 = 1-(2*theta1*theta3/(3*theta2*theta2));

		NormalDistribution d = new NormalDistribution(0, 1);
		ca = d.inverseCumulativeProbability(alpha);
		double inner = h0*ca*Math.sqrt(2*theta2)/theta1+1+theta2*h0*(h0-1)/Math.pow(theta1, 2);
		double Qa = theta1*Math.pow(inner, 1/h0);
		return Qa;
	}
}
