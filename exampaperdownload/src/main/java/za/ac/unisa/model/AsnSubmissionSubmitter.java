package za.ac.unisa.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "asn_submission_submitter")
public class AsnSubmissionSubmitter {

	@Id
	@Column(name = "ID", length = 20)
	private String id;

	@Column(name = "SUBMITTER", length = 99)
	private String submitter;

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
