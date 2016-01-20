package bin;

import models.DecisionTree;
import utils.Config;

/**
 * Created by Administrator on 2015/12/23.
 */
public class TreeBuilder {
    public static void main(String[] args) throws Exception {
        Config config;
        if (args.length==1){
            System.out.println("use the outsize configuration file:"+args[0]);
            config = Config.getInstance(args[0]);
        }else {
            System.out.println("use the default configuration file");
            config = Config.getInstance();
        }
//        DecisionTree.trainBlock4PCATree();
        DecisionTree.trainBlock4KmeansTree();
        DecisionTree.trainTask4KmeansTree();
//        DecisionTree.trainTask4PCATree();
    }
}
