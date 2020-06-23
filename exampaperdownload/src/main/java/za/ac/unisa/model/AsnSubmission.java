package za.ac.unisa.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "asn_submission")
public class AsnSubmission {

	@Id
	@Column(name = "SUBMISSION_ID", length = 7)
	private String submissionId;
}
