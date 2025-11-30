package Network;

import Network.data.ReceiveDataViaNetwork;
import Network.data.SendDataViaNetwork;

import pojos.*;
import java.io.*;
import java.net.Socket;
/**
 * Handles communication with a single connected client on the server side.
 * <p>Each instance of {@code ClientHandler} runs on its own thread, continuously
 * receiving commands, processing them using a {@code CommandProcessor}, and sending back responses.
 * It manages the lifecycle of the client connection until explicitly disconnected or an error occurs.</p>
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ReceiveDataViaNetwork receive;
    private final SendDataViaNetwork send;

    private final ServerConnection serverConnection;
    private final CommandProcessor processor;

    private boolean running = true;
    /**
     * Constructs a new ClientHandler.
     *
     * @param socket The {@code Socket} connection established with the client.
     * @param serverConnection The main {@code ServerConnection} object, used for unregistering the handler upon disconnection.
     * @param doctorAssignmentService The service used by the {@code CommandProcessor} to handle doctor assignment logic.
     */
    public ClientHandler(Socket socket, ServerConnection serverConnection, DoctorAssignmentService doctorAssignmentService) {
        this.socket = socket;
        this.serverConnection = serverConnection;
        this.receive = new ReceiveDataViaNetwork(socket);
        this.send = new SendDataViaNetwork(socket);
        this.processor = new CommandProcessor(doctorAssignmentService);
    }
    /**
     * The main execution loop for the client handler thread.
     * <p>Continuously listens for messages, processes them, and sends a response.
     * The loop terminates if a disconnection command is received or if an {@code IOException} is thrown (client unexpectedly closed the connection).</p>
     */
    @Override
    public void run() {
        try {
            while (running) {

                String message = receive.receiveString();

                String response = processor.handleClientRequest(message, receive, send);

                if (response.startsWith("OK|Disconnected")) {
                    running = false;
                }

                if (!response.equals("NO_REPLY")) {
                    send.sendString(response);
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            serverConnection.unregisterClient(this);
            closeConnection();
            System.out.println("Closed handler for: " + socket.getRemoteSocketAddress());
        }

    }
    /**
     * Closes the socket and releases all associated input and output streams.
     * Sets the internal {@code running} flag to false.
     */
    public void closeConnection(){
        running = false;
        try{
            receive.close();
        }catch(Exception ignored) {}

        try {
            send.close();
        } catch (Exception ignored) {}

        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendShutdownMessage() {
        send.sendString("SERVER_SHUTDOWN");
    }

}





