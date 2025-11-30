package pojos;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a single medical history record for a patient.
 * Stores the date, symptoms, observations, and the IDs of both
 * the patient and the doctor who handled the entry.
 */
public class MedicalHistory {

    private int recordId;
    private LocalDate date;
    private String observations;
    private List<String> symptomsList;
    private int clientId;
    private int doctorId;

    /**
     * Creates an empty medical history record
     */
    public MedicalHistory() {}

    /**
     * Creates a full medical history entry.
     * @param recordId      unique ID of the record
     * @param date          date of the consultation or data entry
     * @param observations  doctor's observations
     * @param symptomsList  list of reported symptoms
     * @param clientId      ID of the patient
     * @param doctorId      ID of the doctor who reviewed the record
     */
    public MedicalHistory(int recordId, LocalDate date, String observations, List<String> symptomsList, int clientId,  int doctorId) {
        this.recordId = recordId;
        this.date = date;
        this.observations = observations;
        this.symptomsList = symptomsList;
        this.clientId = clientId;
        this.doctorId = doctorId;
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

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public List<String> getSymptomsList() {
        return symptomsList;
    }

    public void setSymptomsList(List<String> symptomList) {
        this.symptomsList = symptomList;
    }

    public int getDoctorId() {return doctorId;}

    public void setDoctorId(int doctorId) {this.doctorId = doctorId;}

    @Override
    public String toString() {
        return "MedicalHistory{" +
                "recordId=" + recordId +
                ", date=" + date +
                ", observations='" + observations + '\'' +
                ", symptomsList=" + symptomsList +
                ", clientId=" + clientId +
                ", doctorId=" + doctorId +
                '}';
    }
}
