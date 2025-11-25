package Network;

import Network.data.ReceiveDataViaNetwork;
import Network.data.SendDataViaNetwork;

import jdbc.JDBCClient;
import jdbc.JDBCMedicalHistory;
import jdbc.JDBCSignal;
import jdbc.JDBCDoctor;
import jdbc.JDBCUser;

import pojos.*;
import pojos.TypeSignal;


import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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

                // PROCESS COMMAND WITH THE BRAIN
                String response = processor.handleClientRequest(message, receive, send);

                // SEND RESPONSE BACK
                send.sendString(response);

                if (response.startsWith("OK|Disconnected")) {
                    running = false;
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





