import crawler.HDFSPuller;
import org.apache.hadoop.fs.*;
import utils.Config;

import java.io.*;
import java.net.URI;

/**
 * Created by Administrator on 2015/12/17.
 */
public class HdfsTest {
    private static Config config = Config.getInstance();

    public static void main(String[] args) throws IOException {
        //将各个节点日志合并，以appid命名文件
        String appid = "application_1452478704551_0006";
        File logdir = new File(config.getTestLogPath()+appid+"/");
        File mergelogfile = new File(config.getTestLogPath()+appid+"/"+appid);
        FileWriter fw = new FileWriter(mergelogfile);
        if (logdir.exists()){
            File[] files = logdir.listFiles();
            for (File onefile:files){
                System.out.print(onefile.getName());
                if (onefile.getName().equals(appid))
                    continue;
                String line="";
                BufferedReader br = new BufferedReader(new FileReader(onefile));
                line = br.readLine();
                while (line!=null){
//                    fw .write(line);
                    System.out.print(line);
                    line=br.readLine();
                }
                br.close();
            }
        }
        fw.close();    }
}
