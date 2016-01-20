package models;

import db.metadata.BlockIdFeaData;
import utils.Config;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class DecisionTree {
    private static Config config = Config.getInstance();

    //根据特征 label训练决策树
	public static void trainBlock4PCATree() throws Exception {
        //merge them
        Instances data = new DataInit().getAllBlockIdData4PCATree();
        // set class attribute
        data.setClassIndex(data.numAttributes() - 1);
        // make tree
        J48 tree = new J48();
        String[] options = {};//-U
        tree.setOptions(options);
        tree.buildClassifier(data);

        // print tree
//		System.out.println(tree);
        // print tree
        System.out.println(tree.graph());
        //保存graph的dot文件
        FileWriter fw = new FileWriter(new File(config.getTreePath()+"treeBlockPCA.dot"));
        fw.write(tree.graph());
        fw.close();

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
    public static void trainTask4PCATree() throws Exception {
        //merge them
        Instances data = new DataInit().getAllTaskIdData4PCATree();
        // set class attribute
        data.setClassIndex(data.numAttributes() - 1);
        // make tree
        J48 tree = new J48();
        String[] options = {};//-U
        tree.setOptions(options);
        tree.buildClassifier(data);

        // print tree
//		System.out.println(tree);
        // print tree
        System.out.println(tree.graph());
        //保存graph的dot文件
        FileWriter fw = new FileWriter(new File(config.getTreePath()+"treeTaskPCA.dot"));
        fw.write(tree.graph());
        fw.close();

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

    public static void trainBlock4KmeansTree() throws Exception {
        //merge them
        Instances data = new DataInit().getAllBlockIdData4KmeansTree();
        // set class attribute
        data.setClassIndex(data.numAttributes() - 1);
        // make tree
        J48 tree = new J48();
        String[] options = {};//-U
        tree.setOptions(options);
        tree.buildClassifier(data);

        // print tree
//		System.out.println(tree);
        // print tree
        System.out.println(tree.graph());
        //保存graph的dot文件
        FileWriter fw = new FileWriter(new File(config.getTreePath()+"treeBlockKM.dot"));
        fw.write(tree.graph());
        fw.close();

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
    public static void trainTask4KmeansTree() throws Exception {
        //merge them
        Instances data = new DataInit().getAllTaskIdData4KmeansTree();
        // set class attribute
        data.setClassIndex(data.numAttributes() - 1);
        // make tree
        J48 tree = new J48();
        String[] options = {};//-U
        tree.setOptions(options);
        tree.buildClassifier(data);

        // print tree
		System.out.println(tree.graph());
        //保存graph的dot文件
        FileWriter fw = new FileWriter(new File(config.getTreePath()+"treeTaskKM.dot"));
        fw.write(tree.graph());
        fw.close();

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
