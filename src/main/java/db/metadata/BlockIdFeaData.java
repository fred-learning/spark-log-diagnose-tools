package db.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Administrator on 2015/12/15.
 */
public class BlockIdFeaData {
    private String clusterid;
    private String appid;
    private HashSet<String> stageid;
    private String blockid;
    private HashMap<String,Integer> map;

    public BlockIdFeaData(String clusterid, String appid, String blockid) {
        this.clusterid = clusterid;
        this.appid = appid;
        this.stageid = new HashSet<String>();
        this.blockid = blockid;
        this.map = new HashMap<String, Integer>();
    }

    public void addFea(String stage,String fea){
        stageid.add(stage);
        if (map.containsKey(fea))
            map.put(fea,map.get(fea)+1);
        else
            map.put(fea,1);
    }

    public void print(){
        System.out.println("print the blcokidfeadata");
        System.out.println(clusterid);
        System.out.println(appid);
        System.out.println(stageid.toString());
        System.out.println(blockid);
        System.out.println(map.toString());
    }
    public String getClusterid() {
        return clusterid;
    }

    public void setClusterid(String clusterid) {
        this.clusterid = clusterid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public HashSet<String> getStageid() {
        return stageid;
    }

    public void setStageid(HashSet<String> stageid) {
        this.stageid = stageid;
    }

    public String getBlockid() {
        return blockid;
    }

    public void setBlockid(String blockid) {
        this.blockid = blockid;
    }

    public HashMap<String, Integer> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Integer> map) {
        this.map = map;
    }

}
