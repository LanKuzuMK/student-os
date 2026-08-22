package com.studentos.controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import com.studentos.dao.TaskDAO;
import com.studentos.dao.GoalDAO;
import com.studentos.dao.ProfileDAO;
import com.studentos.dao.SkillDAO;
import com.studentos.dao.MessageDAO;
import com.studentos.dao.NotificationDAO;
import com.studentos.dao.CollaborationRequestDAO;
import com.studentos.model.CollaborationRequest;
import com.studentos.model.DashboardSummary;
import com.studentos.model.Goal;
import com.studentos.model.Profile;
import com.studentos.model.Task;
import com.studentos.model.User;
import com.studentos.util.DashboardCoach;
import java.util.List;

public class DashboardController extends HttpServlet {
    private TaskDAO taskDAO = new TaskDAO();
    private final GoalDAO goalDAO = new GoalDAO();
    private final ProfileDAO profileDAO = new ProfileDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final MessageDAO messageDAO = new MessageDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final CollaborationRequestDAO collaborationRequestDAO = new CollaborationRequestDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        List<Task> tasks = taskDAO.getTasksByUserId(user.getId());
        List<Goal> goals = goalDAO.getGoalsByUserId(user.getId());
        List<CollaborationRequest> collaborationRequests = collaborationRequestDAO.getForUser(user.getId());
        Profile profile = profileDAO.getByUserId(user.getId());
        DashboardSummary summary = buildSummary(tasks, goals, collaborationRequests, profile, user.getId());
        request.setAttribute("tasks", tasks);
        request.setAttribute("goals", goals);
        request.setAttribute("collaborationRequests", collaborationRequests);
        request.setAttribute("dashboardSummary", summary);
        request.setAttribute("nextAction", DashboardCoach.nextAction(summary));
        request.setAttribute("dashboardProfile", profile);
        request.setAttribute("unreadMessageCount", summary.getUnreadMessageCount());
        request.setAttribute("unreadNotificationCount", summary.getUnreadNotificationCount());
        request.getRequestDispatcher("/views/dashboard/index.jsp").forward(request, response);
    }

    private DashboardSummary buildSummary(List<Task> tasks, List<Goal> goals, List<CollaborationRequest> collaborations, Profile profile, int userId) {
        DashboardSummary summary = new DashboardSummary();
        int activeTasks = 0, completedTasks = 0;
        for (Task task : tasks) { if ("COMPLETED".equals(task.getStatus())) completedTasks++; else activeTasks++; }
        int totalProgress = 0;
        for (Goal goal : goals) totalProgress += goal.getProgress();
        int incoming = 0, outgoing = 0;
        for (CollaborationRequest collaboration : collaborations) {
            if ("PENDING".equals(collaboration.getStatus())) { if (collaboration.isIncoming()) incoming++; else outgoing++; }
        }
        summary.setActiveTaskCount(activeTasks); summary.setCompletedTaskCount(completedTasks); summary.setGoalCount(goals.size());
        summary.setAverageGoalProgress(goals.isEmpty() ? 0 : totalProgress / goals.size()); summary.setSkillCount(skillDAO.getSkillsByUserId(userId).size());
        summary.setUnreadMessageCount(messageDAO.countUnreadMessagesForUser(userId)); summary.setUnreadNotificationCount(notificationDAO.countUnreadForRecipient(userId));
        summary.setIncomingCollaborationCount(incoming); summary.setOutgoingCollaborationCount(outgoing); summary.setProfileCompletion(profileCompletion(profile));
        return summary;
    }

    private int profileCompletion(Profile profile) {
        if (profile == null) return 0;
        int completed = 0;
        if (profile.getFirstName() != null) completed++;
        if (profile.getBio() != null) completed++;
        if (profile.getUniversity() != null || profile.getMajor() != null) completed++;
        if (profile.getAvailabilityStatus() != null) completed++;
        if (profile.getPortfolioUrl() != null || profile.getLinkedinUrl() != null || profile.getTelegramUrl() != null) completed++;
        return completed * 20;
    }
}
