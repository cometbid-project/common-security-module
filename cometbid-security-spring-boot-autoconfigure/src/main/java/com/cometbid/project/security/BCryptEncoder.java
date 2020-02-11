/**
 * 
 */
package com.cometbid.project.security;

import org.springframework.beans.factory.annotation.Value;
/**
 * @author Gbenga
 *
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BCryptEncoder {

	/**
	 * must be between 4 and 31
	 */
	@Value("${springbootwebfluxjjwt.password.bcryptencoder.strength}")
	private Integer strength;

	@Value("${springbootwebfluxjjwt.password.encoder.default}")
	private boolean useDefault;

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		if (useDefault) {
			return new BCryptPasswordEncoder(16);
		} else {
			return new BCryptPasswordEncoder(strength);
		}
	}

	@Bean
	public CommonSecurity utils() {
		return new CommonSecurity(getPasswordEncoder());
	}

}
