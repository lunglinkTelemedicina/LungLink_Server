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
    private User user;
    private List<MedicalHistory> medicalHistory;
    private List <Doctor> doctors;

    public Client(int clientId, String name, String surname, Sex sex){
        this.clientId=clientId;
        this.name=name;
        this.surname=surname;
        this.medicalHistory=new ArrayList<MedicalHistory>();
        this.doctors=new ArrayList<Doctor>();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<MedicalHistory> getMedicalHistory () {
        return medicalHistory;
    }

    public void setMedicalHistory(List<MedicalHistory> medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public List<Doctor> getDoctors () {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    @Override
    public String toString() {
        return "Name: " + name + " Surname: " + surname + ", ClientId: " + clientId;
    }

    /*private void createRecord(){
        medicalHistory newRecord=extraInfo();
        newRecord.setClientName(this.name);
        newRecord.setClientSurname(this.surname);
        this.getMedicalHistory().add(newRecord);

    }*/

    /*private MedicalHistory extraInfo(){
    Scanner sc=new Scanner(System.in);
    Sex sex;
    sout("Sex: 0- Female 1- Male");
    int numSex=sc.nextInt();
    if (numSex=0){
      Sex sex=Sex.FEMALE;
      } if
    sout("Height");
    int height= sc.nextInt();
    sout("Weight ");
    double weight=sc.nextDouble;
    sout("Do you have any additional symptoms?"+\n+"1-Yes
     */


}
