package db;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import db.metadata.*;
import models.DecisionTree;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import utils.Config;

public class DBInstance {
    private static Config conf = Config.getInstance();
    private static Gson gson = new Gson();

    private String connectHost;
    private String databaseName;
    private String collectionName;
    private MongoClient mongoClient;
    private MongoCollection<Document> mongoCollection;

    private static String switchCName(String cname) {
        if (cname.equals("blockfea"))
            return conf.getMongoBlockFeaCollection();
        if (cname.equals("taskfea"))
            return conf.getMongoTaskFeaCollection();
        if (cname.equals("typeset"))
            return conf.getMongoTypesetCollection();
        if (cname.equals("diagnose"))
            return conf.getMongoDiagnoseresCollection();
        if (cname.equals("model"))
            return conf.getMongoModelCollection();
        return "nullCollection";
    }

    public DBInstance(String cName) {
        connectHost = conf.getMongoIP();
        databaseName = conf.getMongoDBName();
        collectionName = switchCName(cName);
        mongoClient = new MongoClient(connectHost);
        mongoCollection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
    }

    public DBInstance(String host, String dbName, String cName) {
        connectHost = host;
        databaseName = dbName;
        collectionName = cName;
        mongoClient = new MongoClient(host);
        mongoCollection = mongoClient.getDatabase(dbName).getCollection(cName);
    }

    //get tree data
    public ArrayList<TreeData> getAllBlockFeaData4Tree() {
        FindIterable<Document> docs = mongoCollection.find();
        ArrayList<TreeData> reslist = new ArrayList<TreeData>();
        for(Document doc:docs){
            BlockIdFeaData onedata = gson.fromJson(doc.getString("fulldata"), BlockIdFeaData.class);
            int pcalabel = doc.containsKey("pcalabel")?doc.getInteger("pcalabel"):-1;
            int kmeanslabel = doc.containsKey("kmeanslabel")?doc.getInteger("kmeanslabel"):-1;
            reslist.add(new TreeData(onedata,pcalabel,kmeanslabel));
        }
        return reslist;
    }

    public ArrayList<TreeData> getAllTaskFeaData4Tree() {
        FindIterable<Document> docs = mongoCollection.find();
        ArrayList<TreeData> reslist = new ArrayList<TreeData>();
        for(Document doc:docs){
            TaskIdFeaData onedata = gson.fromJson(doc.getString("fulldata"), TaskIdFeaData.class);
            int pcalabel = doc.containsKey("pcalabel")?doc.getInteger("pcalabel"):-1;
            int kmeanslabel = doc.containsKey("kmeanslabel")?doc.getInteger("kmeanslabel"):-1;
            reslist.add(new TreeData(onedata,pcalabel,kmeanslabel));
        }
        return reslist;
    }

    public ArrayList<TreeData> getOneBlockFeaData4Tree(String appid) {
        FindIterable<Document> docs = mongoCollection.find(new Document("appid", appid));
        ArrayList<TreeData> reslist = new ArrayList<TreeData>();
        for(Document doc:docs){
            BlockIdFeaData onedata = gson.fromJson(doc.getString("fulldata"), BlockIdFeaData.class);
            int pcalabel = doc.containsKey("pcalabel")?doc.getInteger("pcalabel"):-1;
            int kmeanslabel = doc.containsKey("kmeanslabel")?doc.getInteger("kmeanslabel"):-1;
            reslist.add(new TreeData(onedata,pcalabel,kmeanslabel));
        }
        return reslist;
    }

    public ArrayList<TreeData> getOneTaskFeaData4Tree(String appid) {
        FindIterable<Document> docs = mongoCollection.find(new Document("appid", appid));
        ArrayList<TreeData> reslist = new ArrayList<TreeData>();
        for(Document doc:docs){
            TaskIdFeaData onedata = gson.fromJson(doc.getString("fulldata"), TaskIdFeaData.class);
            int pcalabel = doc.containsKey("pcalabel")?doc.getInteger("pcalabel"):-1;
            int kmeanslabel = doc.containsKey("kmeanslabel")?doc.getInteger("kmeanslabel"):-1;
            reslist.add(new TreeData(onedata,pcalabel,kmeanslabel));
        }
        return reslist;
    }

