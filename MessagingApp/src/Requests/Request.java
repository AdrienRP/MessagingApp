package Requests;
import java.io.Serializable;
import java.util.ArrayList;

public class Request implements Serializable{
//datamembers
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;
	private String message;
	
	//constructor
	public Request(String type) {
		this.type = type;
		this.message=null;
	}
	public Request(String type, String message) {
		this.type = type;
		this.message = message;
	}

	public String getType() {
		return this.type;
	}
	
	public String getMessage() {
		return this.message;
	}
	public void setMessage(String message) {
		this.message=message;
	}
	
}
