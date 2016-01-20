package model;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JFrame;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

public class DecisionTree {
	//根据特征 label训练决策树
	public static void train(String type) throws Exception {
		// get the feature
		String feafilepath = "data/feature_"+type;
		BufferedReader feafile = new BufferedReader(new FileReader(feafilepath));
		Instances feadata = new Instances(feafile);
		//get the label
		String labelfilepath = "data/label_kmeans_"+type;
		BufferedReader lablefile = new BufferedReader(new FileReader(labelfilepath));
		Instances labeldata = new Instances(lablefile);
		//merge them
		Instances data = Instances.mergeInstances(feadata, labeldata);
		// set class attribute
		data.setClassIndex(data.numAttributes() - 1);
		// make tree
		J48 tree = new J48();
		String[] options = {};//-U
		tree.setOptions(options);
		tree.buildClassifier(data);

		// print tree
//		System.out.println(tree);

		// display classifier
		final JFrame jf = new JFrame("Weka Classifier Tree Visualizer: J48");
		jf.setSize(1200, 800);
		jf.getContentPane().setLayout(new BorderLayout());
		TreeVisualizer tv = new TreeVisualizer(null, tree.graph(),
				new PlaceNode2());
		jf.getContentPane().add(tv, BorderLayout.CENTER);
		jf.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				jf.dispose();
			}
		});

		jf.setVisible(true);
		tv.fitToScreen();
	}
}
