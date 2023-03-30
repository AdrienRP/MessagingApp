package Requests;

import java.util.ArrayList;

public class CreateGroupRequest extends Request {
    private String groupName;
    private ArrayList<String> members;

    public CreateGroupRequest(String groupName, ArrayList<String> members) {
        super("CreateGroupRequest");
        this.groupName = groupName;
        this.members = members;
    }

    public String getGroupName() {
        return groupName;
    }

    public ArrayList<String> getMembers() {
        return members;
    }
}