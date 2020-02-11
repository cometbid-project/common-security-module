/**
 * 
 */
package com.cometbid.project.security.jwt.utils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * @author Gbenga
 *
 */
@Slf4j
@Component
public class JWTCustomVerifier {

	private JWSVerifier jwsVerifier;

	public JWTCustomVerifier() {
		this.jwsVerifier = this.buildJWSVerifier();
	}

	public Mono<SignedJWT> check(String token) {
		log.info("checking JWT token...{}", token);

		return verifySignature(token).filter(validClaims).filter(isNotExpired);
	}

	public Mono<SignedJWT> verifySignature(String token) {
		log.info("verifying JWT token signature...{}", token);

		return Mono.justOrEmpty(createJWS(token)).filter(validSignature).filter(validClaims);
	}

	public Predicate<SignedJWT> isNotExpired = token -> {

		Date expDate = getExpirationDate(token);
		log.info("Token Expiration Date...{}", expDate);
		Date currentDate = Date.from(Instant.now());
		log.info("Current Date...{}", currentDate);

		boolean notExpired = getExpirationDate(token).after(Date.from(Instant.now()));
		log.info("Token is expired?...{}", !notExpired);

		return notExpired;
	};

	private Predicate<SignedJWT> validSignature = token -> {
		try {

			boolean validSignature = token.verify(this.jwsVerifier);
			log.info("Token has a valid Signature?...{}", validSignature);

			return validSignature;
		} catch (JOSEException e) {
			e.printStackTrace();
			return false;
		}
	};

	private Predicate<SignedJWT> validClaims = token -> {

		try {
			return JWTUtil.ISSUER.equals(extractIssuerFromToken(token))
					&& JWTUtil.AUDIENCES.equals(extractAudienceFromToken(token));
			// && (username.equals(userDetails.getUsername()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	};

	private MACVerifier buildJWSVerifier() {
		try {

			return new MACVerifier(JWTSecrets.DEFAULT_SECRET);
		} catch (JOSEException e) {
			e.printStackTrace();
			return null;
		}
	}

	private SignedJWT createJWS(String token) {

		log.info("Parsing JWT token...{}", token);
		try {
			return SignedJWT.parse(token);
		} catch (ParseException e) {
			e.printStackTrace();

			log.info("Error occured while parsing token...{}", token);
			return null;
		}
	}

	private Date getExpirationDate(SignedJWT token) {
		try {
			return token.getJWTClaimsSet().getExpirationTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<String> extractAudienceFromToken(SignedJWT token) throws ParseException {
		try {
			return token.getJWTClaimsSet().getAudience();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String extractIssuerFromToken(SignedJWT token) throws ParseException {
		try {
			return token.getJWTClaimsSet().getIssuer();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}

	}

}
