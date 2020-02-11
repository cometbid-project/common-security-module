/**
 * 
 */
package com.cometbid.project.security.audit;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.domain.AuditorAware;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
public class AuditAwareImpl implements AuditorAware<User> {

	@Override
	public Optional<User> getCurrentAuditor() {

		return ReactiveSecurityContextHolder.getContext().map(p -> p.getAuthentication())
				.filter(Authentication::isAuthenticated).map(Authentication::getPrincipal)
				.map(User.class::cast).blockOptional();
				// .map(p -> new Username(p.getUsername(),
				//		p.getAuthorities().parallelStream().map(auth -> (GrantedAuthority) auth)
					//			.map(a -> a.getAuthority()).collect(Collectors.joining(",")))).blockOptional();
	}

	public static Mono<User> getAuditor() {
		return ReactiveSecurityContextHolder.getContext().map(p -> p.getAuthentication())
				.switchIfEmpty(Mono.empty())
				.filter(Authentication::isAuthenticated).map(Authentication::getPrincipal).map(User.class::cast);
				/*.map(p -> new Username(p.getUsername(),
						p.getAuthorities().parallelStream().map(auth -> (GrantedAuthority) auth)
								.map(a -> a.getAuthority()).collect(Collectors.joining(","))));*/
	}

}
