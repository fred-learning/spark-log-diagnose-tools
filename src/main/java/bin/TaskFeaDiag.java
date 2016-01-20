package bin;

import models.Kmeans;
import models.PCA;
import parseLog.ExtractTaskFea;
import utils.Config;

/**
 * Created by Administrator on 2015/12/21.
 */
public class TaskFeaDiag {
//    private static Config config = Config.getInstance();
    public static void main(String[] args) throws Exception {
        Config config;
        if (args.length==1){
            System.out.println("use the outsize configuration file:"+args[0]);
            config = Config.getInstance(args[0]);
        }else {
            System.out.println("use the default configuration file");
            config = Config.getInstance();
        }

//        System.out.println("extract fea!!");
//        new ExtractTaskFea().DoExtract(config.getTrainLogPath(),true);
//
//        System.out.println("train!!");
//        new Kmeans().trainTaskFea(Integer.valueOf(config.getKmeansTask()));
        new Kmeans().testTaskFea("x_kmeans_to_cache_noenough_memory.log");
//        new PCA().trainTaskFea();
//        new PCA().testTaskFea("x_kmeans_to_cache_noenough_memory.log");
    }
}
