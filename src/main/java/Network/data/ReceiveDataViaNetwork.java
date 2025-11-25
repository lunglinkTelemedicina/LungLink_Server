package Network.data;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveDataViaNetwork {

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

    public int receiveInt() throws IOException{
        return dataInputStream.readInt();
    }

    public String receiveString() throws IOException {
        return dataInputStream.readUTF();
    }

    public byte[] receiveBytes() throws IOException {
        int length = dataInputStream.readInt();
        byte[] buffer = new byte[length];
        dataInputStream.readFully(buffer); //Block till all bytes arrive
        return buffer;
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
    public void close() throws IOException {
        if (dataInputStream != null) dataInputStream.close();
        if (socket != null) socket.close();
    }

}
