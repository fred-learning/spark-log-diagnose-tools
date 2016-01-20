package bin;

import models.Kmeans;
import models.PCA;
import parseLog.ExtractBlockFea;
import utils.Config;

/**
 * Created by Administrator on 2015/12/22.
 */
public class BlockFeaDiag {
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

        System.out.println("extract fea!!");
        new ExtractBlockFea().DoExtract(config.getTrainLogPath(),true);

        System.out.println("train!!");
        new Kmeans().trainBlockFea(Integer.valueOf(config.getKmeansBlock()));
//        new Kmeans().testBlockFea("x_kmeans_to_cache_noenough_memory.log");
//        new PCA().trainBlockFea();
//        new PCA().testBlockFea("x_kmeans_to_cache_noenough_memory.log");
    }
}
