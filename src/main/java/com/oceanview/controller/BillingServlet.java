package com.oceanview.controller;

import com.oceanview.dao.ReservationDAO;
import com.oceanview.model.Reservation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.temporal.ChronoUnit;

@WebServlet("/billing")
public class BillingServlet extends HttpServlet {
    private ReservationDAO dao = new ReservationDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("index.html");
            return;
        }

        String resNoParam = request.getParameter("resNo");
        if (resNoParam != null && !resNoParam.isEmpty()) {
            int resNo = Integer.parseInt(resNoParam);
            Reservation res = dao.getReservationById(resNo);

            if (res != null) {
                long nights = ChronoUnit.DAYS.between(res.getCheckIn().toLocalDate(), res.getCheckOut().toLocalDate());
                if (nights <= 0)
                    nights = 1; // Minimum 1 night

                double totalBill = nights * res.getRoomRate();

                response.setContentType("text/html");
                response.setCharacterEncoding("UTF-8");
                String html = "<!DOCTYPE html><html><head><title>Print Bill</title>" +
                        "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>"
                        +
                        "</head><body class='container mt-5'>" +
                        "<div class='card print-section'>" +
                        "<div class='card-header text-center'><h2>Ocean View Resort - Invoice</h2></div>" +
                        "<div class='card-body'>" +
                        "<p><strong>Reservation No:</strong> " + res.getResNo() + "</p>" +
                        "<p><strong>Guest Name:</strong> " + res.getGuest().getName() + "</p>" +
                        "<p><strong>Contact:</strong> " + res.getGuest().getContact() + "</p>" +
                        "<p><strong>Room Type:</strong> " + res.getRoomType() + "</p>" +
                        "<p><strong>Check-in:</strong> " + res.getCheckIn() + "</p>" +
                        "<p><strong>Check-out:</strong> " + res.getCheckOut() + "</p>" +
                        "<hr/>" +
                        "<p><strong>Room Rate:</strong> $" + String.format("%.2f", res.getRoomRate()) + " / night</p>" +
                        "<p><strong>Total Nights:</strong> " + nights + "</p>" +
                        "<h4><strong>Total Amount Due:</strong> $" + String.format("%.2f", totalBill) + "</h4>" +
                        "</div>" +
                        "<div class='card-footer text-center'>" +
                        "<button onclick='window.print()' class='btn btn-primary d-print-none'>Print Bill</button> " +
                        "<a href='dashboard.html' class='btn btn-secondary d-print-none'>Back to Dashboard</a>" +
                        "</div></div></body></html>";
                response.getWriter().write(html);
                return;
            }
        }
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("Invalid Reservation ID");
    }
}
