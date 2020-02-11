/**
 * 
 */
package com.cometbid.project.security.audit;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.userdetails.User;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.AbstractAuditable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import reactor.core.publisher.Mono;

/**
 * @author Gbenga
 *
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(includeFieldNames = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Audit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3708223993588759581L;

	@JsonIgnore
	@CreatedBy
    @Field(name = "CREATED_BY")
	private User createdBy;

	@JsonIgnore
	@CreatedDate
	@Field(name = "CREATED_DTE")
	private LocalDateTime creationDate;

	@JsonIgnore
	@LastModifiedBy
	@Field(name = "MODIFIED_BY")
	private User lastModifiedBy;

	@JsonIgnore
	@LastModifiedDate
	@Field(name = "MODIFIED_DTE")
	private LocalDateTime lastModifiedDate;

	public boolean isNew() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public AuditableExtension auditCreate(AuditableExtension auditable) {
		
		this.setCreationDate(AuditEditor.NOW);
		User user = AuditAwareImpl.getAuditor().blockOptional().orElse(null);
		this.setCreatedBy(user);
		
		auditable.setAudit(this);
		// audit.setNewValue(getJsonRepresentation(auditable));

		auditable.addToAuditHistory(this);
		return auditable;
	}

	// @PreRemove
	// @PreUpdate
	public AuditableExtension auditUpdate(AuditableExtension auditable) {
		
		this.setLastModifiedDate(AuditEditor.NOW);
		User user = AuditAwareImpl.getAuditor().blockOptional().orElse(null);
		this.setLastModifiedBy(user);
		
		auditable.setAudit(this);
		// audit.setNewValue(getJsonRepresentation(auditable));

		auditable.addToAuditHistory(this);
		return auditable;
	}

	private String getJsonRepresentation(@NonNull AuditableExtension auditable) {
		// Json representation of the entity
		// Creating Object of ObjectMapper define in Jakson Api
		ObjectMapper Obj = new ObjectMapper();
		String jsonStr = null;
		try {

			// get Oraganisation object as a json string
			jsonStr = Obj.writeValueAsString(auditable);

			// Displaying JSON String
			System.out.println(jsonStr);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

}
