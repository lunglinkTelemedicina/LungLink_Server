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
    public void sendBytes(byte[] data) throws IOException {
        dataOutputStream.writeInt(data.length);
        dataOutputStream.write(data);
        dataOutputStream.flush();

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
    public void close() throws IOException {
        if (dataOutputStream != null) dataOutputStream.close();
        if (socket != null) socket.close();
    }

    // TODO: SEND MedicalHistory cuando este claro como se guardan las signals


}
