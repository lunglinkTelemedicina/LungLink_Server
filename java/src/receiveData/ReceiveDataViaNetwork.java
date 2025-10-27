package receiveData;

import pojos.Client;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReceiveDataViaNetwork {
    public static void main(String[] args) {
        InputStream inputStream = null;
        ObjectInputStream objectInputStream =null;
        ServerSocket serverSocket = null;
        Socket socket = null;

        try{
            serverSocket = new ServerSocket(9000);
            socket = serverSocket.accept();
            inputStream = socket.getInputStream();
            System.out.println("Connection from the direction " + socket.getInetAddress());
        } catch (IOException e) {
            System.out.println("It was not possible to start the server. Fatal error");
            Logger.getLogger(ReceiveDataViaNetwork.class.getName()).log(Level.SEVERE,null,e);
            System.exit(-1);
        }
        try{
            objectInputStream = new ObjectInputStream(inputStream);
            Object tmp;
            while((tmp=objectInputStream.readObject()) != null){
                Client patient = (Client) tmp;
                System.out.println(patient.toString());
            }
        } catch (EOFException e) {
            System.out.println("All data have been correctly read.");
        } catch (IOException | ClassNotFoundException e){
            System.out.println("Unable to read from the patient.");
            Logger.getLogger(ReceiveDataViaNetwork.class.getName()).log(Level.SEVERE,null,e);
        }finally{
            releaseResources(objectInputStream, socket, serverSocket);
        }
    }

    private static void releaseResources(ObjectInputStream objectInputStream, Socket socket, ServerSocket serverSocket){
        try{
            objectInputStream.close();
        } catch (IOException e) {
            Logger.getLogger(ReceiveDataViaNetwork.class.getName()).log(Level.SEVERE,null,e);
        }
        try{
            socket.close();
        } catch (IOException e) {
            Logger.getLogger(ReceiveDataViaNetwork.class.getName()).log(Level.SEVERE,null,e);
        }
        try{
            serverSocket.close();
        } catch (IOException e) {
            Logger.getLogger(ReceiveDataViaNetwork.class.getName()).log(Level.SEVERE,null,e);
        }
    }
}