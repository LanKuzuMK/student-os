package com.studentos.util;

import com.studentos.model.DashboardAction;
import com.studentos.model.DashboardSummary;

/** Selects a single student-owned action, keeping the dashboard useful rather than overwhelming. */
public final class DashboardCoach {
    private DashboardCoach() { }

    public static DashboardAction nextAction(DashboardSummary summary) {
        if (summary.getIncomingCollaborationCount() > 0) return new DashboardAction("Collaboration waiting", "Review a collaboration proposal", "A classmate is waiting for your response. Review the goal and decide when you are ready.", "Review proposals", "/collaborations");
        if (summary.getUnreadMessageCount() > 0) return new DashboardAction("Conversation waiting", "Read your new messages", "A classmate has sent a message. Keep the conversation moving with a clear reply.", "Open messages", "/messages");
        if (summary.getProfileCompletion() < 60) return new DashboardAction("Profile momentum", "Make your profile easier to discover", "Add a little more about your skills, availability, or the work you want to build with others.", "Complete profile", "/profile");
        if (summary.getActiveTaskCount() == 0) return new DashboardAction("Focus for today", "Plan one meaningful task", "A focused task is the easiest way to turn your goals into visible progress.", "Create a task", "/schedule");
        if (summary.getGoalCount() == 0) return new DashboardAction("Direction", "Set a goal to guide your work", "Give your tasks a bigger purpose by defining what you want to achieve next.", "Manage goals", "/goals");
        if (summary.getSkillCount() == 0) return new DashboardAction("Community profile", "Share a skill with classmates", "Tell the community what you can teach or what you want to learn.", "Add a skill", "/skills");
        return new DashboardAction("Keep momentum", "Continue your focused work", "Your workspace is set up. Choose one open task and make a small, useful move today.", "View schedule", "/schedule");
    }
}
