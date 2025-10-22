package src.pojos;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {

    private String name;
    private String surname;
    private String mail;
    private int age;
   //? private Sex sex;
    private int clientId;
    //private User user;
    //private List<MedicalRecords> medicalRecords;
    //private List <Doctor> doctors;


    public Client(){
        //constructor vacío
    }

    public Client(String name, String surname, int clientId){
        this.name=name;
        this.surname=surname;
        this.clientId=clientId;
        //this.medicalRedords=new ArrayList<MedicalRecords>()
        //this.doctors=new ArrayList<Doctor>();
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
    public int getClientId() {
        return clientId;
    }
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "Name: " + name + " Surname: " + surname + ", ClientId: " + clientId;
    }

    /*private void createRecord(){
        MedicalRecord newRecord=extraInfo();
        newRecord.setClientName(this.name);
        newRecord.setClientSurname(this.surname);
        this.getMedicalRecords().add(newRecord);

    }*/

    /*private MedicalRecord extraInfo(){
    Scanner sc=new Scanner(System.in);
    Sex sex;
    sout("Sex: 0- Female 1- Male");
    int numSex=sc.nextInt();
    if (numSex=0){
      Sex sex=Sex.FEMALE;
      } if
    sout("Height");
    int height= sc.nextInt();
    sout("Weight");
    double weight=sc.nextDouble;
    sout("Do you have any additional symptoms?"+\n+"1-Yes
     */


}
