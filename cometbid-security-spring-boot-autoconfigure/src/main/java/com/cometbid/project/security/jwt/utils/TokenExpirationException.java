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
public class TokenExpirationException extends ResponseStatusException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6461519184418652386L;

	private static final HttpStatus status = HttpStatus.PRECONDITION_FAILED;

	public TokenExpirationException(String message, Throwable e) {
		super(status, message, e);
	}

	public TokenExpirationException(String message) {
		super(status, message);
	}

}
