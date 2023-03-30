package application;

import java.util.List;

public class GroupConversation {
	 
	private String groupName;
	    private List<String> members;

	    public GroupConversation(String groupName, List<String> members) {
	        this.groupName = groupName;
	        this.members = members;
	    }

	    public String getGroupName() {
	        return groupName;
	    }

	    public List<String> getMembers() {
	        return members;
	    }
	}


