package models;

import weka.core.Instance;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/1/18.
 */
public class Zeus {
    public static HashMap<Integer,String> getTaskMap(){
        HashMap<Integer,String> taskmap = new HashMap<Integer, String>();
        taskmap.put(1,"1:RDD过大，无足够内存来缓存");
        taskmap.put(2,"2:RDD过大，无足够内存来缓存");
        taskmap.put(3,"3:Shuffle异常，频繁写到硬盘");
        taskmap.put(4,"4:内存不足，为缓存目标RDD而丢掉其他缓存RDD");
        taskmap.put(5,"5:正常");
        return taskmap;
    }

    public static HashMap<Integer,String> getBlockMap(){
        HashMap<Integer,String> blockmap = new HashMap<Integer, String>();
        blockmap.put(1,"1:未合理Cache RDD，频繁远程读取");
        blockmap.put(2,"2:内存不足，为缓存目标RDD而丢掉其他缓存RDD");
        blockmap.put(3,"3:RDD过大，无足够内存来缓存");
        blockmap.put(4,"4:RDD过大，无足够内存来缓存");
        blockmap.put(5,"5:正常");
        return blockmap;
    }

    public static int tag_kmeans_task(Instance instance) {
        double[] arr = instance.toDoubleArray();
        //storage.MemoryStore13-6  storage.MemoryStore11-9
        if (arr[6]>0&&arr[9]>0)
            return 1;
        if (arr[6]>0&&arr[9]<=0)
            return 2;
        //collection.ExternalAppendOnlyMap1-11
        if (arr[11]>0)
            return 3;
        //storage.BlockManager40-17 storage.MemoryStore10-8
        if (arr[17]>0&&arr[8]>0)
            return 4;
        return 5;
    }
    public static int tag_kmeans_block(Instance instance) {
        double[] arr = instance.toDoubleArray();
        //storage.BlockManager26-2
        if (arr[2]>5)
            return 1;
        //storage.BlockManager40-11
        if(arr[11]>0)
            return 2;
        //storage.MemoryStore13-7 storage.MemoryStore11-9
        if (arr[7]>0&&arr[9]>0)
            return 3;
        if (arr[7]>0&&arr[9]<=0)
            return 4;
        return 5;
    }
}
