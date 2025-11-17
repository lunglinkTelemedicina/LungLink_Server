package jdbcInterfaces;

import pojos.MedicalHistory;
import java.util.List;

public interface MedicalHistoryManager {

    int addMedicalHistory(MedicalHistory history);
    MedicalHistory getMedicalHistoryById(int id);
    List<MedicalHistory> getMedicalHistoryByClientId(int clientId);
    List<MedicalHistory> getMedicalHistories();
    void addSymptoms(int recordId, List<String> symptoms);
    void deleteMedicalHistory(int id);

}
