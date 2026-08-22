package com.studentos.model;

/** Aggregated, signed-in-student-only dashboard data; it never represents other students' private activity. */
public class DashboardSummary {
    private int activeTaskCount;
    private int completedTaskCount;
    private int goalCount;
    private int averageGoalProgress;
    private int skillCount;
    private int unreadMessageCount;
    private int unreadNotificationCount;
    private int incomingCollaborationCount;
    private int outgoingCollaborationCount;
    private int profileCompletion;

    public int getActiveTaskCount() { return activeTaskCount; }
    public void setActiveTaskCount(int activeTaskCount) { this.activeTaskCount = activeTaskCount; }
    public int getCompletedTaskCount() { return completedTaskCount; }
    public void setCompletedTaskCount(int completedTaskCount) { this.completedTaskCount = completedTaskCount; }
    public int getGoalCount() { return goalCount; }
    public void setGoalCount(int goalCount) { this.goalCount = goalCount; }
    public int getAverageGoalProgress() { return averageGoalProgress; }
    public void setAverageGoalProgress(int averageGoalProgress) { this.averageGoalProgress = averageGoalProgress; }
    public int getSkillCount() { return skillCount; }
    public void setSkillCount(int skillCount) { this.skillCount = skillCount; }
    public int getUnreadMessageCount() { return unreadMessageCount; }
    public void setUnreadMessageCount(int unreadMessageCount) { this.unreadMessageCount = unreadMessageCount; }
    public int getUnreadNotificationCount() { return unreadNotificationCount; }
    public void setUnreadNotificationCount(int unreadNotificationCount) { this.unreadNotificationCount = unreadNotificationCount; }
    public int getIncomingCollaborationCount() { return incomingCollaborationCount; }
    public void setIncomingCollaborationCount(int incomingCollaborationCount) { this.incomingCollaborationCount = incomingCollaborationCount; }
    public int getOutgoingCollaborationCount() { return outgoingCollaborationCount; }
    public void setOutgoingCollaborationCount(int outgoingCollaborationCount) { this.outgoingCollaborationCount = outgoingCollaborationCount; }
    public int getProfileCompletion() { return profileCompletion; }
    public void setProfileCompletion(int profileCompletion) { this.profileCompletion = profileCompletion; }
}
