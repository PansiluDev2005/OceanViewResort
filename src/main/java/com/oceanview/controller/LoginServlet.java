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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private ReservationDAO dao = new ReservationDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String user = request.getParameter("username");
        String pass = request.getParameter("password");

        User authenticatedUser = dao.validateUser(user, pass);
        if (authenticatedUser != null) {
            HttpSession session = request.getSession();
            // Store the whole User object to easily access ID and Role later
            session.setAttribute("user", authenticatedUser);
            response.sendRedirect("dashboard.html");
        } else {
            response.sendRedirect("index.html?error=1");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect("index.html");
        }
    }
}
