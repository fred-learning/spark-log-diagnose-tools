import crawler.DAGUtils;
import crawler.metadata.SparkSummary;
import crawler.metadata.Stage;

import java.util.List;

/**
 * Created by Administrator on 2015/12/17.
 */
public class DagUtilsTest {
    public static void main(String[] args) {
        String appid = "application_1449125036155_0021";
        List<SparkSummary> summarylist = DAGUtils.getAppSummarys(appid);
        if (summarylist.size()==1) {
            SparkSummary ss = summarylist.get(0);
            List<Stage> stagelist = DAGUtils.getJobsByAppSummary(ss);
            for (Stage stage : stagelist) {
                System.out.println(stage.getStageId()+"--"+stage.getName());
            }
        }
    }
}
