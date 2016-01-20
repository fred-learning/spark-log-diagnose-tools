package crawler.metadata;

public class Stage {
	private String status;
	private Integer stageId;
	private Integer numCompleteTasks;
	private Integer numFailedTasks;
	private Long executorRunTime;
	private Long inputBytes;
	private Long inputRecords;
	private Long outputBytes;
	private Long outputRecords;
	private Long shuffleReadBytes;
	private Long shuffleReadRecords;
	private Long shuffleWriteBytes;
	private Long shuffleWriteRecords;
	private Long memoryBytesSpilled;
	private Long diskBytesSpilled;
	private String name;
	private String details;
	private String schedulingPool;
	
	
	public String getStatus() {
		return status;
	}
	public Integer getStageId() {
		return stageId;
	}
	public Integer getNumCompleteTasks() {
		return numCompleteTasks;
	}
	public Integer getNumFailedTasks() {
		return numFailedTasks;
	}
	public Long getExecutorRunTime() {
		return executorRunTime;
	}
	public Long getInputBytes() {
		return inputBytes;
	}
	public Long getInputRecords() {
		return inputRecords;
	}
	public Long getOutputBytes() {
		return outputBytes;
	}
	public Long getOutputRecords() {
		return outputRecords;
	}
	public Long getShuffleReadBytes() {
		return shuffleReadBytes;
	}
	public Long getShuffleReadRecords() {
		return shuffleReadRecords;
	}
	public Long getShuffleWriteBytes() {
		return shuffleWriteBytes;
	}
	public Long getShuffleWriteRecords() {
		return shuffleWriteRecords;
	}
	public Long getMemoryBytesSpilled() {
		return memoryBytesSpilled;
	}
	public Long getDiskBytesSpilled() {
		return diskBytesSpilled;
	}
	public String getName() {
		return name;
	}
	public String getDetails() {
		return details;
	}
	public String getSchedulingPool() {
		return schedulingPool;
	}
	
	
}
