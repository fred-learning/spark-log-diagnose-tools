package models;

import weka.core.Instance;

/**
 * Created by Administrator on 2016/1/18.
 */
public class Zeus {
    public static int tag_kmeans_task(Instance instance) {
        double[] arr = instance.toDoubleArray();
        //storage.MemoryStore13-6  storage.MemoryStore11-9
        if (arr[6]>0&&arr[9]>1)
            return 8;
        if (arr[6]>0&&arr[9]<=1)
            return 7;
        //collection.ExternalAppendOnlyMap1-11
        if (arr[11]>1)
            return 13;
        //storage.BlockManager40-17
        if (arr[17]>2)
            return 12;
        return -1;
    }
    public static int tag_kmeans_block(Instance instance) {
        double[] arr = instance.toDoubleArray();
        //storage.BlockManager26-2
        if (arr[2]>4)
            return 0;
        //storage.MemoryStore13-7
        if (arr[7]>0)
            return 7;
        return -1;
    }
}
