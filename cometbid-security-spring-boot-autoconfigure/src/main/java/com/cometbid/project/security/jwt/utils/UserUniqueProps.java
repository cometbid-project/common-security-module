/**
 * 
 */
package com.cometbid.project.security.jwt.utils;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Gbenga
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class UserUniqueProps implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6878543605786506039L;

	private String userId;

	private String ipAddress;
	
	private String sessionId;

	public UserUniqueProps(String id, String ipAddr, String sessionId) {		
		this.userId = id;	
		this.ipAddress = ipAddr;
		this.sessionId = sessionId;
	}
	
}
