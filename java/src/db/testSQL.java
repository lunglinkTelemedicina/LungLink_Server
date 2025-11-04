package src.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class testSQL {

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:lunglink.db")) {
            System.out.println("✅ Conexión a SQLite exitosa");
        } catch (Exception e) {
            System.out.println("❌ Error al conectar con la BD");
            e.printStackTrace();
        }
    }

}
