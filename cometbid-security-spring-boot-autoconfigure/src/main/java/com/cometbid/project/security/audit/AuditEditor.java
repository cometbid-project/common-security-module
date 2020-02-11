/**
 * 
 */
package com.cometbid.project.security.audit;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.Instant;
import java.time.ZoneOffset;
// import javax.persistence.PrePersist;
// import javax.persistence.PreUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

/**
 * @author Gbenga
 *
 */
public class AuditEditor {

	public static LocalDateTime NOW = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
			.truncatedTo(ChronoUnit.MILLIS);  
}
