package com.example.whatsapp_practice;

import com.example.whatsapp_practice.model.Group;
import com.example.whatsapp_practice.model.Message;
import com.example.whatsapp_practice.model.User;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
public class WhatsappRepository {
    HashMap<String, User> userList = new HashMap<>();
    HashMap<Group, List<User>> groupUserMap = new HashMap<>();
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository() {
        this.userList = new HashMap<String, User>();
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobileNumber) throws Exception {
        if (userList.containsKey(mobileNumber)) throw new Exception("User already exists");
        User user = new User(name, mobileNumber);
        userList.put(mobileNumber, user);
        return "SUCCESS";
    }

    public Group createGroup(List<User> userList) {
        Group group = null;
        if (userList.size() == 2) group = new Group(userList.get(1).getName(), 2);
        else {
            customGroupCount++;
            group = new Group("Group " + customGroupCount, userList.size());
        }
        groupUserMap.put(group, userList);
        adminMap.put(group, userList.get(0));
        groupMessageMap.put(group, new ArrayList<>());
        return group;
    }

    public int createMessage(String messageContent) {
        messageId++;
        Message message = new Message(messageId, messageContent);
        return messageId;
    }

    public int sendMessage(Message messageContent, User sender, Group group) throws Exception {
        if (!adminMap.containsKey(group)) throw new Exception("Group does not exist");
        List<User> users = groupUserMap.get(group);
        Boolean isUserPresent = false;
        for (User user : users) {
            if (user.equals(sender)) {
                isUserPresent = true;
                break;
            }
        }
        if (isUserPresent) {
            senderMap.put(messageContent, sender);
            List<Message> messages = groupMessageMap.get(group);
            messages.add(messageContent);
            groupMessageMap.put(group, messages);
            return messages.size();
        }
        throw new Exception("You are not allowed to send message");
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if (!adminMap.containsKey(group)) throw new Exception("Group does not exist");
        if (!adminMap.get(group).equals(approver)) throw new Exception("Approver does not have rights");
        List<User> participants = groupUserMap.get(group);
        Boolean isUserPresent = false;
        for (User participant : participants) {
            if (participant.equals(user)) {
                isUserPresent = true;
                break;
            }
        }
        if (!isUserPresent) throw new Exception("User is not a participant");
        adminMap.put(group, user);
        //Not Mentioned in comments but necessary
        participants.remove(user);
        participants.add(0, user);
        return "SUCCESS";

    }

    public int removeUser(User user) throws Exception {
        Boolean isUserPresent = false;
        Group usersGroup = null;
        for (Group group : groupUserMap.keySet()) {
            List<User> participants = groupUserMap.get(group);
            for (User participant : participants) {
                if (participant.equals(user)) {
                    if (adminMap.get(group).equals(user)) throw new Exception("Cannot remove admin");
                    usersGroup = group;
                    isUserPresent = true;
                    break;
                }
            }
            if (isUserPresent) {
                break;
            }
        }
        if (isUserPresent) {
            List<User> users = groupUserMap.get(usersGroup);
            List<User> newUserList = new ArrayList<>();
            for (User participant : users) {
                if (participant.equals(user))
                    continue;
                newUserList.add(participant);
            }
            groupUserMap.put(usersGroup, newUserList);

            List<Message> messages = groupMessageMap.get(usersGroup);
            List<Message> updatedMessages = new ArrayList<>();
            for (Message message : messages) {
                if (!senderMap.get(message).equals(user)) updatedMessages.add(message);
            }
            groupMessageMap.put(usersGroup, updatedMessages);

            HashMap<Message, User> updatedSenderMap = new HashMap<>();
            for (Message message : senderMap.keySet()) {
                if (!senderMap.get(message).equals(user)) updatedSenderMap.put(message, senderMap.get(message));
            }
            senderMap = updatedSenderMap;
            return newUserList.size() + updatedMessages.size() + updatedSenderMap.size();
        }
        throw new Exception("User not found");
    }

    public String findMessage(Date start, Date end, int K) throws Exception {
        List<Message> messages = new ArrayList<>();
        for (Group group : groupMessageMap.keySet()) messages.addAll(groupMessageMap.get(group));
        List<Message> filteredMessages = new ArrayList<>();
        for (Message message : messages) {
            if (message.getTimestamp().after(start) && message.getTimestamp().before(end)) filteredMessages.add(message);
        }
        if (filteredMessages.size() < K) throw new Exception("K is greater than the number of messages");
        Collections.sort(filteredMessages, new Comparator<Message>() {
            public int compare(Message m1, Message m2) {
                return m2.getTimestamp().compareTo(m1.getTimestamp());
            }
        });
        return filteredMessages.get(K - 1).getContent();
    }
}
