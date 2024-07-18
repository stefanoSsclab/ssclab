package org.ssclab.vrp;

public class InvalidCVRPInputException extends IllegalArgumentException {
  
	private static final long serialVersionUID = 1L;

	public InvalidCVRPInputException(String message) {
        super(message);
    }
}
