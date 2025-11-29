package main;

import Network.*;
import jdbc.*;
import pojos.*;
import java.sql.Connection;
import java.util.List;

public class MainServer {
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
