package com.oceanview.controller;

import com.oceanview.dao.ReservationDAO;
import com.oceanview.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private ReservationDAO dao = new ReservationDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Authorization check: Must be admin
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("index.html");
            return;
        }
        User sessionUser = (User) session.getAttribute("user");
        if (!"admin".equals(sessionUser.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required.");
            return;
        }

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            boolean success = dao.registerUser(username, password, "staff");
            if (!success) {
                response.sendRedirect("dashboard.html?error=add_staff_failed");
                return;
            }
            response.sendRedirect("dashboard.html");

        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            User u = new User(id, username, password, "staff");
            dao.updateUser(u);
            response.sendRedirect("dashboard.html");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Authorization check: Must be admin
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("index.html");
            return;
        }
        User sessionUser = (User) session.getAttribute("user");

        String action = request.getParameter("action");

        if ("session-role".equals(action)) {
            // Return current user's role and username for UI toggles
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    "{\"role\":\"" + sessionUser.getRole() + "\", \"username\":\"" + sessionUser.getUsername() + "\"}");
            return;
        }

        if (!"admin".equals(sessionUser.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required.");
            return;
        }

        if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            dao.deleteUser(id);
            response.sendRedirect("dashboard.html");

        } else if ("list".equals(action)) {
            StringBuilder json = new StringBuilder("[");
            for (User u : dao.getAllStaff()) {
                json.append("{")
                        .append("\"id\":").append(u.getId()).append(",")
                        .append("\"username\":\"").append(u.getUsername()).append("\",")
                        .append("\"password\":\"").append(u.getPassword()).append("\"")
                        .append("},");
            }
            if (json.length() > 1) {
                json.setLength(json.length() - 1); // remove last comma
            }
            json.append("]");

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json.toString());
        }
    }
}
