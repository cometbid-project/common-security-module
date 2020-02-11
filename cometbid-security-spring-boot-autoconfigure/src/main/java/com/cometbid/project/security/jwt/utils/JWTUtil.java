package com.cometbid.project.security.jwt.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Configuration
@PropertySource(value = "classpath:authSecurity.properties")
public class JWTUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3933956882691612317L;

	public static final String ISSUER = "cometbid.alajounion.com";
	public static final List<String> AUDIENCES = new ArrayList<>(Arrays.asList("Alajounion-Cometbid Customers"));

	public static final String SESSION_ID = "Session_id";
	public static final Instant CURRENT_TIME = Instant.now();
	public static final String REALM_ACCESS = "auths";
	public static final String IP_ADDRESS = "ip";
	public static final String USER_ID = "user_id";
	private static final String BEARER = "Bearer ";

	@Value("${auth.params.jwt_expiration_period}")
	private String expirationTime;

	public Authentication generateToken(Authentication auth) {

		String username = (String) auth.getPrincipal();
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		UserUniqueProps uniqProps = (UserUniqueProps) auth.getDetails();

		String authToken = generateToken(uniqProps, username, authorities);

		return new UsernamePasswordAuthenticationToken(authToken, authToken);
	}

	/**
	 * 
	 * @param subjectName
	 * @param authorities
	 * @return
	 */
	public String generateToken(UserUniqueProps uniqProps, String username,
			Collection<? extends GrantedAuthority> authorities) {

		int MAX_AGE = Integer.parseInt(expirationTime);

		Instant EXPIRATION_TIME = CURRENT_TIME.plus(MAX_AGE, ChronoUnit.MINUTES);

		String uniqueId = UUID.randomUUID().toString();
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().jwtID(uniqueId)
				.subject(username).issuer(ISSUER).audience(AUDIENCES)
				.issueTime(Date.from(CURRENT_TIME)).expirationTime(Date.from(EXPIRATION_TIME))
				.claim(IP_ADDRESS, uniqProps.getIpAddress())
				.claim(USER_ID, uniqProps.getUserId())
				.claim(SESSION_ID, uniqProps.getSessionId())
				.claim(REALM_ACCESS, authorities.parallelStream().map(auth -> (GrantedAuthority) auth)
						.map(a -> a.getAuthority()).collect(Collectors.joining(",")))
				.build();

		SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

		try {
			signedJWT.sign(getJWTSigner());
		} catch (JOSEException e) {
			e.printStackTrace();
		}

		return signedJWT.serialize();
	}

	private JWSSigner getJWTSigner() {
		JWSSigner jwsSigner;
		try {
			jwsSigner = new MACSigner(JWTSecrets.DEFAULT_SECRET);
		} catch (KeyLengthException e) {
			jwsSigner = null;
		}
		return jwsSigner;
	}

	public static String getAuthorizationPayload(ServerWebExchange serverWebExchange) {
		return serverWebExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
	}

	public static Predicate<String> matchBearerLength() {
		Predicate<String> matchBearerLength = authValue -> authValue.length() > BEARER.length();
		return matchBearerLength;
	}

	public static Function<String, String> getBearerValue() {
		Function<String, String> getBearerValue = authValue -> authValue.substring(BEARER.length(), authValue.length());
		return getBearerValue;
	}

	public Authentication getUsernamePasswordAuthenticationToken(SignedJWT signedJWT) {
		String subject;
		String auths;
		String userId;
		String sessionId;

		log.info("Signed JWT ...{}", signedJWT);
		try {
			subject = signedJWT.getJWTClaimsSet().getSubject();
			auths = (String) signedJWT.getJWTClaimsSet().getClaim(REALM_ACCESS);
			userId = (String) signedJWT.getJWTClaimsSet().getClaim(USER_ID);
			sessionId = (String) signedJWT.getJWTClaimsSet().getClaim(SESSION_ID);
		} catch (ParseException e) {
			return null;
		}

		log.info("Extractng JWT details...{} {} {}", subject, auths, userId);

		String ipAddress = null;
		try {
			ipAddress = (String) signedJWT.getJWTClaimsSet().getClaim(IP_ADDRESS);
		} catch (Exception ex) {
			// ignore
		}

		log.info("Extractng JWT details...{}", ipAddress);

		List<? extends GrantedAuthority> authorities = Stream.of(auths.split(","))
				.map(a -> new SimpleGrantedAuthority(a)).collect(Collectors.toList());

		UsernamePasswordAuthenticationToken userAuthToken = new UsernamePasswordAuthenticationToken(subject, null,
				authorities);
		userAuthToken.setDetails(new UserUniqueProps(userId, ipAddress, sessionId));

		log.info("UsernamePassword details...{}", userAuthToken);
		return userAuthToken;
	}

	public Boolean validateToken(String token, UserDetails userDetails) throws ParseException {
		final String username = extractUsernameFromToken(token);
		final String roles = extractRolesFromToken(token);

		return roles
				.equals(userDetails.getAuthorities().parallelStream().map(auth -> (GrantedAuthority) auth)
						.map(a -> a.getAuthority()).collect(Collectors.joining(",")))
				&& ISSUER.equals(extractIssuerFromToken(token)) && AUDIENCES.equals(extractAudienceFromToken(token))
				&& (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public String extractUsernameFromToken(String token) throws ParseException {
		return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
	}

	public Boolean isTokenExpired(String token) throws ParseException {
		return extractExpirationFromToken(token).before(new Date());
	}

	public Date extractExpirationFromToken(String token) throws ParseException {
		return SignedJWT.parse(token).getJWTClaimsSet().getExpirationTime();
	}

	public String extractUserIdFromToken(String token) throws ParseException {
		return (String) SignedJWT.parse(token).getJWTClaimsSet().getClaim(USER_ID);
	}

	public String extractRolesFromToken(String token) throws ParseException {
		return (String) SignedJWT.parse(token).getJWTClaimsSet().getClaim(REALM_ACCESS);
	}

	public List<String> extractAudienceFromToken(String token) throws ParseException {
		return SignedJWT.parse(token).getJWTClaimsSet().getAudience();
	}

	public String extractIssuerFromToken(String token) throws ParseException {
		return SignedJWT.parse(token).getJWTClaimsSet().getIssuer();
	}

}
