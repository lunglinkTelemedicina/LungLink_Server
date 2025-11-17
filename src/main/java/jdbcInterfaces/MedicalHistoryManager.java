package jdbcInterfaces;

import pojos.MedicalHistory;
import pojos.Signal;

import java.sql.Connection;
import java.util.List;

public interface MedicalHistoryManager {

    int addMedicalHistory(MedicalHistory history);
    MedicalHistory getMedicalHistoryById(int id);
    List<MedicalHistory> getMedicalHistoryByClientId(int clientId);
    List<MedicalHistory> getMedicalHistories();
    void addSymptoms(int recordId, List<String> symptoms);
    void addSignalToMedicalHistory(int recordId, Signal signal);
    void loadSignalsForHistory(MedicalHistory mh, Connection conn);
    void deleteMedicalHistory(int id);

}
