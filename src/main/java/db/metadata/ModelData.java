package db.metadata;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/12/10.
 */
public class ModelData implements Serializable{
    private String fealevel;
    private String modelname;
    private Object model;
    private String[] anomalylabels;


    public ModelData(String fealevel, String modelname, Object model, String[] anomalylabels) {
        this.fealevel = fealevel;
        this.modelname = modelname;
        this.model = model;
        this.anomalylabels = anomalylabels;
    }

    public String getFealevel() {
        return fealevel;
    }

    public void setFealevel(String fealevel) {
        this.fealevel = fealevel;
    }

    public String getModelname() {
        return modelname;
    }

    public void setModelname(String modelname) {
        this.modelname = modelname;
    }

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
    }

    public String[] getAnomalylabels() {
        return anomalylabels;
    }

    public void setAnomalylabels(String[] anomalylabels) {
        this.anomalylabels = anomalylabels;
    }
}
