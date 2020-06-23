package za.ac.unisa.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;


@Entity
@Table(name = "asn_submission_attachments")
public class AsnSubmissionAttachments implements Serializable{

	@Id
	@Column(name = "ATTACHMENT", length = 1024)
	private String attachement;
	
	@Id
	@Column(name = "SUBMISSION_ID", length = 36)
	private String submissionId;

	public String getAttachement() {
		return attachement;
	}

	public void setAttachement(String attachement) {
		this.attachement = attachement;
	}

	public String getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(String submissionId) {
		this.submissionId = submissionId;
	}
	
	
}
