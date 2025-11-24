package jdbc;

import java.io.File;
import java.sql.*;

public class JDBCConnectionManager {

    private static JDBCConnectionManager instance;

    public JDBCConnectionManager() {
        try {
            Class.forName("org.sqlite.JDBC");

            // Crear carpeta database si no existe
            File dbDirectory = new File("./database");
            if (!dbDirectory.exists()) {
                dbDirectory.mkdirs();
            }

            // Crear base solo UNA VEZ, NO en cada conexión
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:./database/lunglink.db")) {
                conn.createStatement().execute("PRAGMA foreign_keys = ON");
                createTables(conn);
            }

            System.out.println("LungLink database correctly initialized\n");

        } catch (Exception e) {
            throw new RuntimeException("Error initializing DB", e);
        }
    }

    public static synchronized JDBCConnectionManager getInstance() {
        if (instance == null) {
            instance = new JDBCConnectionManager();
        }
        return instance;
    }

    public Connection getConnection(){
        try{
            return DriverManager.getConnection("jdbc:sqlite:./database/lunglink.db");
        }catch(SQLException e){
            throw new RuntimeException("Can't get connection", e);
        }
    }



    public void createTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS user (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS doctor (
                    doctor_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    surname TEXT NOT NULL,
                    email TEXT NOT NULL,
                    specialty TEXT,                     
                    user_id INTEGER NOT NULL,          
                    FOREIGN KEY (user_id) REFERENCES user(id)  -- foreign key al usuario para el login
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS client (
                    client_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    surname TEXT NOT NULL,
                    dob DATE,
                    mail TEXT,
                    sex TEXT,
                    weight DOUBLE,
                    height DOUBLE,
                    doctor_id INTEGER,
                    user_id INTEGER NOT NULL,
                    FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id),  -- foreign key para el doctor asignado
                    FOREIGN KEY (user_id) REFERENCES user(id)  -- foreign key al usuario para el login
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS medicalhistory (
                    record_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    date TEXT NOT NULL,
                    client_id INTEGER NOT NULL,
                    doctor_id INTEGER,
                    observations TEXT,
                    symptomsList TEXT,
                    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE,
                    FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id) ON DELETE SET NULL
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS signal (
                     signal_id INTEGER PRIMARY KEY AUTOINCREMENT,
                     type TEXT NOT NULL,
                     signal_file TEXT,
                     record_id INTEGER NOT NULL,
                     FOREIGN KEY (record_id) REFERENCES medicalhistory(record_id)
                );
            """);

            System.out.println("Tables created or correctly verified");

        } catch (SQLException e) {
            if (e.getMessage().contains("already exist")) {
                System.out.println("Tables already exist");
            } else {
                System.out.println("Error while creating tables:");
                e.printStackTrace();
            }
        }
    }


}
