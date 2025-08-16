package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/bugtrackai";
        String user = "root";
        String password = "Sairam@3824"; // change this

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // ✅ explicitly load driver
        } catch (ClassNotFoundException e) {
            System.out.println("❌ JDBC Driver not found: " + e.getMessage());
        }

        return DriverManager.getConnection(url, user, password);
    }
}
