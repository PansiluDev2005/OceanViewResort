package com.oceanview.dao;

import com.oceanview.config.DatabaseConnection;
import com.oceanview.model.Guest;
import com.oceanview.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public com.oceanview.model.User validateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new com.oceanview.model.User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(String username, String password, String role) {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, role);
            return pst.executeUpdate() > 0;

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
        String query = "INSERT INTO reservations (guest_id, room_type, check_in, check_out, room_rate, added_by) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, res.getGuestId());
            pst.setString(2, res.getRoomType());
            pst.setDate(3, res.getCheckIn());
            pst.setDate(4, res.getCheckOut());
            pst.setDouble(5, res.getRoomRate());
            if (res.getAddedBy() > 0) {
                pst.setInt(6, res.getAddedBy());
            } else {
                pst.setNull(6, Types.INTEGER);
            }

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        String query = "SELECT r.*, g.name, g.address, g.contact, u.username as added_by_username " +
                "FROM reservations r " +
                "JOIN guests g ON r.guest_id = g.guest_id " +
                "LEFT JOIN users u ON r.added_by = u.id ORDER BY r.res_no DESC";

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
                res.setAddedBy(rs.getInt("added_by"));
                res.setAddedByUsername(rs.getString("added_by_username"));

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
        String query = "SELECT r.*, g.name, g.address, g.contact, u.username as added_by_username " +
                "FROM reservations r " +
                "JOIN guests g ON r.guest_id = g.guest_id " +
                "LEFT JOIN users u ON r.added_by = u.id " +
                "WHERE r.res_no = ?";
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
                res.setAddedBy(rs.getInt("added_by"));
                res.setAddedByUsername(rs.getString("added_by_username"));

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

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            if (conn == null)
                return false;
            conn.setAutoCommit(false);
            try {
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
                    pst2.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

    public List<com.oceanview.model.User> getAllStaff() {
        List<com.oceanview.model.User> list = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = 'staff'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pst = conn.prepareStatement(query)) {

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(new com.oceanview.model.User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateUser(com.oceanview.model.User user) {
        String query = "UPDATE users SET username=?, password=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, user.getUsername());
            pst.setString(2, user.getPassword());
            pst.setInt(3, user.getId());
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, userId);
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
