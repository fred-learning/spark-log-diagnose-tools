package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Config {
	private static Logger log = Logger.getLogger(Config.class);
	
	private static Config conf;
	private Properties P;
	
	public static synchronized Config getInstance() {
		if (conf == null) {
			conf = new Config();
		}
		return conf;
	}
    public static synchronized Config getInstance(String filepath) {
        if (conf == null) {
            conf = new Config(filepath);
        }
        return conf;
    }
	
	private Config() {
		try {
			InputStream stream = this.getClass().getResourceAsStream("/settings.properties");
			P = new Properties();
			P.load(stream);
		} catch (IOException e) {
			log.fatal("Error when reading settings.properties:\n", e);
			System.exit(-1);
		}
	}
    private Config(String filepath) {
        try {
            File file = new File(filepath);
            InputStream stream;
            if (file.exists()&&file.isFile())
                stream = new FileInputStream(new File(filepath));
            else
                stream = this.getClass().getResourceAsStream("/settings.properties");
            P = new Properties();
            P.load(stream);
        } catch (IOException e) {
            log.fatal("Error when reading settings.properties:\n", e);
            System.exit(-1);
        }
    }
	public String getValue(String key) {
		return P.getProperty(key);
	}

    public String getKmeansBlock() { return P.getProperty("kmeansBlock"); }

    public String getKmeansTask() { return P.getProperty("kmeansTask"); }

	public String getClusterID() { return P.getProperty("clusterID"); }

    public String getBlockTag() { return P.getProperty("blockTag"); }

    public String getTaskTag() { return P.getProperty("taskTag"); }
	
	public String getHistServerPath() {
		return P.getProperty("historyServerPath");
	}

    public String getHDFSPath() {
        return P.getProperty("hdfsPath");
    }

	public String getYarnPath() {
		return P.getProperty("yarnPath");
	}

	public String getMongoIP() { return P.getProperty("mongoIP"); }

    public String getMongoPort() { return P.getProperty("mongoPort"); }

    public String getMongoDBName() { return P.getProperty("mongoDBName"); }

    public String getMongoBlockFeaCollection() {
		return P.getProperty("mongoBlockFeaCollection");
	}

    public String getMongoTaskFeaCollection() {
        return P.getProperty("mongoTaskFeaCollection");
    }

    public String getMongoTypesetCollection() {
		return P.getProperty("mongoTypesetCollection");
	}

    public String getMongoDiagnoseresCollection() {
        return P.getProperty("mongoDiagnoseresCollection");
    }

    public String getMongoModelCollection() {
        return P.getProperty("mongoModelCollection");
    }

    public Integer getJettyPort() {
        return Integer.parseInt(P.getProperty("jettyPort"));
    }

    public String getTemplateRegPath() {
        return P.getProperty("templateRegPath");
    }

    public String getTemplateRawPath() {
        return P.getProperty("templateRawPath");
    }

    public String getDictPath() {
        return P.getProperty("DictPath");
    }

    public Boolean getTFIDFSwitch() {
        return Boolean.parseBoolean(P.getProperty("TFIDFSwitch"));
    }

    public String getTrainLogPath() {
        return P.getProperty("TrainLogPath");
    }

    public String getTestLogPath() {
        return P.getProperty("TestLogPath");
    }

    public String getTreePath() {
        return P.getProperty("TreePath");
    }


}
