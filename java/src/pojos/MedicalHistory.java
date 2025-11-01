package pojos;


import java.time.LocalDate;
import java.util.List;

public class MedicalHistory {

    private int recordId;
    private LocalDate date;
    private int patientId;
    private int doctorId;
    private Signal signalEMG;
    private Signal signalECG;
    private String data;
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

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<String> getSymptomList() {
        return symptomsList;
    }

    public void setSymptomList(List<String> symptomList) {
        this.symptomsList = symptomList;
    }
}
