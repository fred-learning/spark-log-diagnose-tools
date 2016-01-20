package crawler.metadata;

import crawler.metadata.SparkAttempts;

import java.util.List;

public class SparkSummary {
    private String id;
    private String name;
    private List<SparkAttempts> attempts;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<SparkAttempts> getAttempts() {
        return attempts;
    }
}
