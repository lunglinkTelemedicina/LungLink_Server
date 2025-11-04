package jdbc;

import jdbcInterfaces.MedicalHistoryManager;
import pojos.MedicalHistory;

import java.util.List;


public class jdbcMedicalHistory implements MedicalHistoryManager {
    @Override
    public void addMedicalHistory(MedicalHistory history) {

    }

    @Override
    public MedicalHistory getMedicalHistoryById(int id) {
        return null;
    }

    @Override
    public List<MedicalHistory> getMedicalHistoryByClientId(int clientId) {
        return List.of();
    }

    @Override
    public List<MedicalHistory> getMedicalHistories() {
        return List.of();
    }

    @Override
    public void deleteMedicalHistory(int id) {

    }
}
