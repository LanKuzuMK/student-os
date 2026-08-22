package com.studentos.model;
public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private int counterpartId;
    private String content;
    private String counterpartEmail;
    private boolean counterpartBlocked;
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public int getSenderId() { return senderId; } public void setSenderId(int senderId) { this.senderId = senderId; }
    public int getReceiverId() { return receiverId; } public void setReceiverId(int receiverId) { this.receiverId = receiverId; }
    public int getCounterpartId() { return counterpartId; } public void setCounterpartId(int counterpartId) { this.counterpartId = counterpartId; }
    public String getContent() { return content; } public void setContent(String content) { this.content = content; }
    public String getCounterpartEmail() { return counterpartEmail; } public void setCounterpartEmail(String counterpartEmail) { this.counterpartEmail = counterpartEmail; }
    public boolean isCounterpartBlocked() { return counterpartBlocked; } public void setCounterpartBlocked(boolean counterpartBlocked) { this.counterpartBlocked = counterpartBlocked; }
}
