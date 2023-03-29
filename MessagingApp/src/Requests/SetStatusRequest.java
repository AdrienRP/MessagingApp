package Requests;

public class SetStatusRequest extends Request {
	private static final long serialVersionUID = 1L;	
	String status;
	
	public SetStatusRequest(String status) {
		super("SetStatusRequest");
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	
	

}
