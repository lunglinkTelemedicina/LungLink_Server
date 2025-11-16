package Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run(){
        System.out.println("Client connected" /*+ socket.getRemoteSocketAddress()*/);

        try{
            BufferedReader bf=new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            while((line=bf.readLine())!=null){
                if(line.equals("x")||line.equalsIgnoreCase("DISCONNECT")){
                    System.out.println("Client ended communication.");
                    break;
                }
                System.out.println("Message received: "+line);
            }

        } catch (SocketException se) {
            System.out.println("Client disconnected abruptly.");

        } catch (IOException eCliente) {
            System.out.println(" Error with client.");
            eCliente.printStackTrace();

        } finally {
            System.out.println("Closing sockets of this client.");

            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}

