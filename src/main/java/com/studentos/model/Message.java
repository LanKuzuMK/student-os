package com.studentos.model;
public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    private String counterpartEmail;
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public int getSenderId() { return senderId; } public void setSenderId(int senderId) { this.senderId = senderId; }
    public int getReceiverId() { return receiverId; } public void setReceiverId(int receiverId) { this.receiverId = receiverId; }
    public String getContent() { return content; } public void setContent(String content) { this.content = content; }
    public String getCounterpartEmail() { return counterpartEmail; } public void setCounterpartEmail(String counterpartEmail) { this.counterpartEmail = counterpartEmail; }
}
