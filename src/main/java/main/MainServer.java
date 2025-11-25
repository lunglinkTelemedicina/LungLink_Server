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


import java.util.List;

public class MainServer {
    public static void main(String[] args) {

        //Start the connection manager so the database is created
        new JDBCConnectionManager();

//        JDBCDoctor jdbcDoctor = new JDBCDoctor();
//        List<Doctor> doctors = jdbcDoctor.getDoctors();
//
//        DoctorAssignmentService doctorAssignmentService = new DoctorAssignmentService(doctors);
//
//        for (Doctor d : doctors) {
//            System.out.println("DOCTOR >> " + d.getName() + " | " + d.getSpecialty());
//        }
        DoctorAssignmentService doctorAssignmentService = new DoctorAssignmentService();

        JDBCDoctor jdbcDoctor = new JDBCDoctor();
        List<Doctor> doctors = jdbcDoctor.getDoctors();

        for (Doctor d : doctors) {
            System.out.println("DOCTOR >> " + d.getName() + " | " + d.getSpecialty());
        }

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
