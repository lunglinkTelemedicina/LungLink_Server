package pojos;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a patient in the system. Stores personal information,
 * physical data, medical history records and the doctor assigned.
 */

public class Client {

    private int clientId;
    private String name;
    private String surname;
    private LocalDate dob;
    private String mail;
    private Sex sex;
    private List<MedicalHistory> medicalHistory;
    private double weight;
    private double height;
    private int doctorId;
    private int  userId;

    /**
     * Creates an empty Client instance.
     */
    public Client(){}

    /**
     * Creates a Client with all its fields.
     *
     * @param clientId     unique ID of the client
     * @param name         patient's name
     * @param surname      patient's surname
     * @param dob          date of birth
     * @param mail         email address
     * @param sex          biological sex
     * @param medicalHistory list with the patient's medical history
     * @param weight       weight in kg
     * @param height       height in meters
     * @param doctorId     assigned doctor ID
     * @param userId       user account ID
     */

    public Client(int clientId, String name, String surname, LocalDate dob, String mail, Sex sex, List<MedicalHistory> medicalHistory, double weight, double height, int doctorId, int userId) {
        this.clientId = clientId;
        this.name = name;
        this.surname = surname;
        this.dob = dob;
        this.mail = mail;
        this.sex = sex;
        this.medicalHistory = medicalHistory;
        this.weight = weight;
        this.height = height;
        this.doctorId = doctorId;
        this.userId = userId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getDob() { return dob; }

    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public List<MedicalHistory> getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(List<MedicalHistory> medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientId=" + clientId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", dob=" + dob +
                ", mail='" + mail + '\'' +
                ", sex=" + sex +
                ", medicalHistory=" + medicalHistory +
                ", weight=" + weight +
                ", height=" + height +
                ", doctorId=" + doctorId +
                ", userId=" + userId +
                '}';
    }
}
