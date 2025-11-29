package Network;

import Network.data.ReceiveDataViaNetwork;
import Network.data.SendDataViaNetwork;

import pojos.*;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ReceiveDataViaNetwork receive;
    private final SendDataViaNetwork send;

    private final ServerConnection serverConnection;
    private final CommandProcessor processor;

    private boolean running = true;

    public ClientHandler(Socket socket, ServerConnection serverConnection, DoctorAssignmentService doctorAssignmentService) {
        this.socket = socket;
        this.serverConnection = serverConnection;
        this.receive = new ReceiveDataViaNetwork(socket);
        this.send = new SendDataViaNetwork(socket);
        this.processor = new CommandProcessor(doctorAssignmentService);
    }

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





