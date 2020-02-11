/**
 * 
 */
package com.cometbid.project.security.audit;

/**
 * @author Gbenga
 *
 */
public interface AuditableExtension {
	
	Audit getAudit();

    void setAudit(Audit audit);

    boolean addToAuditHistory(Audit audit);

}
