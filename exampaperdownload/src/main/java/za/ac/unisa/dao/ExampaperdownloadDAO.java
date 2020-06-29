package za.ac.unisa.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.math.BigInteger; 

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class ExampaperdownloadDAO {
	private SessionFactory sessionFactory;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	/*
	 * public List<Usrsun> getUserList() { Criteria criteria =
	 * this.getSessionFactory().getCurrentSession().createCriteria(Usrsun.class);
	 * 
	 * @SuppressWarnings("unchecked") List<Usrsun> userList = criteria.list();
	 * return userList; }
	 * 
	 * // @SuppressWarnings("unchecked") public List<Usrsun>
	 * getUserDetailsPerModuleYearSem(String studyUnitCode,int academicYear, int
	 * semester) { Criteria criteria =
	 * this.getSessionFactory().getCurrentSession().createCriteria(Usrsun.class);
	 * criteria.add(Restrictions.eq("mkStudyUnitCode", studyUnitCode));
	 * criteria.add(Restrictions.eq("mkAcademicYear", academicYear));
	 * criteria.add(Restrictions.eq("mkSemesterPeriod", semester)); return
	 * criteria.list(); }
	 * 
	 * public List<Usrsun> getModuleListPerUser(String userId,int academicYear, int
	 * semester) { Criteria criteria =
	 * this.getSessionFactory().getCurrentSession().createCriteria(Usrsun.class);
	 * criteria.add(Restrictions.eq("novelUserId", userId));
	 * criteria.add(Restrictions.eq("mkAcademicYear", academicYear));
	 * criteria.add(Restrictions.eq("mkSemesterPeriod", semester)); return
	 * criteria.list(); }
	 */

	/*
	 * public List getExamPaper(String siteId,String examTitle ) {
	 * 
	 * String queryString =
	 * "select ASSIGNMENT_ID,context,open_date,title ,SUBSTRING(context,1,7) AS MODULE, draft  from asn_assignment"
	 * + " where context = ?  and   upper(title) = upper(?)  and   draft = 0";
	 * 
	 * 
	 * SQLQuery sqlQuery =
	 * this.getSessionFactory().getCurrentSession().createSQLQuery(queryString);
	 * List results= sqlQuery.list();
	 * 
	 * return results; }
	 */

	public List getSubmissions(String assignmentId) {

	 
		String queryString = " select asn_submission.submission_id AS SUBMISSION_ID, asn_submission_submitter.SUBMITTER, sakai_user_id_map.eid AS STUDENTNR,"
				+ " asn_submission_attachments.attachment, "
				+ " content_resource.file_Path AS FILEPATH "
				+ " from asn_submission, asn_submission_submitter, sakai_user_id_map, asn_submission_attachments, content_resource"
				+ " where assignment_id = '"+assignmentId+"'"
				+ " and   asn_submission.submission_id = asn_submission_submitter.SUBMISSION_ID"
				+ " and   asn_submission_submitter.SUBMITTER = sakai_user_id_map.user_id"
				+ " and   asn_submission_attachments.submission_id = asn_submission_submitter.submission_id"
				+ " and   asn_submission_attachments.attachment = CONCAT('/content',content_resource.resource_id)";

		SQLQuery sqlQuery = this.getSessionFactory().getCurrentSession().createSQLQuery(queryString);
		List<Object[]> results = sqlQuery.list();
		List<ExamPaperDetails> list= new ArrayList<ExamPaperDetails>(); 

		Iterator it = results.iterator();
		while(it.hasNext()){
		     Object[] line = (Object[]) it.next();
		     ExamPaperDetails examPaperDetails = new ExamPaperDetails();
		     examPaperDetails.setSubmissionId(line[0].toString());
		     examPaperDetails.setSubmitter(line[1].toString());
		     examPaperDetails.setStudentNr(line[2].toString());
		     examPaperDetails.setAttachment(line[3].toString());
			 examPaperDetails.setFilePath(line[4].toString());

		     list.add(examPaperDetails);
		}
		return list;
	}

	public List getExamPaper(String siteId, String examTitle) {
		
		//siteId = "MNM3714-2018-10";
		//examTitle = "Sonette Test";
		
		String queryString = "select ASSIGNMENT_ID,context, title ,SUBSTRING(context,1,7) AS MODULE, draft  from asn_assignment"
				+ " where context = "+siteId+"  and       title = "+examTitle.toUpperCase()+"  and   draft = 0";

		System.out.println("queryString: "+queryString);
		SQLQuery sqlQuery = this.getSessionFactory().getCurrentSession().createSQLQuery(queryString);
		
		List<Object[]> results = sqlQuery.list();
		List<ExamPaper> list= new ArrayList<ExamPaper>(); 
		
		Iterator it = results.iterator();
		while(it.hasNext()){
		     Object[] line = (Object[]) it.next();
		     ExamPaper eq = new ExamPaper();
		     eq.setAssignmentid((line[0].toString()));
		     eq.setContext( line[1].toString());
		     eq.setTitle(line[2].toString());
		     eq.setModule(line[3].toString());
		     eq.setDraft(line[4].toString());

		     list.add(eq);
		}
		
 

		return list;
	}

	public Integer getCountSubmissionPerStudent(String assignmentId, String user_id) {
		
		//siteId = "MNM3714-2018-10";
		//examTitle = "Sonette Test";
		
		Integer totalSubmission = 0;
		String queryString = "select count(*) as TOTAL"+
							"from asn_submission, asn_submission_submitter,asn_submission_attachments "+
							"where assignment_id = '"+assignmentId+"' "+
							"and   asn_submission.submission_id = asn_submission_submitter.SUBMISSION_ID "+
							"and   asn_submission_submitter.SUBMITTER = '"+user_id+"'"+
							"and   asn_submission_attachments.submission_id = asn_submission_submitter.submission_id ";


		System.out.println("queryString: "+queryString);
		SQLQuery sqlQuery = this.getSessionFactory().getCurrentSession().createSQLQuery(queryString);
		
		List<Object[]> results = sqlQuery.list();
		List<ExamPaper> list= new ArrayList<ExamPaper>(); 
		
		Iterator it = results.iterator();
	
		while(it.hasNext()){
		     Object[] line = (Object[]) it.next();
			 //String totalSubmission = Integer.parseInt(line[0].toString());
			 //String totalSubmission = line[0].toString();
			 //Integer.valueOf((String) object);
		     totalSubmission = ((BigInteger) line[0]).intValue();
		}
		
		//int i = Integer.parseInt(totalSubmission);
		return totalSubmission;

	}

/*
select count(*)
from asn_submission, asn_submission_submitter,asn_submission_attachments
where assignment_id = '89f7a275-b0eb-4df5-92ff-90790df8d3c4'
and   asn_submission.submission_id = asn_submission_submitter.SUBMISSION_ID
and   asn_submission_submitter.SUBMITTER = '068aaad9-bcd0-49a4-a4fb-cac5f331cdca'
and   asn_submission_attachments.submission_id = asn_submission_submitter.submission_id
*/
	public void insertExamPaperLog(String module, String stno, String oldPath, String newPath, Boolean success, String error) {
		
		/*CREATE TABLE exampaper_download
( module VARCHAR(7) NOT NULL,
studentnr VARCHAR(10),
copy_from VARCHAR(100),
copy_to varchar(100),
copy_date date,
success boolean*/
		
		String queryString = "INSERT INTO EXAMPAPER_DOWNLOAD_LOG "+
							" VALUES ('"+module+"', '"+stno+"', '"+oldPath+"', '"+newPath+"', now(), "+success+", '"+error+"')";
		System.out.println("queryString INSERTEXAMPAPERLOG: "+queryString);
		
		SQLQuery sqlQuery = this.getSessionFactory().getCurrentSession().createSQLQuery(queryString);
		
		sqlQuery.executeUpdate();
		
	}


}
