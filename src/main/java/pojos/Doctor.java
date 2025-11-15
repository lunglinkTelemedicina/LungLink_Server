package pojos;

import java.util.List;

public class Doctor {

    private int doctorId;
    private String name;
    private String surname;
    private String email;
    private DoctorSpecialty specialty;
    private List<Client> patients;
    private int userId;

    public Doctor(int doctorId, String name, String surname, String email, DoctorSpecialty specialty, List<Client> patients,  int userId) {
        this.doctorId = doctorId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.specialty = specialty;
        this.patients = patients;
        this.userId = userId;
    }

    public Doctor(){

    }


    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DoctorSpecialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(DoctorSpecialty specialty) {
        this.specialty = specialty;
    }

    public List<Client> getPatients() {
        return patients;
    }

    public void setPatients(List<Client> patients) {
        this.patients = patients;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "doctorId=" + doctorId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", specialty=" + specialty +
                ", patients=" + patients +
                ", userId=" + userId +
                '}';
    }
}
