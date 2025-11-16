package pojos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private int clientId;
    private String name;
    private String surname;
    private LocalDate dob;
    private String mail;
    private Sex sex;
    private List<MedicalHistory> medicalHistory;
    private int doctorId;
    private int  userId;

    public Client(){

    }

    public Client(int clientId, String name, String surname, LocalDate dob, String mail, Sex sex, List<MedicalHistory> medicalHistory, int doctorId, int userId) {
        this.clientId = clientId;
        this.name = name;
        this.surname = surname;
        this.dob = dob;
        this.mail = mail;
        this.sex = sex;
        this.medicalHistory = medicalHistory;
        this.doctorId = doctorId;
        this.userId = userId;
    }

    public Client(int clientId, String name, String surname, LocalDate dob, String mail, Sex sex, Object o) {

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

    public int createClientId(){
        //TODO elegir si random o no random
        return ++clientId;
    }
    public void addExtraInfo(){
        //TODO funcion
    }

    public List<String>viewResults(){
        //TODO funcion
        return null;//cambiar el null
    }

    public void registerSymptoms(){
        //TODO funcion
    }



    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", clientId=" + clientId +
                '}';
    }
}




