package org.sakaiproject.yaft.impl;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.util.SiteEmailNotification;
import org.sakaiproject.yaft.api.Discussion;
import org.sakaiproject.yaft.api.Group;
import org.sakaiproject.yaft.api.SakaiProxy;
import org.sakaiproject.yaft.api.YaftFunctions;

public class NewDiscussionNotification extends SiteEmailNotification {
	
	private static ResourceLoader rb = new ResourceLoader("org.sakaiproject.yaft.impl.bundle.newdiscussionnotification");
	
	private SakaiProxy sakaiProxy = null;
	
	public NewDiscussionNotification() {}
	
    public NewDiscussionNotification(String siteId) {
        super(siteId);
    }
    
    public void setSakaiProxy(SakaiProxy sakaiProxy) {
    	this.sakaiProxy = sakaiProxy;
    }
    
    protected String getFromAddress(Event event) {

		Reference ref = EntityManager.newReference(event.getResource());
        Discussion discussion = (Discussion) ref.getEntity();

        if (discussion.isAnonymous()) {
            // If this is an anonymous first message, override the default.
            String noReplyAddress = "no-reply@" + ServerConfigurationService.getServerName();
            String noReplyUser = ServerConfigurationService.getString("ui.service", "Sakai");
            return "From: \"" + noReplyUser + "\" <" + noReplyAddress + ">";
        } else {
            return getFrom(event);
        }
    }
    
	protected String plainTextContent(Event event) {

		Reference ref = EntityManager.newReference(event.getResource());
        Discussion discussion = (Discussion) ref.getEntity();

        String creatorName = "";
        if (discussion.isAnonymous()) {
            creatorName = "Anonymous";
        } else {
            try {
                creatorName = UserDirectoryService.getUser(discussion.getCreatorId()).getDisplayName();
            } catch (UserNotDefinedException e) {
                e.printStackTrace();
            }
        }
		
		return rb.getFormattedMessage("noti.neworupdateddiscussion", new Object[]{creatorName,discussion.getSubject(),ServerConfigurationService.getServerUrl() + discussion.getUrl()});
	}
	
	protected String getSubject(Event event) {

		Reference ref = EntityManager.newReference(event.getResource());
        Discussion discussion = (Discussion) ref.getEntity();
        
        String siteTitle = "";
		try {
			siteTitle = SiteService.getSite(discussion.getSiteId()).getTitle();
		} catch (IdUnusedException e) {
			e.printStackTrace();
		}
        
        return rb.getFormattedMessage("noti.subject", new Object[]{siteTitle, discussion.getSubject()});
	}
	
	protected List<User> getRecipients(Event event) {

		Reference ref = EntityManager.newReference(event.getResource());
        Discussion discussion = (Discussion) ref.getEntity();
        
        List<User> users = new ArrayList<User>();
        
        List<Group> groups = discussion.getGroups();
        if (groups.size() > 0) {
        	// This discussion is limited to groups. Make sure the alert only goes
		    // to the group members
		    users = sakaiProxy.getGroupUsers(groups);
		    
		    // Maintainers need to get emails also.
		    users.addAll(sakaiProxy.getCurrentSiteMaintainers());
        }
        else {
        	users = super.getRecipients(event);
        }
	    
	    return users;
	}
	
	protected String getTag(String title, boolean shouldUseHtml) {
		return rb.getFormattedMessage("noti.tag", new Object[]{ServerConfigurationService.getString("ui.service", "Sakai"), ServerConfigurationService.getPortalUrl(), title});
    }
	
	protected List getHeaders(Event event) {

        List rv = super.getHeaders(event);
        rv.add("Subject: " + getSubject(event));
        rv.add(getFromAddress(event));
        rv.add(getTo(event));
        return rv;
    }
}
