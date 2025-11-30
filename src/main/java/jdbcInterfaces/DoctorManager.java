package jdbcInterfaces;

import pojos.Client;
import pojos.Doctor;

import java.sql.Connection;
import java.util.List;


/**
 * Manages persistence and retrieval operations for the {@code Doctor} entity
 * and related client assignments in the database.
 */
public interface DoctorManager {

    /**
     * Inserts a new doctor record into the database.
     *
     * @param doctor The {@code Doctor} object to add. Its ID will be updated upon successful insertion.
     */
    void addDoctor(Doctor doctor);

    /**
     * Retrieves a doctor profile from the database using the associated user ID.
     *
     * @param userId The ID of the user linked to the doctor profile.
     * @return The retrieved {@code Doctor} object, or null if no doctor is found for the given user ID.
     */
    Doctor getDoctorByUserId(int userId);

    /**
     * Retrieves a list of all doctor records from the database.
     *
     * @return A list of all {@code Doctor} objects found in the database.
     */
    List<Doctor> getDoctors();

    /**
     * Retrieves a list of all clients currently assigned to a specific doctor.
     *
     * @param doctorId The ID of the doctor whose clients are to be retrieved.
     * @return A list of {@code Client} objects assigned to the doctor.
     */
    List<Client> getClientsByDoctorId(int doctorId);

    /**
     * Checks if a specific client is currently assigned to a specific doctor.
     *
     * @param doctorId The ID of the doctor to check assignment against.
     * @param clientId The ID of the client to verify.
     * @return true if the client is assigned to the doctor, false otherwise.
     */
    boolean isPatientAssignedToDoctor(int doctorId, int clientId);

    /**
     * Inserts a default doctor user into the database if one does not already exist.
     *
     * @param conn The active database connection used for insertion.
     */
    void insertDoctorByDefault(Connection conn);

    /**
     * Retrieves the doctor ID of the default doctor profile.
     *
     * @return The ID of the default doctor, or -1 if the doctor is not found.
     */
    int getDefaultDoctorId();

}