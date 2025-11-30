package jdbc;

import java.io.File;
import java.sql.*;
/**
 * Manages the connection to the SQLite database.
 * * <p>This class follows the **Singleton pattern** to ensure that the database
 * driver is loaded, the database file is created, and the schema (tables)
 * is initialized only once throughout the application lifecycle.</p>
 */
public class JDBCConnectionManager {

    private static JDBCConnectionManager instance;
    /**
     * Private constructor to prevent direct instantiation (Singleton pattern).
     * * <p>Initializes the SQLite JDBC driver, creates the database directory (if necessary),
     * and calls {@link #createTables(Connection)} to ensure the database schema is ready.</p>
     * * @throws RuntimeException if there is an error initializing the database (e.g., driver not found).
     */
    private JDBCConnectionManager() {
        try {
            Class.forName("org.sqlite.JDBC");

            //Create database folder if it does not exist
            File dbDirectory = new File("./database");
            if (!dbDirectory.exists()) {
                dbDirectory.mkdirs();
            }

            //Create the database only once, not every time we connect
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:./database/lunglink.db")) {
                conn.createStatement().execute("PRAGMA foreign_keys = ON");
                createTables(conn);

            }

            System.out.println("LungLink database correctly initialized\n");

        } catch (Exception e) {
            throw new RuntimeException("Error initializing DB", e);
        }

    }
    /**
     * Gets the single instance of the JDBCConnectionManager (Singleton).
     *
     * @return The instance of JDBCConnectionManager.
     */
    public static synchronized JDBCConnectionManager getInstance() {
        if (instance == null) {
            instance = new JDBCConnectionManager();
        }
        return instance;
    }
    /**
     * Retrieves a new {@link Connection} object for interacting with the database.
     *
     * @return A new active database connection.
     * @throws RuntimeException if the connection cannot be established.
     */
    public Connection getConnection(){
        try{
            return DriverManager.getConnection("jdbc:sqlite:./database/lunglink.db");
        }catch(SQLException e){
            throw new RuntimeException("Can't get connection", e);
        }
    }


    /**
     * Creates all necessary tables in the database if they do not already exist.
     * * <p>Tables created: user, doctor, client, medicalhistory, signal.</p>
     *
     * @param conn The active database connection used to execute the CREATE TABLE statements.
     */
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
                    doctor_id INTEGER NULL, 
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
