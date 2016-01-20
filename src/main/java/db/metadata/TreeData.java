package db.metadata;

/**
 * Created by Administrator on 2015/12/23.
 */
public class TreeData {
    private Object feadata;
    private int pcalabel;
    private int kmeanslabel;

    public TreeData(Object feadata, int pcalabel, int kmeanslabel) {
        this.feadata = feadata;
        this.pcalabel = pcalabel;
        this.kmeanslabel = kmeanslabel;
    }

    public Object getFeadata() {
        return feadata;
    }

    public void setFeadata(Object feadata) {
        this.feadata = feadata;
    }

    public int getPcalabel() {
        return pcalabel;
    }

    public void setPcalabel(int pcalabel) {
        this.pcalabel = pcalabel;
    }

    public int getKmeanslabel() {
        return kmeanslabel;
    }

    public void setKmeanslabel(int kmeanslabel) {
        this.kmeanslabel = kmeanslabel;
    }
}
