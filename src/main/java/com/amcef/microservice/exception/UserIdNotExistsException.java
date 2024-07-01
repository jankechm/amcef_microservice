package com.amcef.microservice.exception;

public class UserIdNotExistsException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;
	
	
	public UserIdNotExistsException() {
        super();
    }
	
	public UserIdNotExistsException(String message) {
        super(message);
    }
	
}
