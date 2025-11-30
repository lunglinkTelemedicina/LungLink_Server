package main;

import Network.*;
import jdbc.*;
import pojos.*;
import java.sql.Connection;
import java.util.List;

/**
 * Clase principal que inicializa y arranca el servidor de LungLink.
 * * <p>Se encarga de establecer la conexión con la base de datos,
 * asegurar la existencia de los perfiles de usuario y médico por defecto,
 * e iniciar la conexión de red para escuchar a los clientes.</p>
 */
public class MainServer {
    /**
     * Punto de entrada de la aplicación del servidor.
     * * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {

        //starting connection with database
        JDBCConnectionManager cm = JDBCConnectionManager.getInstance();
        Connection conn = cm.getConnection();

        JDBCUser.getInstance().insertDefaultDoctorUser(conn);
        JDBCDoctor.getInstance().insertDoctorByDefault(conn);

        DoctorAssignmentService doctorAssignmentService = new DoctorAssignmentService();

        JDBCDoctor jdbcDoctor = new JDBCDoctor();
        List<Doctor> doctors = jdbcDoctor.getDoctors();


        int port = 9000;
        //Create the server socket
        ServerConnection server = new ServerConnection(port, doctorAssignmentService);

        //Run the server on a separate thread
        Thread serverThread = new Thread(server::start);
        serverThread.start();

        ServerUI ui = new ServerUI(server);
        ui.start();
    }
}
