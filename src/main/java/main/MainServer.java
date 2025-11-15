package main;
import Network.*;

public class MainServer {
    public static void main(String[] args) {
        ServerConnection server = new ServerConnection(9000);
        server.start();
    }
}
