package jdbcInterfaces;

import pojos.DoctorSpecialty;
import pojos.MedicalHistory;
import pojos.Signal;

import java.sql.Connection;
import java.util.List;

/**
 * Manages persistence and retrieval operations for the {@code MedicalHistory} entity
 * and handles core business logic related to medical records and doctor assignments.
 */
public interface MedicalHistoryManager {

    /**
     * Inserts a new medical history record into the database.
     *
     * @param history The {@code MedicalHistory} object to add. Its ID should be updated upon successful insertion.
     * @return The auto-generated record ID, or a negative value if the operation fails.
     */
    int addMedicalHistory(MedicalHistory history);

    /**
     * Retrieves all medical history records associated with a specific client ID.
     *
     * @param clientId The ID of the client whose records are to be retrieved.
     * @return A list of {@code MedicalHistory} objects for the given client. Returns an empty list if none are found.
     */
    List<MedicalHistory> getMedicalHistoryByClientId(int clientId);

    /**
     * Adds a list of symptoms to an existing medical history record identified by its ID.
     *
     * @param recordId The ID of the medical history record to update.
     * @param symptoms The list of symptoms (Strings) to be recorded.
     */
    void addSymptoms(int recordId, List<String> symptoms);

    /**
     * Updates the observations field for an existing medical history record.
     *
     * @param recordId The ID of the medical history record to update.
     * @param observations The new observation text to be stored.
     */
    void updateObservations(int recordId, String observations);

    /**
     * Assigns pending medical history records to a specific doctor based on the doctor's specialty
     * and the type of signal associated with the record (if any).
     *
     * @param doctorId The ID of the doctor to whom the records should be assigned.
     * @param specialty The {@code DoctorSpecialty} used to filter and assign relevant pending records.
     */
    void assignPendingRecordsToDoctor(int doctorId, DoctorSpecialty specialty);

}