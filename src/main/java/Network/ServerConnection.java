package Network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the functionality of the server socket and manages all client connections.
 * This class is responsible for starting the server, accepting new clients,
 * keeping track of active connections and shutting everything down safely.
 */


public class ServerConnection{

    private int port;

    private DoctorAssignmentService doctorAssignmentService;

    private boolean running = true;
    private ServerSocket serverSocket;
    private List<ClientHandler> connectedClients = new ArrayList<ClientHandler>();

    /**
     * Creates a new ServerConnection, specifying the port where the server will listen
     * and the doctor assignment service that each client handler will use.
     *
     * @param port the port number the server will run on
     * @param doctorAssignmentService the service used for assigning doctors to patients
     */

    public ServerConnection(int port, DoctorAssignmentService doctorAssignmentService) {
        this.port = port;
        this.doctorAssignmentService = doctorAssignmentService;
    }

    /**
     * Starts the server and listens for incoming client connections.
     * For each new connection, a ClientHandler is created and executed in its own thread.
     * The method continues running until the server is stopped.
     */

    public void start(){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("\nServer started at port " + port);

            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("New client connected: " + socket.getRemoteSocketAddress()+"\n");

                    // create handler
                    ClientHandler handler = new ClientHandler(socket,  this, doctorAssignmentService);
                    registerClient(handler);

                    //Run the handler in a separate thread
                    new Thread(handler).start();

                } catch (IOException e) {
                    if (running) {
                        System.out.println("Error accepting client: " + e.getMessage());
                    } else {
                        System.out.println("Server is shutting down.");
                    }
                }
            }
        } catch (IOException e){
            System.out.println("Server Error" + e.getMessage());
            e.printStackTrace();
        }finally{
            stopServer();
        }
    }
    /**
     * Stops the server and closes all active client connections.
     * This method ensures that the server socket is closed safely and that
     * all client handlers shut down correctly.
     */

    public void stopServer(){
        running = false;

        //close server socket
        if(serverSocket != null && !serverSocket.isClosed()){
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing server socket: " + e.getMessage());
                e.printStackTrace();
            }
        }
        //Close all connected clientHandlers
        for (ClientHandler ch : connectedClients) {
            try {
                ch.closeConnection();
            } catch (Exception e) {
                System.err.println("Error closing client handler: " + e.getMessage());
                e.printStackTrace();
            }
        }

        connectedClients.clear();

        System.out.println("Server stopped.");
    }
    /**
     * Registers a newly connected client so that the server can keep track of it.
     * @param handler the client handler representing the connected client
     */

    public void registerClient(ClientHandler handler){
        connectedClients.add(handler);
    }
    /**
     * Removes a client from the list once it disconnects.
     * @param handler the client handler to unregister
     */
    public void unregisterClient(ClientHandler handler){
        connectedClients.remove(handler);
    }
    /**
     * Returns the current number of connected clients.
     * @return the active client count
     */
    public int getConnectedClientCount() {
        return connectedClients.size();
    }
    /**
     * Sends a shutdown message to all connected clients so they can disconnect gracefully
     * when the server is about to stop.
     */
    public void broadcastShutdownMessage() {
        synchronized (connectedClients) {
            for (ClientHandler handler : connectedClients) {
                handler.sendShutdownMessage();
            }
        }
    }
}
