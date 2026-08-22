package com.studentos.controller;
import com.studentos.dao.SkillDAO;
import com.studentos.model.Skill;
import com.studentos.model.User;
import com.studentos.util.DiscoveryPolicy;
import com.studentos.util.InputValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/skills/*")
public class SkillController extends HttpServlet {
    private SkillDAO skillDAO = new SkillDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if ("/discover".equals(path)) {
            User user = (User) request.getSession().getAttribute("user");
            String query = InputValidator.trimToLength(request.getParameter("q"), 100);
            String type = DiscoveryPolicy.skillType(request.getParameter("type"));
            String level = DiscoveryPolicy.level(request.getParameter("level"));
            String availability = DiscoveryPolicy.availability(request.getParameter("availability"));
            String sort = DiscoveryPolicy.sort(request.getParameter("sort"));
            int page = page(request.getParameter("page"));
            int pageSize = 12;
            int total = skillDAO.countPublicSkills(query, type, level, availability);
            request.setAttribute("allSkills", skillDAO.searchPublicSkills(query, type, level, availability, sort, pageSize, (page - 1) * pageSize));
            request.setAttribute("currentUserId", user.getId());
            request.setAttribute("discoverQuery", query);
            request.setAttribute("discoverType", type);
            request.setAttribute("discoverLevel", level);
            request.setAttribute("discoverAvailability", availability);
            request.setAttribute("discoverSort", sort);
            request.setAttribute("discoverCount", total);
            request.setAttribute("discoverPage", page);
            request.setAttribute("discoverPages", Math.max(1, (int) Math.ceil(total / (double) pageSize)));
            request.getRequestDispatcher("/views/skills/discover.jsp").forward(request, response);
        } else {
            User user = (User) request.getSession().getAttribute("user");
            request.setAttribute("mySkills", skillDAO.getSkillsByUserId(user.getId()));
            request.getRequestDispatcher("/views/skills/index.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        String path = request.getPathInfo();
        
        if ("/add".equals(path)) {
            Skill skill = new Skill();
            skill.setUserId(user.getId());
            skill.setSkillName(request.getParameter("skillName"));
            skill.setSkillLevel(request.getParameter("skillLevel"));
            skill.setType(request.getParameter("type"));
            skillDAO.addSkill(skill);
            response.sendRedirect(request.getContextPath() + "/skills");
        }
    }

    private int page(String value) {
        try { return Math.max(1, Integer.parseInt(value)); }
        catch (NumberFormatException e) { return 1; }
    }
}