    //check if label has been tagged
    public Boolean checkDoneOneKmeansLabel(String appid){
        FindIterable<Document> docs = mongoCollection.find(new Document("appid", appid));
        ArrayList<BlockIdFeaData> reslist = new ArrayList<BlockIdFeaData>();
        Document doc = docs.first();
        if (doc!=null&&doc.containsKey("kmeanslabel"))
            return true;
        return false;
    }
    public Boolean checkDoneOnePCALabel(String appid){
        FindIterable<Document> docs = mongoCollection.find(new Document("appid", appid));
        ArrayList<BlockIdFeaData> reslist = new ArrayList<BlockIdFeaData>();
        Document doc = docs.first();
        if (doc!=null&&doc.containsKey("pcalabel"))
            return true;
        return false;
    }

    //check app fea exist
    public Boolean checkOneFeaData(String appid) {
        FindIterable<Document> docs = mongoCollection.find(new Document("appid", appid));
        ArrayList<BlockIdFeaData> reslist = new ArrayList<BlockIdFeaData>();
        Document doc = docs.first();
        if (doc!=null)
            return true;
        return false;
    }
    //save and get of fea
    public ArrayList<BlockIdFeaData> getOneBlockFeaData(String appid) {
        FindIterable<Document> docs = mongoCollection.find(new Document("appid", appid));
        ArrayList<BlockIdFeaData> reslist = new ArrayList<BlockIdFeaData>();
        for(Document doc:docs){
            BlockIdFeaData onedata = gson.fromJson(doc.getString("fulldata"), BlockIdFeaData.class);
            reslist.add(onedata);
        }
        return reslist;
    }

    public ArrayList<TaskIdFeaData> getOneTaskFeaData(String appid) {
        FindIterable<Document> docs = mongoCollection.find(new Document("appid", appid));
        ArrayList<TaskIdFeaData> reslist = new ArrayList<TaskIdFeaData>();
        for(Document doc:docs){
            TaskIdFeaData onedata = gson.fromJson(doc.getString("fulldata"), TaskIdFeaData.class);
            reslist.add(onedata);
        }
        return reslist;
    }


    public ArrayList<BlockIdFeaData> getAllBlockFeaData() {
        FindIterable<Document> docs = mongoCollection.find();
        ArrayList<BlockIdFeaData> reslist = new ArrayList<BlockIdFeaData>();
        for(Document doc:docs){
            BlockIdFeaData onedata = gson.fromJson(doc.getString("fulldata"), BlockIdFeaData.class);
            reslist.add(onedata);
        }
        return reslist;
    }

    public ArrayList<TaskIdFeaData> getAllTaskFeaData() {
        FindIterable<Document> docs = mongoCollection.find();
        ArrayList<TaskIdFeaData> reslist = new ArrayList<TaskIdFeaData>();
        for(Document doc:docs){
            TaskIdFeaData onedata = gson.fromJson(doc.getString("fulldata"), TaskIdFeaData.class);
            reslist.add(onedata);
        }
        return reslist;
    }

    public void updateTaskFeaData(ArrayList<TaskIdFeaData> datalist,ArrayList<Integer> pcalabel,ArrayList<Integer> kmeanslabel){
        if (pcalabel==null&&kmeanslabel==null)
            return;
        if (pcalabel!=null){
            for(int i=0;i<datalist.size();i++){
                TaskIdFeaData taskIdFeaData = datalist.get(i);
                int label = pcalabel.get(i);
                Document doc = new Document().append("clusterid", taskIdFeaData.getClusterid())
                        .append("appid", taskIdFeaData.getAppid()).append("taskid",taskIdFeaData.getTaskid());
                mongoCollection.updateMany(doc,new Document("$set",new Document("pcalabel",label)));
            }
        }else {
            for(int i=0;i<datalist.size();i++){
                TaskIdFeaData taskIdFeaData = datalist.get(i);
                int label = kmeanslabel.get(i);
                Document doc = new Document().append("clusterid", taskIdFeaData.getClusterid())
                        .append("appid", taskIdFeaData.getAppid()).append("taskid",taskIdFeaData.getTaskid());
                mongoCollection.updateMany(doc,new Document("$set",new Document("kmeanslabel",label)));
            }
        }
    }
    public void updateBlockFeaData(ArrayList<BlockIdFeaData> datalist,ArrayList<Integer> pcalabel,ArrayList<Integer> kmeanslabel){
        if (pcalabel==null&&kmeanslabel==null)
            return;
        if (pcalabel!=null){
            for(int i=0;i<datalist.size();i++){
                BlockIdFeaData blockIdFeaData = datalist.get(i);
                int label = pcalabel.get(i);
                Document doc = new Document().append("clusterid", blockIdFeaData.getClusterid())
                        .append("appid", blockIdFeaData.getAppid()).append("blockid",blockIdFeaData.getBlockid());
                mongoCollection.updateMany(doc,new Document("$set",new Document("pcalabel",label)));
            }
        }else {
            for(int i=0;i<datalist.size();i++){
                BlockIdFeaData blockIdFeaData = datalist.get(i);
                int label = kmeanslabel.get(i);
                Document doc = new Document().append("clusterid", blockIdFeaData.getClusterid())
                        .append("appid", blockIdFeaData.getAppid()).append("blockid",blockIdFeaData.getBlockid());
                mongoCollection.updateMany(doc,new Document("$set",new Document("kmeanslabel",label)));
            }
        }
    }

