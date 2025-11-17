package jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JDBCConnectionManager {

    private Connection c = null;

    public JDBCConnectionManager() {
        try {
            // Cargar el driver JDBC de SQLite
            Class.forName("org.sqlite.JDBC");

            // Crear el directorio "database" si no existe
            File dbDirectory = new File("./database");
            if (!dbDirectory.exists() && !dbDirectory.mkdirs()) {
                throw new IOException("No se pudo crear el directorio ./database");
            }

            // Establecer conexión con la base de datos
            c = DriverManager.getConnection("jdbc:sqlite:./database/lunglink.db");

            // Activar claves foráneas
            c.createStatement().execute("PRAGMA foreign_keys=ON");

            System.out.println("Conexión establecida con la base de datos LungLink.");
            createTables();

        } catch (ClassNotFoundException e) {
            System.out.println("No se cargó el driver JDBC de SQLite.");
        } catch (SQLException ex) {
            Logger.getLogger(JDBCConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JDBCConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createTables() {
        try (Statement stmt = c.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE user (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password BLOB NOT NULL
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE doctor (
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
                CREATE TABLE client (
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
                    client_id INTEGER,
                    doctor_id INTEGER,
                    signalEMG TEXT,
                    signalECG TEXT,
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
                     values TEXT NOT NULL,
                     signal_file TEXT,             
                     sampling_rate INTEGER DEFAULT 100,
                     client_id INTEGER NOT NULL,
                     FOREIGN KEY (client_id) REFERENCES client(client_id)
                );
            """);

            System.out.println("Tablas creadas o verificadas correctamente.");

        } catch (SQLException e) {
            if (e.getMessage().contains("already exist")) {
                System.out.println("Las tablas ya existen.");
            } else {
                System.out.println("Error al crear las tablas:");
                e.printStackTrace();
            }
        }
    }


    public Connection getConnection() {
        return c;
    }


    public void disconnect() {
        try {
            if (c != null && !c.isClosed()) {
                c.close();
                System.out.println("Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión:");
            e.printStackTrace();
        }
    }
}
