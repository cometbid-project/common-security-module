/**
 * 
 */
package com.cometbid.project.security.audit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Gbenga
 *
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Username {

	private String username;
	private String roles;
	
	public Username(String username) {
		this.username = username;
	}
}
