/**
 * 
 */
package com.cometbid.project.security.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

import com.cometbid.project.security.jwt.utils.JWTUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Slf4j
@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

	@Autowired
	private JWTReactiveAuthManager authenticationManager;

	@Override
	public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange swe) {
		String authHeader = JWTUtil.getAuthorizationPayload(swe);

		log.info("Authentication Header...{}", authHeader);

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String authToken = authHeader.substring(7);

			log.info("Authentication Header token...{}", authToken);

			if (authToken != null) {
				Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken);

				return this.authenticationManager.authenticate(auth).map((authentication) -> {
					log.info("Security Context has been created successfully...{}", authentication);
					
					return new SecurityContextImpl(authentication);
				});
			}
		}
		return Mono.empty();

	}

	public Mono<SecurityContext> load(ServerRequest sr) {
		return load(sr.exchange());
	}

}
