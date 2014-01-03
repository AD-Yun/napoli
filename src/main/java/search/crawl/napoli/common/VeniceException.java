package search.crawl.napoli.common;

import search.crawl.napoli.common.VeniceEnums.Errors;

public class VeniceException extends Exception {
	private final int error_code;
	private final String error_message;
	private final Exception exception;
	private final Errors error;
	
	public VeniceException() { 
		super();
		this.error_code = 0;
		this.error_message = "";
		this.exception = null;
		this.error = Errors.UNKNOWN;
	}
	
	public VeniceException(VeniceEnums.Errors err, Exception exception) { 
		super(err.getMessage());
		this.error_code = err.getErrorCode();
		this.error_message = err.getMessage();
		this.exception = exception;
		this.error = err;
	}
	
	public VeniceException(int error_code, String message, Exception exception) { 
		super(message);
		this.error_code = error_code;
		this.error_message = message;
		this.exception = exception;
		this.error = Errors.getErrors(error_code);
	}
	
	public int getError_code() {
		return error_code;
	}
	public String getError_message() {
		return error_message;
	}
	public Exception getException() {
		return exception;
	}
	
	public Errors getError() {
		return error;
	}

	public String toString() {
		return "Error Code : " + this.error_code + ", ErrorMessage : "  + this.error_message;
	}
}
