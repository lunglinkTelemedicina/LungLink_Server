package Network.data;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Class used to send different types of data (int, String, byte[])
 * through a socket connection. Wraps a DataOutputStream to simplify
 * sending structured data to the remote side.
 */

public class SendDataViaNetwork {

    private DataOutputStream dataOutputStream;
    private Socket socket;

    /**
     * Creates a new sender using the given socket and prepares the output stream.
     *
     * @param socket the socket connected to the destination
     */

    public SendDataViaNetwork(Socket socket) {
        try {
            this.socket = socket;
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error initializing SendDataViaNetwork: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Sends an integer to the other side.
     * @param value the int value to send
     * @throws IOException if the write fails
     */

    public void sendInt(int value) throws IOException {
        dataOutputStream.writeInt(value);
        dataOutputStream.flush();
    }
    /**
     * Sends a UTF string. Uses writeUTF for automatic encoding.
     * @param message the string to send
     */

    public void sendString(String message) {
        try {
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
        } catch (IOException e) {
            System.err.println("Error sending string: " + e.getMessage());
        }
    }

    /**
     * Sends a byte array, first sending its length and then the raw data.
     * @param data the byte array to send
     * @throws IOException if the socket fails during transmission
     */

    public void sendBytes(byte[] data) throws IOException {
        dataOutputStream.writeInt(data.length);
        dataOutputStream.write(data);
        dataOutputStream.flush();

    }
    /**
     * Sends raw bytes directly without sending a length before them.
     * Useful for already structured binary data.
     * @param data the bytes to send
     */

    public void sendRawBytes(byte[] data) {
        try {
            dataOutputStream.write(data);
            dataOutputStream.flush();
        } catch (IOException e) {
            System.out.println("Error sending raw bytes: " + e.getMessage());
        }
    }

    /**
     * Closes the output stream and socket safely.
     * Should be called when you finish sending data.
     */

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

    /**
     * Closes the sender and all underlying resources.
     * @throws IOException if closing fails
     */
    public void close() throws IOException {
        if (dataOutputStream != null) dataOutputStream.close();
        if (socket != null) socket.close();
    }
}