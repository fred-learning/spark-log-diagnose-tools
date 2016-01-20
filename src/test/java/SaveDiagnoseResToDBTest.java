import db.DBInstance;
import db.metadata.DiagnoseResData;

import java.io.IOException;

/**
 * Created by Administrator on 2015/12/17.
 */
public class SaveDiagnoseResToDBTest {
    public static void main(String[] args) throws IOException {
        DBInstance db = new DBInstance("diagnose");
        for(int i=0;i<15;i++) {
            String clusterid = "cluster-bda";
            String appid = "application_1447660331941_0109";
            String stageid = "stage"+i;
            String judgebad = "anomaly";
            String analysis = "shuffle bad";
            String locate = "collect line 27,als class...";
            DiagnoseResData diagnoseResData = new DiagnoseResData(clusterid,appid,stageid,locate);
            db.saveDiagnoseResData(diagnoseResData);
        }
    }
}
