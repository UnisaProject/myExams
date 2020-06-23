package za.ac.unisa.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "asn_assignment")
public class AsnAssignment {
	
	@Id
	@Column(name = "ASSIGNMENT_ID", length = 36)
	private String assignmentId;
	
	 
	@Column(name = "CONTEXT", length = 99)
	private String context;
	
	
	 
	@Column(name = "OPEN_DATE", length = 10)
	private String openDate;  
	
	@Column(name = "TITLE", length = 255)
	private String title; 
	
	@Column(name = "DRAFT", length = 1)
	private String draft;

	public String getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(String assignmentId) {
		this.assignmentId = assignmentId;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getOpenDate() {
		return openDate;
	}

	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDraft() {
		return draft;
	}

	public void setDraft(String draft) {
		this.draft = draft;
	} 
	
	
	
}
