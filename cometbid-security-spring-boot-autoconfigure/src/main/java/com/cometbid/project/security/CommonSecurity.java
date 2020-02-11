/**
 * 
 */
package com.cometbid.project.security;

/**
 * @author Gbenga
 *
 */
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
public class CommonSecurity {

	private PasswordEncoder encoder;

}
