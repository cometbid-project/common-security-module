/**
 * 
 */
package com.cometbid.project.security.annotation;

/**
 * @author Gbenga
 *
 */
import org.springframework.context.annotation.Import;
import com.cometbid.project.security.CommonSecurityConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(CommonSecurityConfiguration.class)
public @interface EnableCommonSecurity {
	Algorithm algorithm() default Algorithm.BCRYPT;
}