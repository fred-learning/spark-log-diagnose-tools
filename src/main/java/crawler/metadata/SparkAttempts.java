package crawler.metadata;

public class SparkAttempts {
    private String attemptId;
    private String startTime;
    private String endTime;
    private String sparkUser;
    private Boolean completed;

    public String getAttemptId() {
        return attemptId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getSparkUser() {
        return sparkUser;
    }

    public Boolean getCompleted() {
        return completed;
    }
}
