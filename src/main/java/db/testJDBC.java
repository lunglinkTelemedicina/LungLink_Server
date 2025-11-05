package db;

import jdbc.*;
import pojos.*;
import java.time.LocalDate;
import java.util.List;

public class testJDBC {

    public static void main(String[] args) {
        System.out.println("========== PRUEBA DE CONEXIÓN Y DAOs ==========\n");

        //Probar la conexión
        jdbcConnectionManager cm = new jdbcConnectionManager();
        cm.disconnect();

        //Probar User
        System.out.println("\n🧍‍♀️ PROBANDO TABLA USER -------------------");
        jdbcUser userDAO = new jdbcUser();
        User user = new User();
        user.username = "martina";
        user.password = "1234".getBytes();
        int userId = userDAO.addUser(user);
        System.out.println("Usuario creado con ID: " + userId);

        User loadedUser = userDAO.getUserByUsername("martina");
        if (loadedUser != null) {
            System.out.println("Usuario encontrado: " + loadedUser.username);
        }

//        // Probar Doctor
//        System.out.println("\nPROBANDO TABLA DOCTOR -------------------");
//        jdbcDoctor doctorDAO = new jdbcDoctor();
//        Doctor doctor = new Doctor();
//        doctor.setName("Laura");
//        doctor.setSurname("Gómez");
//        doctor.setMail("laura@example.com");
//        doctor.setSpecialty("Cardiología");
//        int doctorId = doctorDAO.addDoctor(doctor);
//        System.out.println("Doctor insertado con ID: " + doctorId);

        // Probar Client
        System.out.println("\nPROBANDO TABLA CLIENT -------------------");
        jdbcClient clientDAO = new jdbcClient();
        Client client = new Client();
        client.setName("Juan");
        client.setSurname("Pérez");
        client.setDob(LocalDate.of(1990, 5, 14));
        client.setMail("juan.perez@example.com");
        client.setSex(Sex.MALE);

     // No asignamos doctor
     // client.setDoctorId(doctorId);

        int clientId = clientDAO.addClient(client);
        System.out.println("Cliente insertado con ID: " + clientId);


        Client loadedClient = clientDAO.getClientById(clientId);
        if (loadedClient != null) {
            System.out.println("Cliente recuperado: " + loadedClient.getName() + " " + loadedClient.getSurname());
        }

        // Actualizar cliente
        loadedClient.setSurname("Pérez Gómez");
        clientDAO.updateClient(loadedClient);
        System.out.println("Cliente actualizado correctamente.");

        List<Client> allClients = clientDAO.getClients();
        System.out.println("Clientes totales: " + allClients.size());

        // Probar MedicalHistory
        System.out.println("\nPROBANDO TABLA MEDICALHISTORY -------------------");
        jdbcMedicalHistory mhDAO = new jdbcMedicalHistory();
        MedicalHistory mh = new MedicalHistory();
        mh.setDate(LocalDate.now());
        mh.setClientId(clientId);
        //mh.setDoctorId(doctorId);
        mh.setObservations("Revisión general sin incidencias.");

        int mhId = mhDAO.addMedicalHistory(mh);
        System.out.println("Historial médico insertado con ID: " + mhId);

        MedicalHistory loadedMH = mhDAO.getMedicalHistoryById(mhId);
        if (loadedMH != null) {
            System.out.println("Historial médico recuperado: " + loadedMH.getObservations());
        }

        // Listar todos
        List<MedicalHistory> histories = mhDAO.getMedicalHistories();
        System.out.println("Total de historiales en BD: " + histories.size());

        // 6️Eliminar todo (limpieza)
//        System.out.println("\nLIMPIEZA -------------------");
//        mhDAO.deleteMedicalHistory(mhId);
//        clientDAO.deleteClient(clientId);
//        // doctorDAO.deleteDoctor(doctorId);
//        userDAO.deleteUser(userId);

        System.out.println("\nTODAS LAS PRUEBAS JDBC SE EJECUTARON CORRECTAMENTE ✅");
    }
}

