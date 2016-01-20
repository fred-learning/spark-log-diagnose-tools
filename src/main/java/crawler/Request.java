package crawler;

import java.sql.Blob;

import org.apache.log4j.Logger;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public abstract class Request<T> {
	protected static Logger logger = Logger.getLogger(Request.class);
	private String acceptType;
	
	public Request() {
		acceptType = "application/json";
	}
	
	public Request(String type) {
		acceptType = type;
	}
	
	public T handle(String url,boolean single) {
		logger.debug("Handling html request: " + url);

		try {
			HttpResponse<String> resp = Unirest.get(url).header("accept", acceptType).asString();
			if (resp.getStatus() != 200) {
				logger.fatal(String.format("Getting data from %s failed. Response status %d.", 
						url, resp.getStatus()));
				System.exit(-1);
			} else {
				String content = resp.getBody();
				if (single) {
					content="["+content+"]";
				}
				return process(content);
			}
		} catch (UnirestException e) {
			logger.fatal("Error when getting data from " + url, e);
			System.exit(-1);
		}
		return null;
	}
	
	abstract T process(String content);
}
