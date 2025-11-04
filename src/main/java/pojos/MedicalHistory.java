package pojos;


import java.time.LocalDate;
import java.util.List;

public class MedicalHistory {

    private int recordId;
    private LocalDate date;
    private int clientId;
    private int doctorId;
    private Signal signalEMG;
    private Signal signalECG;
    private String observations;
    private List<String> symptomsList;

    public MedicalHistory() {
        //constructor vacio
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public Signal getSignalEMG() {
        return signalEMG;
    }

    public void setSignalEMG(Signal signalEMG) {
        this.signalEMG = signalEMG;
    }

    public Signal getSignalECG() {
        return signalECG;
    }

    public void setSignalECG(Signal signalECG) {
        this.signalECG = signalECG;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public List<String> getSymptomList() {
        return symptomsList;
    }

    public void setSymptomList(List<String> symptomList) {
        this.symptomsList = symptomList;
    }
}
