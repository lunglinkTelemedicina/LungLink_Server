package Network.data;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *Handles incoming data from a client or server over a TCP socket connection.
 * This class wraps a {@link DataInputStream} and provides methods for receiving integers,
 * UTF strings, and byte arrays following a length-prefixed format.
 * It is used all through the system to standardize how low-level data packets
 * are read from the network.
 */

public class ReceiveDataViaNetwork {

    private DataInputStream dataInputStream;
    private Socket socket;

    /**
     * Creates a new instance linked to the socket.
     * Initializes the input stream used for receiving structured data.
     * @param socket the active socket from which data will be read.
     */

    public ReceiveDataViaNetwork(Socket socket) {
        try{
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
        }catch (IOException e){
            System.err.println("Error initializing ReceiveDataViaNetwork: " + e.getMessage());
            e.printStackTrace();
        }

    }
    /**
     * Receives a 4-byte int sent through the network stream.
     * @return the integer value received
     * @throws IOException if the socket is closed or the stream fails during reading
     */

    public int receiveInt() throws IOException{
        return dataInputStream.readInt();
    }
    /**
     * Receives a UTF-encoded string sent through the network.
     * This method expects that the sender used {@link java.io.DataOutputStream#writeUTF(String)}.
     * @return the decoded string
     * @throws IOException if an I/O error occurs while reading
     */

    public String receiveString() throws IOException {
        return dataInputStream.readUTF();
    }

    /**
     * Receives a byte array from the stream. First it reads the length, then it reads the bytes.
     * @return the fully received byte array
     * @throws IOException if reading fails or the connection is interrupted
     */

    public byte[] receiveBytes() throws IOException {
        int length = dataInputStream.readInt();
        byte[] buffer = new byte[length];
        dataInputStream.readFully(buffer);
        return buffer;
    }
    /**
     * Closes the input stream and the socket (if they are open).
     * This method should be called when you are done receiving data.
     */

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
    /**
     * Closes the receiver and frees its resources.
     * @throws IOException if the close operation fails
     */

    public void close() throws IOException {
        if (dataInputStream != null) dataInputStream.close();
        if (socket != null) socket.close();
    }
}