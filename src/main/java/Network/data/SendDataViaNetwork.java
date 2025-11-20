package Network.data;

import pojos.Client;
import pojos.Doctor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SendDataViaNetwork {

    private DataOutputStream dataOutputStream;
    private Socket socket;

    public SendDataViaNetwork(Socket socket) {
        try {
            this.socket = socket;
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error initializing SendDataViaNetwork: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendString(String message) {
        try {
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Error sending string: " + e.getMessage());
        }
    }

    public void sendClient(Client client)  {
        try {
            dataOutputStream.writeInt(client.getClientId());
            dataOutputStream.writeUTF(client.getName());
            dataOutputStream.writeUTF(client.getSurname());
            dataOutputStream.writeUTF(client.getDob().toString());
            dataOutputStream.writeUTF(client.getMail());
            dataOutputStream.writeUTF(client.getSex().toString());

            dataOutputStream.flush();

        } catch (IOException e) {
            System.err.println("Error sending client data: " + e.getMessage());
        }
    }


    public void sendDoctor(Doctor doctor) {
        try {
            dataOutputStream.writeInt(doctor.getDoctorId());
            dataOutputStream.writeUTF(doctor.getName());
            dataOutputStream.writeUTF(doctor.getSurname());
            dataOutputStream.writeUTF(doctor.getEmail());
            dataOutputStream.writeUTF(doctor.getSpecialty().toString());

            dataOutputStream.flush();

        } catch (IOException e) {
            System.err.println("Error sending doctor data: " + e.getMessage());
        }
    }

    public void releaseResources() {
        try {
            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            System.err.println("Error with resources: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    // TODO: SEND MedicalHistory cuando este claro como se guardan las signals


}
