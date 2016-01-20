import db.DBInstance;
import db.metadata.ModelData;
import model.DataInit;
import model.DecisionTree;
import model.Kmeans;
import utils.Config;
import weka.clusterers.SimpleKMeans;

/**
 * Created by Administrator on 2015/12/20.
 */
public class KmeansTest {
    private static Config config = Config.getInstance("D:/settings.properties");

    public static void main(String[] args) throws Exception{
//        String trainfeapath = "data/feature_";
//        new DataInit().getTaskIdData();
//        String type = "tid";//"blockid"
//        Kmeans.train(8, type, trainfeapath);
//        DecisionTree.train(type);
        System.out.println(config.getTaskTag());
        DBInstance modeldb = new DBInstance("model");
        ModelData model = modeldb.getModel(config.getTaskTag(), "kmeans");
        String[] badlist = model.getAnomalylabels();
        SimpleKMeans m = (SimpleKMeans)model.getModel();
        System.out.print(m);

    }
}
