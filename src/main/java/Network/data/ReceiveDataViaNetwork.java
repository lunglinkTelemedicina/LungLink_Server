package Network.data;

import pojos.Client;
import pojos.Doctor;
import pojos.DoctorSpecialty;
import pojos.Sex;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;

public class ReceiveDataViaNetwork {
//TODO : no tengo claro como se conecta esto con clienthandler o server connection
    private DataInputStream dataInputStream;
    private Socket socket;

    public ReceiveDataViaNetwork(Socket socket) {
        try{
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
        }catch (IOException e){
            System.err.println("Error initializing ReceiveDataViaNetwork: " + e.getMessage());
            e.printStackTrace();

        }
    }

    public String receiveString() throws IOException {
        return dataInputStream.readUTF();
    }

    public Client receiveClient() {
        Client client = null;

        try {
            int clientId = dataInputStream.readInt();
            String name = dataInputStream.readUTF();
            String surname = dataInputStream.readUTF();
            LocalDate dob = LocalDate.parse(dataInputStream.readUTF());
            String mail = dataInputStream.readUTF();
            String sexStr = dataInputStream.readUTF();
            Sex sex = Sex.valueOf(sexStr);

            client = new Client(clientId, name, surname, dob, mail, sex, null);

        } catch (EOFException ex) {
            System.out.println("Client data not correctly read.");
        } catch (IOException ex) {
            System.err.println("Error receiving Client data: " + ex.getMessage());
            ex.printStackTrace();
        }

        return client;
    }

    public Doctor receiveDoctor() {
        Doctor doctor = null;

        try {
            int id = dataInputStream.readInt();
            String name = dataInputStream.readUTF();
            String surname = dataInputStream.readUTF();
            String email = dataInputStream.readUTF();
            String specialtyStr = dataInputStream.readUTF();
            DoctorSpecialty specialty =  DoctorSpecialty.valueOf(specialtyStr);

            doctor = new Doctor(id, name, surname, email, specialty);

        } catch (EOFException ex) {
            System.out.println("Doctor data not fully received.");
        } catch (IOException ex) {
            System.err.println("Error receiving Doctor data: " + ex.getMessage());
            ex.printStackTrace();
        }

        return doctor;
    }

    public void releaseResources() {
        try {
            if (dataInputStream != null) {
                dataInputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            System.err.println("Error with resources: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    //TODO receive medicalHistory cuando las signals este claro como se recogen y se guardan




}
