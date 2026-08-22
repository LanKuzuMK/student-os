package com.studentos.util;

import com.studentos.model.DashboardSummary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DashboardCoachTest {
    @Test void prioritizesIncomingCollaborationBeforeOtherActivity() {
        DashboardSummary summary = readySummary();
        summary.setIncomingCollaborationCount(1);
        summary.setUnreadMessageCount(3);
        assertEquals("/collaborations", DashboardCoach.nextAction(summary).getUrl());
    }

    @Test void guidesIncompleteProfilesBeforePlanningMoreWork() {
        DashboardSummary summary = new DashboardSummary();
        summary.setProfileCompletion(20);
        assertEquals("/profile", DashboardCoach.nextAction(summary).getUrl());
    }

    @Test void guidesStudentsToTasksWhenTheirWorkspaceIsOtherwiseReady() {
        DashboardSummary summary = readySummary();
        summary.setActiveTaskCount(0);
        assertEquals("/schedule", DashboardCoach.nextAction(summary).getUrl());
    }

    private DashboardSummary readySummary() {
        DashboardSummary summary = new DashboardSummary();
        summary.setProfileCompletion(100);
        summary.setActiveTaskCount(2);
        summary.setGoalCount(1);
        summary.setSkillCount(1);
        return summary;
    }
}
