package service;

import db.DBConnection;
import model.Bug;
import exception.BugAlreadyExistsException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BugService {

    public void addBugToDatabase(Bug bug) throws BugAlreadyExistsException {
        String query = "INSERT INTO bugs (id, title, description, status, severity) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            // ✅ Check if bug with same ID already exists
            if (bugExists(bug.getId())) {
                throw new BugAlreadyExistsException("Bug with ID " + bug.getId() + " already exists.");
            }

            stmt.setInt(1, bug.getId());
            stmt.setString(2, bug.getTitle());
            stmt.setString(3, bug.getDescription());
            stmt.setString(4, bug.getStatus());
            stmt.setString(5, bug.getSeverity());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Bug inserted successfully into database!");
            }

        } catch (SQLException e) {
            System.out.println("❌ SQL Error: " + e.getMessage());
        }
    }

    public List<Bug> getAllBugs() {
        List<Bug> bugList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM bugs")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String status = rs.getString("status");
                String severity = rs.getString("severity");

                Bug bug = new Bug(id, title, description, severity);
                bug.setStatus(status);
                bugList.add(bug);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching bugs: " + e.getMessage());
        }

        return bugList;
    }

    private boolean bugExists(int bugId) {
        String query = "SELECT id FROM bugs WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bugId);
            return stmt.executeQuery().next(); // ✅ returns true if record exists

        } catch (SQLException e) {
            System.out.println("❌ Error checking bug existence: " + e.getMessage());
            return false;
        }
    }

    public void updateBugStatusAndSeverity(int id, String newStatus, String newSeverity) {
        String checkQuery = "SELECT * FROM bugs WHERE id = ?";
        String updateQuery = "UPDATE bugs SET status = ?, severity = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("⚠️ No bug found with ID: " + id);
                return;
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, newStatus);
                updateStmt.setString(2, newSeverity);
                updateStmt.setInt(3, id);

                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("✅ Bug with ID " + id + " updated successfully!");
                } else {
                    System.out.println("⚠️ Bug update failed.");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error updating bug: " + e.getMessage());
        }
    }

    public void deleteBugById(int id) {
        String checkQuery = "SELECT * FROM bugs WHERE id = ?";
        String deleteQuery = "DELETE FROM bugs WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("⚠️ No bug found with ID: " + id);
                return;
            }

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, id);

                int rowsDeleted = deleteStmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("✅ Bug with ID " + id + " deleted successfully!");
                } else {
                    System.out.println("⚠️ Bug deletion failed.");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error deleting bug: " + e.getMessage());
        }
    }

}
