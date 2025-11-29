package jdbcInterfaces;

import pojos.DoctorSpecialty;
import pojos.MedicalHistory;
import pojos.Signal;

import java.sql.Connection;
import java.util.List;

public interface MedicalHistoryManager {

    int addMedicalHistory(MedicalHistory history);
    List<MedicalHistory> getMedicalHistoryByClientId(int clientId);
    void addSymptoms(int recordId, List<String> symptoms);
    void updateObservations(int recordId, String observations);
    void assignPendingRecordsToDoctor(int doctorId, DoctorSpecialty specialty);

}
