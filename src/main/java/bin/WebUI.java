package bin;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import server.DiagnoseServlet;
import server.SingletonHandle;
import utils.Config;

/**
 * Created by Administrator on 2015/12/15.
 */
public class WebUI {
//    private static Config config = Config.getInstance();
    public static void main(String[] args) {
        Config config;
        if (args.length==1){
            System.out.println("use the outsize configuration file:"+args[0]);
            config = Config.getInstance(args[0]);
        }else {
            System.out.println("use the default configuration file");
            config = Config.getInstance();
        }

        Server server = new Server(config.getJettyPort());
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        handler.addServlet(DiagnoseServlet.class, "/diagnose");
        SingletonHandle.getInstance();
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            System.exit(-1);
        }
    }

}
