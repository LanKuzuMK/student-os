package com.studentos.controller;
import com.studentos.dao.SkillDAO;
import com.studentos.model.Skill;
import com.studentos.model.User;
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
            request.setAttribute("allSkills", skillDAO.getAllSkills());
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
}
