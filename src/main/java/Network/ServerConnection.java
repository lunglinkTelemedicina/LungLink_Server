package Network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerConnection{

    private int port;
    private boolean running = true;
    private ServerSocket serverSocket;
    private List<ClientHandler> connectedClients = new ArrayList<ClientHandler>();

    public ServerConnection(int port) {
        this.port = port;
    }

    //Starts server and accepts clients until stopped

    public void start(){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started at port " + port);

            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("New client connected: \n" + socket.getRemoteSocketAddress());

                    // Create handler
                    ClientHandler handler = new ClientHandler(socket,  this);
                    registerClient(handler);

                    // Start handler thread
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

    //stop server safely
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

    public void registerClient(ClientHandler handler){
        connectedClients.add(handler);
    }
    public void unregisterClient(ClientHandler handler){
        connectedClients.remove(handler);
    }

    public int getConnectedClientCount() {
        return connectedClients.size();
    }

    public void broadcastShutdownMessage() {
        synchronized (connectedClients) {
            for (ClientHandler handler : connectedClients) {
                handler.sendShutdownMessage();
            }
        }
    }


}
