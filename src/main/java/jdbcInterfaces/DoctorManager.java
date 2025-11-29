package jdbcInterfaces;

import pojos.Client;
import pojos.Doctor;

import java.sql.Connection;
import java.util.List;


public interface DoctorManager {

    void addDoctor(Doctor doctor);
    Doctor getDoctorByUserId(int userId);
    List<Doctor> getDoctors();
    List<Client> getClientsByDoctorId(int doctorId);
    boolean isPatientAssignedToDoctor(int doctorId, int clientId);
    void insertDoctorByDefault(Connection conn);
    int getDefaultDoctorId();

}
