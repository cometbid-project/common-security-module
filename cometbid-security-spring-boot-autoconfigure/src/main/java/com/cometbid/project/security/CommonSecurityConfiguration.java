/**
 * 
 */
package com.cometbid.project.security;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.cometbid.project.security.annotation.Algorithm;
import com.cometbid.project.security.annotation.EnableCommonSecurity;

public class CommonSecurityConfiguration implements ImportSelector {

	public String[] selectImports(AnnotationMetadata annotationMetadata) {
		AnnotationAttributes attributes = AnnotationAttributes
				.fromMap(annotationMetadata.getAnnotationAttributes(EnableCommonSecurity.class.getName(), false));
		Algorithm algorithm = attributes.getEnum("algorithm");

		switch (algorithm) {
		case PBKDF2:
			return new String[] { "com.cometbid.project.security.Pbkdf2Encoder" };
		case BCRYPT:
		default:
			return new String[] { "com.cometbid.project.security.BCryptEncoder" };
		}
	}
}
	