package com.cometbid.project.security.handler;


import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;

import com.cometbid.project.security.jwt.utils.InvalidTokenException;
import com.cometbid.project.security.jwt.utils.JWTCustomVerifier;
import com.cometbid.project.security.jwt.utils.JWTUtil;
import com.cometbid.project.security.jwt.utils.TokenExpirationException;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Primary
@Component
public class JWTReactiveAuthManager implements ReactiveAuthenticationManager {

	@Autowired
	private JWTUtil jwtUtil;

	private JWTCustomVerifier jwtVerifier = new JWTCustomVerifier();

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		String authToken = authentication.getCredentials().toString();

		log.info("authenticating JWT token...{}", authToken);
		if (authToken != null) {
			return jwtVerifier.verifySignature(authToken)
					.switchIfEmpty(Mono
							.error(new InvalidTokenException("Authentication Error: token is invalid")))
					.doOnSuccess(u -> log.info("Token verification was successful"))
					.filter(jwtVerifier.isNotExpired)
					.switchIfEmpty(Mono
							.error(new TokenExpirationException("Authentication Error: token has expired, pls renew")))
					.map(jwtUtil::getUsernamePasswordAuthenticationToken).filter(Objects::nonNull);
		}

		return Mono.empty();
	}

}
