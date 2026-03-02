package com.oceanview.dao;

import com.oceanview.config.DatabaseConnection;
import com.oceanview.model.Guest;
import com.oceanview.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public boolean validateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int addGuest(Guest guest) {
        String query = "INSERT INTO guests (name, address, contact) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, guest.getName());
            pst.setString(2, guest.getAddress());
            pst.setString(3, guest.getContact());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean addReservation(Reservation res) {
        String query = "INSERT INTO reservations (guest_id, room_type, check_in, check_out, room_rate) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, res.getGuestId());
            pst.setString(2, res.getRoomType());
            pst.setDate(3, res.getCheckIn());
            pst.setDate(4, res.getCheckOut());
            pst.setDouble(5, res.getRoomRate());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        String query = "SELECT r.*, g.name, g.address, g.contact FROM reservations r JOIN guests g ON r.guest_id = g.guest_id";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pst = conn.prepareStatement(query)) {

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Reservation res = new Reservation();
                res.setResNo(rs.getInt("res_no"));
                res.setGuestId(rs.getInt("guest_id"));
                res.setRoomType(rs.getString("room_type"));
                res.setCheckIn(rs.getDate("check_in"));
                res.setCheckOut(rs.getDate("check_out"));
                res.setRoomRate(rs.getDouble("room_rate"));

                Guest guest = new Guest();
                guest.setGuestId(rs.getInt("guest_id"));
                guest.setName(rs.getString("name"));
                guest.setAddress(rs.getString("address"));
                guest.setContact(rs.getString("contact"));

                res.setGuest(guest);
                list.add(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Reservation getReservationById(int resNo) {
        String query = "SELECT r.*, g.name, g.address, g.contact FROM reservations r JOIN guests g ON r.guest_id = g.guest_id WHERE r.res_no = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, resNo);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Reservation res = new Reservation();
                res.setResNo(rs.getInt("res_no"));
                res.setGuestId(rs.getInt("guest_id"));
                res.setRoomType(rs.getString("room_type"));
                res.setCheckIn(rs.getDate("check_in"));
                res.setCheckOut(rs.getDate("check_out"));
                res.setRoomRate(rs.getDouble("room_rate"));

                Guest guest = new Guest();
                guest.setGuestId(rs.getInt("guest_id"));
                guest.setName(rs.getString("name"));
                guest.setAddress(rs.getString("address"));
                guest.setContact(rs.getString("contact"));

                res.setGuest(guest);
                return res;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateReservation(Reservation res, Guest guest) {
        String updateGuest = "UPDATE guests SET name=?, address=?, contact=? WHERE guest_id=?";
        String updateRes = "UPDATE reservations SET room_type=?, check_in=?, check_out=?, room_rate=? WHERE res_no=?";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        boolean success = false;
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement pst1 = conn.prepareStatement(updateGuest)) {
                pst1.setString(1, guest.getName());
                pst1.setString(2, guest.getAddress());
                pst1.setString(3, guest.getContact());
                pst1.setInt(4, guest.getGuestId());
                pst1.executeUpdate();
            }

            try (PreparedStatement pst2 = conn.prepareStatement(updateRes)) {
                pst2.setString(1, res.getRoomType());
                pst2.setDate(2, res.getCheckIn());
                pst2.setDate(3, res.getCheckOut());
                pst2.setDouble(4, res.getRoomRate());
                pst2.setInt(5, res.getResNo());
                success = pst2.executeUpdate() > 0;
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public boolean deleteReservation(int resNo) {
        String query = "DELETE FROM reservations WHERE res_no = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, resNo);
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
