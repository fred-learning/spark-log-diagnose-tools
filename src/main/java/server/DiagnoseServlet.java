package server;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import utils.Config;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * Created by Administrator on 2015/12/15.
 */
public class DiagnoseServlet  extends HttpServlet {
//    private static RecommendParamsService service = RecommendParamsService.getInstance();
    private static Config conf = Config.getInstance();
    private static SingletonHandle hand = SingletonHandle.getInstance();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String clusterid = req.getParameter("clusterid");
        String appid = req.getParameter("appid");
        if (clusterid !=null&&appid != null) {
            try {
                hand.dodiagnose(clusterid,appid);
            } catch (Exception e) {
                e.printStackTrace();
            }
            resp.setStatus(HttpStatus.OK_200);
            resp.getWriter().println("success");
        } else {
            resp.setStatus(HttpStatus.OK_200);
            resp.getWriter().println("error");
        }
    }

}
