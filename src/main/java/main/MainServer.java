package main;
import Network.*;
import Network.ServerUI;
import jdbc.*;

public class MainServer {
    public static void main(String[] args) {

        //Start the connection manager so the database is created
        new JDBCConnectionManager();

        int port = 9000;
        //Create the server socket
        ServerConnection server = new ServerConnection(port);

        //Run the server on a separate thread
        Thread serverThread = new Thread(server::start);
        serverThread.start();

        //Start admin interface (console UI)
        ServerUI ui = new ServerUI(server);
        ui.start();
    }
}
