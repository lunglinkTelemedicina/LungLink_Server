package Network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnection{
    private int port;
    public ServerConnection(int port) {
        this.port = port;
    }

    public void start(){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Server started at port " + port);
            while(true){
                Socket socket=serverSocket.accept();
                Thread thread= new Thread(new ClientHandler(socket));
                thread.start();
            }
        } catch (IOException e){
            System.out.println("Server Error" + e.getMessage());
            e.printStackTrace();
        }
    }
}