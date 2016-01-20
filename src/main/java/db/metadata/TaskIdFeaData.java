package db.metadata;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/12/15.
 */
public class TaskIdFeaData {
    private String clusterid;
    private String appid;
    private String stageid;
    private String taskid;
    private HashMap<String,Integer> map;


    public TaskIdFeaData(String clusterid, String appid, String stageid, String taskid, HashMap<String, Integer> map) {
        this.clusterid = clusterid;
        this.appid = appid;
        this.stageid = stageid;
        this.taskid = taskid;
        this.map = map;
    }

    public void print(){
        System.out.println("print one taskidfeadata");
        System.out.println(clusterid);
        System.out.println(appid);
        System.out.println(stageid);
        System.out.println(taskid);
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

    public String getStageid() {
        return stageid;
    }

    public void setStageid(String stageid) {
        this.stageid = stageid;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public HashMap<String, Integer> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Integer> map) {
        this.map = map;
    }

}
