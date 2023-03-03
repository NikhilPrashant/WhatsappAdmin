package com.example.whatsapp_practice.model;

public class Group {
    private String groupName;
    private int noOfParticipants;

    public Group(String groupName, int noOfParticipants) {
        this.groupName = groupName;
        this.noOfParticipants = noOfParticipants;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getNoOfParticipants() {
        return noOfParticipants;
    }

    public void setNoOfParticipants(int noOfParticipants) {
        this.noOfParticipants = noOfParticipants;
    }
}
