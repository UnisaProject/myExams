package za.ac.unisa.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "content_resource")
public class ContentResource {

	@Id
	@Column(name = "RESOURCE_ID", length = 255)
	private String resourseId;

	public String getResourseId() {
		return resourseId;
	}

	public void setResourseId(String resourseId) {
		this.resourseId = resourseId;
	}
	
	
}
