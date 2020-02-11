/**
 * 
 */
package com.cometbid.project.security.jwt.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Gbenga
 *
 */
public class InvalidTokenException extends ResponseStatusException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6472078305776553107L;
	
	private static final HttpStatus status = HttpStatus.UNAUTHORIZED;

	public InvalidTokenException(String message, Throwable e) {
		super(status, message, e);
	}

	public InvalidTokenException(String message) {
		super(status, message);
	}
}
