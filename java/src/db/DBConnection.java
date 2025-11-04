package src.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {

    private static final String URL = "jdbc:sqlite:lunglink.db";
    private static Connection conn = null;

    public static synchronized Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL);
            System.out.println("Conectado a SQLite: " + URL);
        }
        return conn;
    }

    public static synchronized void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}


