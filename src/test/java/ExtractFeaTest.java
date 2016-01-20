import parseLog.ExtractBlockFea;
import parseLog.ExtractTaskFea;
import utils.Config;

/**
 * Created by Administrator on 2015/12/18.
 */
public class ExtractFeaTest {
    private static Config config = Config.getInstance();
    public static void main(String[] args) throws Exception {
        ExtractBlockFea handler = new ExtractBlockFea();
//        ExtractTaskFea handler = new ExtractTaskFea();
        handler.DoExtract(config.getTestLogPath(),true);
    }
}
