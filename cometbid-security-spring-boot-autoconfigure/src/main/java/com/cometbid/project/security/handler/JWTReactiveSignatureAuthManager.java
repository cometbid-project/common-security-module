/**
 * 
 */
package com.cometbid.project.security.handler;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.cometbid.project.security.jwt.utils.InvalidTokenException;
import com.cometbid.project.security.jwt.utils.JWTCustomVerifier;
import com.cometbid.project.security.jwt.utils.JWTUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Slf4j
@Component
@Qualifier("signature")
public class JWTReactiveSignatureAuthManager implements ReactiveAuthenticationManager {

	@Autowired
	private JWTUtil jwtUtil;

	private JWTCustomVerifier jwtVerifier = new JWTCustomVerifier();

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		String authToken = authentication.getCredentials().toString();

		log.info("Authenticating JWT token...{}", authToken);
		if (authToken != null) {
			return jwtVerifier.verifySignature(authToken)
					.switchIfEmpty(
							Mono.error(new InvalidTokenException("Authentication Error: token is invalid")))
					.map(jwtUtil::getUsernamePasswordAuthenticationToken)
					.map(jwtUtil::generateToken).filter(Objects::nonNull);
		}

		return Mono.empty();
	}
}
