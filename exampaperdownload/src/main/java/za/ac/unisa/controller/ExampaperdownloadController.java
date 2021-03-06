package za.ac.unisa.controller;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.math.BigInteger; 


import org.apache.commons.collections4.map.ListOrderedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import za.ac.unisa.dao.ExamPaper;
import za.ac.unisa.dao.ExamPaperDetails;
import za.ac.unisa.service.ExampaperdownloadService;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.*;  
import javax.mail.*;  
import javax.mail.internet.*;  
import javax.activation.*; 
//import javax.xml.rpc.ServiceException;

import java.io.*;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.nio.channels.FileChannel; 
//import org.apache.commons.io.FileUtils;
import com.google.common.io.Files;
import java.nio.ByteBuffer;


@RestController
public class ExampaperdownloadController {

	ExampaperdownloadService exampaperdownloadService;

	@Autowired
	public void setUserBO(ExampaperdownloadService exampaperdownloadService) {
		this.exampaperdownloadService = exampaperdownloadService;
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public String test(@RequestParam(name = "siteId") String siteId,
			@RequestParam(name = "examTitle") String examTitle) {


		return "test";
	}

	@RequestMapping(value = "/gradebook", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public String getSubmissions(@RequestParam(name = "siteId") String siteId,
			@RequestParam(name = "examTitle") String examTitle) {

		List exampaperList = exampaperdownloadService.getExamPaper(siteId.toUpperCase(), examTitle.toUpperCase());

		if (exampaperList.size() > 0) {

			Iterator recordsGI_it = exampaperList.iterator();
			while (recordsGI_it.hasNext()) {
			
				ExamPaper examPaper=(ExamPaper)recordsGI_it.next();				 
				String assignmentId = examPaper.getAssignmentid();
				//System.out.println("assignmentId " + assignmentId);
				String module = examPaper.getModule();
				int successCount = 0;
				int failCount = 0;
				String successPapers = "Papers copied successfully: \n";
				String failPapers = "Papers not copied successfully: \n";

				 //part 2
				List recordsExamPapers = exampaperdownloadService.getSubmissions(assignmentId);
				Iterator recordsExamPapersIterator = recordsExamPapers.iterator();
				System.out.println(module+" ("+assignmentId+") in recordsExamPapers size " + recordsExamPapers.size());
				while (recordsExamPapersIterator.hasNext()) {
				
					ExamPaperDetails examPaperDetails=(ExamPaperDetails) recordsExamPapersIterator.next();				 
										
					String submissionId = examPaperDetails.getSubmissionId();
					String STUDENTNR = examPaperDetails.getStudentNr();
					String submitter = examPaperDetails.getSubmitter();
					String attachment = examPaperDetails.getAttachment();
					String filePath = examPaperDetails.getFilePath();
					//System.out.println("output submissionId" + submissionId+" STUDENTNR "+STUDENTNR+" attachment "+attachment);
					
					String[] bits = attachment.split("\\.");
					String fileExtension = bits[bits.length-1];
					
					String oldFilePath = "/data/sakai/content/"+filePath;
					String newFilePath = "/data/ExamPaperImport/myExams/"+module+"/"+STUDENTNR+"_"+module+"_80."+fileExtension;
					String createPath = "/data/ExamPaperImport/myExams/"+module;
					// file format: Studno(8) _ module(7) _ 80.???
					String fileName = STUDENTNR+"_"+module+"_80."+fileExtension;

					File source = new File(oldFilePath);  
			        File destinationFile = new File(newFilePath); 
					
					// check if student has multiple submissions then do not download
					Long numberOfSubmissions = exampaperdownloadService.getCountSubmissionPerStudent(assignmentId, submitter );
					if (numberOfSubmissions >= 2) {
						exampaperdownloadService.insertExamPaperLog(module, STUDENTNR, oldFilePath, "", false, "multiple uploads found for student ="+numberOfSubmissions);
					} 
					
						try {
							boolean success = copy(source, destinationFile, createPath, module, STUDENTNR, oldFilePath, newFilePath);
							//System.out.println("***** FILE COPIED SUCCESSFULLY ");
							if (success) {
								exampaperdownloadService.insertExamPaperLog(module, STUDENTNR, oldFilePath, newFilePath, true, "");
								successCount++;
								successPapers = successPapers+fileName+" ("+oldFilePath+") \n";
							}
						} catch (IOException e) { // Files.copy(source, dest);;
							/*String error = e.toString();
							error = error.substring(1,100);
							exampaperdownloadService.insertExamPaperLog(module, STUDENTNR, oldFilePath, newFilePath, false, error);*/
							e.printStackTrace();
							
							failCount++;
							failPapers = failPapers+module+fileName+" ("+oldFilePath+") "+ e +"\n";
							try {
								String heading = "MyExams: ERROR Exam paper Copy Module: "+module+" ("+assignmentId+")";
								String body = module+"=="+STUDENTNR+" error "+e;
								sendEmail(heading, body, "syzelle@unisa.ac.za");
							} catch (AddressException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
											}
							System.out.println("***** SY EXAMPAPERDOWNLOAD: error "+e);
							e.printStackTrace();
							
						} // end try */		       
				} // while (recordsExamPapersIterator.hasNext()) {
				
				try {
					String heading = "MyExams: Exam paper Copy Module: "+module+" ("+assignmentId+")";
					String body = "Files retrieved from myExams: "+recordsExamPapers.size()+" files. \n\n Number of papers copied successfuly: "+successCount+"\n\n"+successPapers;
					sendEmail(heading, body, "syzelle@unisa.ac.za");
				} catch (AddressException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					String heading = "ExamPaper Copy Module: "+module+" ("+assignmentId+")";
					String body = "Number of papers NOT copied successfuly: "+failCount+"\n\n"+failPapers;
					sendEmail(heading, body, "syzelle@unisa.ac.za");
				} catch (AddressException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//System.out.println("AssignmentId = " + assignmentId);
			}

		}

		return "success";
	}
	
	//throws FileNotFoundException, IOException
	public boolean copy ( File source,  File target, String createPath, String module, String stno, String oldFilePath, String newFilePath) 
			throws IOException {  
	        FileChannel sourceChannel = null;  
	        FileChannel targetChannel = null;  
			boolean success = false;
	        try {  
				// make directory
				File file = new File(createPath);
				boolean dirCreated = file.mkdir();
				System.out.println("EXAMPAPERDOWNLOAD dirCreated ="+dirCreated);
	            
				sourceChannel = new FileInputStream(source).getChannel();  
	            System.out.println(" sourceChannel >>"+sourceChannel);
	            targetChannel = new FileOutputStream(target).getChannel();
	            System.out.println(" targetChannel >>"+targetChannel);
	            //targetChannel.transferFrom(sourceChannel, 0,  
	            	//		sourceChannel.size());  
	            ByteBuffer buffer = ByteBuffer.allocate(1024);
	            while(sourceChannel.read(buffer) > 0) {
	                buffer.flip();
	                targetChannel.write(buffer);
	                buffer.compact();
	            }
				success = true;
				
	        } catch(IOException e) {    
				String error = e.toString();
				error = error.substring(1,100);
				exampaperdownloadService.insertExamPaperLog(module, stno, oldFilePath, newFilePath, false, error);
						
				System.err.format("EXAMPAPERDOWNLOAD I/O Error when copying file");
				e.printStackTrace();
				success = false;
	        }  finally {  
		        if (targetChannel != null) {
		        	targetChannel.close();  
		        }
		        if (targetChannel != null) {
		        	sourceChannel.close();
		        }
				//System.out.println("FINALLY == success "+success);
				return success;
	        }   //finally
	}// end  public static void copy ( File source,  File target)  
	
	public void sendEmail(String subject, String body, String emailAddress) throws AddressException {
		
		//Get the session object  
      Properties properties = System.getProperties();  
      //properties.setProperty("mail.smtp.host", "lazu-pstfx01pv.int.unisa.ac.za");  
	  properties.setProperty("mail.smtp.host", "smtp.sendgrid.net");  
      Session session = Session.getDefaultInstance(properties); 
	  
	  //compose the message  
      try{  
         MimeMessage message = new MimeMessage(session);  
         message.setFrom(new InternetAddress(emailAddress));  
         message.addRecipient(Message.RecipientType.TO,new InternetAddress(emailAddress));  
         message.setSubject(subject);  
         message.setText(body);  
  
         // Send message  
         Transport.send(message);  
         //System.out.println("message sent successfully....");  
  
      }catch (MessagingException mex) {
		  
		} //end try 
	  
	} // end of sendEmail

}