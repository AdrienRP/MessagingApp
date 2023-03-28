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
	
	//constructor
	public Request(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	
}
