/**
 * 
 */
package com.cometbid.project.security;

/**
 * @author Gbenga
 *
 */
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@Configuration
public class Pbkdf2Encoder {

	@Value("${springbootwebfluxjjwt.password.encoder.secret}")
	private String secret;

	@Value("${springbootwebfluxjjwt.password.encoder.iteration}")
	private Integer iteration;

	@Value("${springbootwebfluxjjwt.password.encoder.keylength}")
	private Integer keylength;

	@Value("${springbootwebfluxjjwt.password.encoder.default}")
	private boolean useDefault;

	/*
	@Bean
	public PasswordEncoder getPasswordEncoder() {
		if (useDefault) {
			return new Pbkdf2PasswordEncoder();
		} else {
			return new Pbkdf2PasswordEncoder(secret, iteration, keylength);
		}
	}

	@Bean
	public CommonSecurity utils() {
		return new CommonSecurity(getPasswordEncoder());
	}
	*/

}