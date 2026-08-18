package com.studentos.model;
import java.sql.Timestamp;
public class Skill {
    private int id;
    private int userId;
    private String skillName;
    private String skillLevel;
    private String type; // TEACH or LEARN
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; } public void setUserId(int userId) { this.userId = userId; }
    public String getSkillName() { return skillName; } public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getSkillLevel() { return skillLevel; } public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }
    public String getType() { return type; } public void setType(String type) { this.type = type; }
}
