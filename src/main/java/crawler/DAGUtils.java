package crawler;

import java.util.List;

import crawler.metadata.SparkSummary;
import crawler.metadata.Stage;
import org.apache.log4j.Logger;
import utils.Config;


public class DAGUtils {
	private static Logger logger = Logger.getLogger(DAGUtils.class);
    private static String host = Config.getInstance().getHistServerPath();
    public static List<SparkSummary> getAppSummarys(String appid) {
        String apiPath = String.format("%s/api/v1/applications/%s", host,appid);
        JsonArrayRequest<SparkSummary> request = new JsonArrayRequest<SparkSummary>(SparkSummary.class);
        return request.handle(apiPath,true);
    }
    
	public static List<Stage> getJobsByAppSummary(SparkSummary summary) {
        String attemptId = summary.getAttempts().get(0).getAttemptId();
        String apiPath;
        if (attemptId == null) {
            apiPath = String.format("%s/api/v1/applications/%s/stages",
            		host, summary.getId());
        } else {
            apiPath = String.format("%s/api/v1/applications/%s/%s/stages/",
            		host, summary.getId(), attemptId);
        }
		JsonArrayRequest<Stage> request = new JsonArrayRequest<Stage>(Stage.class);
		return request.handle(apiPath,false);
	}
}