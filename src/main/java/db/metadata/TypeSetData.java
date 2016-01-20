package db.metadata;

import java.io.Serializable;
import java.util.HashSet;

@SuppressWarnings("serial")
public class TypeSetData implements Serializable{
    private String fealevel;//
	private HashSet<String> typeset;

	public TypeSetData() {
        fealevel = "nonelevel";
		typeset = new HashSet<String>();
	}
	public TypeSetData(String level, HashSet<String> ts){
		fealevel = level;
        typeset = ts;
	}
	
	public HashSet<String> getTypeset() {
		return typeset;
	}
	public void setTypeset(HashSet<String> typeset) {
		this.typeset = typeset;
	}
    public String getFealevel() {
        return fealevel;
    }
    public void setFealevel(String fealevel) {
        this.fealevel = fealevel;
    }
}
