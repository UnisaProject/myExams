package za.ac.unisa.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.ac.unisa.dao.ExampaperdownloadDAO;
 
//import org.springframework.beans.factory.annotation.Autowired;

 
@Service
@Transactional
public class ExampaperdownloadService {
	private ExampaperdownloadDAO exampaperdownloadDAO;

	@Autowired
	public void setUserDAO(ExampaperdownloadDAO usrsunDAO) {
		this.exampaperdownloadDAO = usrsunDAO;
	}

	public List<Object> getSubmissions(String assignmentId) {
		return this.exampaperdownloadDAO.getSubmissions(assignmentId );
	}
	
	public List getExamPaper(String siteId,String examTitle) {
		return this.exampaperdownloadDAO.getExamPaper(siteId,examTitle );
	}
	
	public void insertExamPaperLog(String module, String stno, String oldPath, String newPath, Boolean success) {
		this.exampaperdownloadDAO.insertExamPaperLog(module, stno, oldPath, newPath, success );
	}	
	
}
