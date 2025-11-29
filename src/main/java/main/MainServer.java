package main;
import Network.*;
import Network.ServerUI;
import jdbc.JDBCClient;
import jdbc.JDBCMedicalHistory;
import jdbc.JDBCSignal;
import jdbc.JDBCDoctor;
import jdbc.JDBCUser;
import pojos.Doctor;
import jdbc.JDBCConnectionManager;


import java.sql.Connection;
import java.util.List;
//aqui teneis que estar
public class MainServer {
    public static void main(String[] args) {

        //Start the connection manager so the database is created
        //JDBCConnectionManager.getInstance();
//        JDBCUser.getInstance().insertDefaultDoctorUser();
//        JDBCDoctor.getInstance().insertDoctorByDefault();

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

        //Start admin interface (console UI)
        ServerUI ui = new ServerUI(server);
        ui.start();
    }
}
