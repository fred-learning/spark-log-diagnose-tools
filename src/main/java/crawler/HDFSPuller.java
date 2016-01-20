package crawler;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import utils.Config;

import java.io.*;
import java.net.URI;

/**
 * Created by Administrator on 2015/12/17.
 */
public class HDFSPuller {
    private static Config config = Config.getInstance();
    public static int PullLog(String appid) throws IOException {
        //判断文件是否存在,存在返回
        File logdir = new File(config.getTestLogPath()+appid+"/");
        if (logdir.exists()){
            return 2;
        }

        String url = config.getHDFSPath()+appid;
        Path path = new Path(url);
        Configuration conf = new Configuration();
        conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        conf.set("fs.defaultFS",url);
        FileSystem fileSystem = FileSystem.get(URI.create(url), conf);
        //拉取日志
        fileSystem.copyToLocalFile(false,path,new Path(config.getTestLogPath()),true);

        //将各个节点日志合并，以appid命名文件
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
                    fw.write(line+"\n");
                    line = br.readLine();
                }
                br.close();
            }
        }
        fw.close();
        return 1;
    }
}
