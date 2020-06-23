package za.ac.unisa.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sakai_user_id_map")
public class SakaiUserIdMap {

   @Id
	@Column(name = "USER_ID", length = 7)
	private String userId;

	@Column(name = "EID", length = 7)
	private String studentNr;

	public String getStudentNr() {
		return studentNr;
	}

	public void setStudentNr(String studentNr) {
		this.studentNr = studentNr;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	
}