    public void saveBlockFeaData(BlockIdFeaData feadata) throws IOException {
        Document doc = new Document()
                .append("clusterid", feadata.getClusterid())
                .append("appid", feadata.getAppid())
                .append("blockid",feadata.getBlockid())
                .append("fulldata", gson.toJson(feadata));
        getMongoCollection().insertOne(doc);
    }

    public void saveTaskFeaData(TaskIdFeaData feadata) throws IOException {
        Document doc = new Document()
                .append("clusterid", feadata.getClusterid())
                .append("appid", feadata.getAppid())
                .append("taskid",feadata.getTaskid())
                .append("fulldata", gson.toJson(feadata));
        getMongoCollection().insertOne(doc);
    }


    //save and get of typeset
    public void saveTypeset(TypeSetData typeSetData) throws IOException {
        String fealevel = typeSetData.getFealevel();
        if (getTypeset(fealevel)!=null){
            getMongoCollection().deleteMany(new Document("fealevel", fealevel));
        }
        Document doc = new Document()
                .append("fealevel", typeSetData.getFealevel())
                .append("fulldata", gson.toJson(typeSetData));
        getMongoCollection().insertOne(doc);
    }
    public TypeSetData getTypeset(String type) {
        Document searchDoc = new Document().append("fealevel", type);
        FindIterable<Document> docs = mongoCollection.find(searchDoc);
        Document doc = docs.first();
        if (doc != null) {
            TypeSetData fulldata = gson.fromJson(doc.getString("fulldata"), TypeSetData.class);
            return fulldata;
        } else {
            return null;
        }
    }
    //save and get of model
    public void saveModel(ModelData modelData) throws Exception {
        String fealevel = modelData.getFealevel();
        String modelname = modelData.getModelname();
        if (getModel(fealevel, modelname)!=null){
            getMongoCollection().deleteMany(new Document().append("fealevel", fealevel).append("modelname",modelname));
        }
        Document doc = new Document()
                .append("fealevel", modelData.getFealevel())
                .append("modelname", modelData.getModelname())
                .append("fulldata", Util.serializeToMongo(modelData,ModelData.class));
        getMongoCollection().insertOne(doc);
    }
    public ModelData getModel(String fealevel,String modelname) throws Exception {
        Document searchDoc = new Document().append("fealevel", fealevel).append("modelname",modelname);
        FindIterable<Document> docs = mongoCollection.find(searchDoc);
        Document doc = docs.first();
        if (doc != null) {
            ModelData fulldata = Util.deserializeFromMongo(doc.get("fulldata"), ModelData.class);
            return fulldata;
        } else {
            return null;
        }
    }
    //save and get of diagnoseResData
    public void saveDiagnoseResData(DiagnoseResData diagnoseResData) throws IOException {
        // serialize non structure data
        Document doc = new Document()
                .append("clusterid", diagnoseResData.getClusterid())
                .append("appid", diagnoseResData.getAppid())
                .append("stageid",Integer.valueOf(diagnoseResData.getStageid()))
                .append("fulldata", gson.toJson(diagnoseResData));
        getMongoCollection().insertOne(doc);
    }
    public ArrayList<DiagnoseResData> getDiagnoseResData(String clusterid, String appid) {
        FindIterable<Document> docs = mongoCollection.find(new Document("appid", appid));
        ArrayList<DiagnoseResData> reslist = new ArrayList<DiagnoseResData>();
        for(Document doc:docs){
            DiagnoseResData onedata = gson.fromJson(doc.getString("fulldata"), DiagnoseResData.class);
            reslist.add(onedata);
        }
        return reslist;
    }

    //save and get
    public void clearCollection() {
        getMongoCollection().drop();
    }

    public void close() {
        getMongoClient().close();
    }

    public String getConnectHost() {
        return connectHost;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public MongoCollection<Document> getMongoCollection() {
        return mongoCollection;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void setMongoCollection(String cName) {
        this.mongoCollection = mongoClient.getDatabase(databaseName).getCollection(cName);
    }
}
