package com.oceanview.controller;

import com.oceanview.dao.ReservationDAO;
import com.oceanview.model.Guest;
import com.oceanview.model.Reservation;
import com.oceanview.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/reservation")
public class ReservationServlet extends HttpServlet {
    private ReservationDAO dao = new ReservationDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("index.html");
            return;
        }

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            String name = request.getParameter("name");
            String address = request.getParameter("address");
            String contact = request.getParameter("contact");
            String roomType = request.getParameter("roomType");
            Date checkIn = Date.valueOf(request.getParameter("checkIn"));
            Date checkOut = Date.valueOf(request.getParameter("checkOut"));
            double roomRate = Double.parseDouble(request.getParameter("roomRate"));

            // Get logged-in user from session
            User loggedInUser = (User) session.getAttribute("user");
            int addedBy = loggedInUser.getId();

            if (checkOut.before(checkIn) || checkOut.equals(checkIn)) {
                response.sendRedirect("dashboard.html?error=invalid_dates");
                return;
            }

            Guest guest = new Guest(0, name, address, contact);
            int guestId = dao.addGuest(guest);

            if (guestId != -1) {
                Reservation res = new Reservation(0, guestId, roomType, checkIn, checkOut, roomRate, addedBy);
                dao.addReservation(res);
            }
            response.sendRedirect("dashboard.html");
        } else if ("update".equals(action)) {
            int resNo = Integer.parseInt(request.getParameter("resNo"));
            int guestId = Integer.parseInt(request.getParameter("guestId"));
            String name = request.getParameter("name");
            String address = request.getParameter("address");
            String contact = request.getParameter("contact");
            String roomType = request.getParameter("roomType");
            Date checkIn = Date.valueOf(request.getParameter("checkIn"));
            Date checkOut = Date.valueOf(request.getParameter("checkOut"));
            double roomRate = Double.parseDouble(request.getParameter("roomRate"));

            if (checkOut.before(checkIn) || checkOut.equals(checkIn)) {
                response.sendRedirect("dashboard.html?error=invalid_dates");
                return;
            }

            Guest guest = new Guest(guestId, name, address, contact);
            Reservation res = new Reservation(resNo, guestId, roomType, checkIn, checkOut, roomRate, 0);

            dao.updateReservation(res, guest);
            response.sendRedirect("dashboard.html");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("index.html");
            return;
        }

        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            int resNo = Integer.parseInt(request.getParameter("resNo"));
            dao.deleteReservation(resNo);
            response.sendRedirect("dashboard.html");
        } else if ("list".equals(action)) {
            StringBuilder json = new StringBuilder("[");
            for (Reservation r : dao.getAllReservations()) {
                json.append("{")
                        .append("\"resNo\":").append(r.getResNo()).append(",")
                        .append("\"name\":\"").append(r.getGuest().getName()).append("\",")
                        .append("\"address\":\"").append(r.getGuest().getAddress()).append("\",")
                        .append("\"contact\":\"").append(r.getGuest().getContact()).append("\",")
                        .append("\"roomType\":\"").append(r.getRoomType()).append("\",")
                        .append("\"checkIn\":\"").append(r.getCheckIn().toString()).append("\",")
                        .append("\"checkOut\":\"").append(r.getCheckOut().toString()).append("\",")
                        .append("\"roomRate\":").append(r.getRoomRate()).append(",")
                        .append("\"guestId\":").append(r.getGuestId()).append(",")
                        .append("\"addedByUsername\":\"")
                        .append(r.getAddedByUsername() != null ? r.getAddedByUsername() : "Unknown").append("\"")
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
