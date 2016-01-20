package db.metadata;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/12/15.
 */
public class DiagnoseResData {
    private String clusterid;
    private String appid;
    private String stageid;
    private String locate;
    private int goodcnt_task;
    private int badcnt_task;
    private int goodcnt_block;
    private int badcnt_block;

    private HashMap<String,String> goodmap_task;//<taskid,label|analysis>
    private HashMap<String,String> badmap_task;

    private HashMap<String,String> goodmap_block;//<taskid,label|analysis>
    private HashMap<String,String> badmap_block;

    public DiagnoseResData(String clusterid, String appid, String stageid, String locate) {
        this.clusterid = clusterid;
        this.appid = appid;
        this.stageid = stageid;
        this.locate = locate;
        goodmap_task = new HashMap<String, String>();
        badmap_task = new HashMap<String, String>();
        goodmap_block = new HashMap<String, String>();
        badmap_block = new HashMap<String, String>();
        goodcnt_task=0;
        badcnt_task=0;
        goodcnt_block=0;
        badcnt_block=0;
    }

    public void updateTaskGoodmap(String taskid,String analysis){
        goodmap_task.put(taskid,analysis);
    }
    public void  updateTaskBadmap(String taskid,String analysis){
        badmap_task.put(taskid,analysis);
    }
    public void updateTaskCnt(){
        goodcnt_task=goodmap_task.size();
        badcnt_task=badmap_task.size();
    }
    public void updateBlockGoodmap(String taskid,String analysis){
        goodmap_block.put(taskid,analysis);
    }
    public void  updateBlockBadmap(String taskid,String analysis){
        badmap_block.put(taskid,analysis);
    }
    public void updatBlockCnt(){
        goodcnt_block=goodmap_block.size();
        badcnt_block=badmap_block.size();
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

    public String getLocate() {
        return locate;
    }

    public void setLocate(String locate) {
        this.locate = locate;
    }

    public int getGoodcnt_task() {
        return goodcnt_task;
    }

    public void setGoodcnt_task(int goodcnt_task) {
        this.goodcnt_task = goodcnt_task;
    }

    public int getBadcnt_task() {
        return badcnt_task;
    }

    public void setBadcnt_task(int badcnt_task) {
        this.badcnt_task = badcnt_task;
    }

    public int getGoodcnt_block() {
        return goodcnt_block;
    }

    public void setGoodcnt_block(int goodcnt_block) {
        this.goodcnt_block = goodcnt_block;
    }

    public int getBadcnt_block() {
        return badcnt_block;
    }

    public void setBadcnt_block(int badcnt_block) {
        this.badcnt_block = badcnt_block;
    }

    public HashMap<String, String> getGoodmap_task() {
        return goodmap_task;
    }

    public void setGoodmap_task(HashMap<String, String> goodmap_task) {
        this.goodmap_task = goodmap_task;
    }

    public HashMap<String, String> getBadmap_task() {
        return badmap_task;
    }

    public void setBadmap_task(HashMap<String, String> badmap_task) {
        this.badmap_task = badmap_task;
    }

    public HashMap<String, String> getGoodmap_block() {
        return goodmap_block;
    }

    public void setGoodmap_block(HashMap<String, String> goodmap_block) {
        this.goodmap_block = goodmap_block;
    }

    public HashMap<String, String> getBadmap_block() {
        return badmap_block;
    }

    public void setBadmap_block(HashMap<String, String> badmap_block) {
        this.badmap_block = badmap_block;
    }
}
