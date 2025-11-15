package db;

import jdbc.*;
import pojos.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class testJDBC {

    public static void main(String[] args) throws SQLException {
        System.out.println("========== PRUEBA DE CONEXIÓN Y DAOs ==========\n");

        // 🔹 1. Probar conexión
        JDBCConnectionManager cm = new JDBCConnectionManager();
        cm.disconnect();
        System.out.println("✅ Conexión probada correctamente.\n");

        // ================================================================
        // 🔹 2. TABLA USER
        // ================================================================
        System.out.println("🧍‍♀️ PROBANDO TABLA USER -------------------");
        JDBCUser userDAO = new JDBCUser();

        // Usuario para doctor
        User doctorUser = new User();
        doctorUser.setUsername("lauraDoctor");
        doctorUser.setPassword("doc1234".getBytes());
        int doctorUserId = userDAO.addUser(doctorUser);
        System.out.println("Doctor User creado con ID: " + doctorUserId);

        // Usuario para cliente
        User clientUser = new User();
        clientUser.setUsername("juanClient");
        clientUser.setPassword("client1234".getBytes());
        int clientUserId = userDAO.addUser(clientUser);
        System.out.println("Client User creado con ID: " + clientUserId);

        // ================================================================
        // 🔹 3. TABLA DOCTOR
        // ================================================================
        System.out.println("\n👩‍⚕️ PROBANDO TABLA DOCTOR -------------------");
        JDBCDoctor doctorDAO = new JDBCDoctor();

        Doctor doctor = new Doctor();
        doctor.setName("Laura");
        doctor.setSurname("Gómez");
        doctor.setEmail("laura@example.com");
        doctor.setSpecialty(DoctorSpecialty.CARDIOLOGIST);
        doctor.setUserId(doctorUserId);

        doctorDAO.addDoctor(doctor);
        int doctorId = doctor.getDoctorId();
        System.out.println("Doctor insertado con ID: " + doctorId);

        Doctor loadedDoctor = doctorDAO.getDoctorById(doctorId);
        if (loadedDoctor != null) {
            System.out.println("Doctor recuperado: " + loadedDoctor.getName() + " " + loadedDoctor.getSurname());
        }

        // ================================================================
        // 🔹 4. TABLA CLIENT
        // ================================================================
        System.out.println("\n🧑‍⚕️ PROBANDO TABLA CLIENT -------------------");
        JDBCClient clientDAO = new JDBCClient();

        Client client = new Client();
        client.setName("Juan");
        client.setSurname("Pérez");
        client.setDob(LocalDate.of(1990, 5, 14));
        client.setMail("juan.perez@example.com");
        client.setSex(Sex.MALE);
        client.setDoctorId(doctorId); // 🔗 relación con el doctor
        client.setUserId(clientUserId); // 🔗 relación con su user

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

        // ================================================================
        // 🔹 5. TABLA MEDICAL HISTORY
        // ================================================================
        System.out.println("\n📋 PROBANDO TABLA MEDICALHISTORY -------------------");
        JDBCMedicalHistory mhDAO = new JDBCMedicalHistory();

        MedicalHistory mh = new MedicalHistory();
        mh.setDate(LocalDate.now());
        mh.setClientId(clientId);
        mh.setDoctorId(doctorId);
        mh.setObservations("Revisión general sin incidencias.");
       // mh.setSymptomsList(List.of("fatiga leve", "dolor muscular"));

        int mhId = mhDAO.addMedicalHistory(mh);
        System.out.println("Historial médico insertado con ID: " + mhId);

        MedicalHistory loadedMH = mhDAO.getMedicalHistoryById(mhId);
        if (loadedMH != null) {
            System.out.println("Historial médico recuperado: " + loadedMH.getObservations());
        }

        List<MedicalHistory> histories = mhDAO.getMedicalHistories();
        System.out.println("Total de historiales en BD: " + histories.size());

        // ================================================================
        // 🔹 6. LIMPIEZA (opcional)
        // ================================================================
        /*
        System.out.println("\n🧹 LIMPIEZA -------------------");
        mhDAO.deleteMedicalHistory(mhId);
        clientDAO.deleteClient(clientId);
        doctorDAO.deleteDoctor(doctorId);
        userDAO.deleteUser(doctorUserId);
        userDAO.deleteUser(clientUserId);
        System.out.println("Todos los registros de prueba eliminados correctamente.");
        */

        System.out.println("\n✅ TODAS LAS PRUEBAS JDBC SE EJECUTARON CORRECTAMENTE");
    }
}
