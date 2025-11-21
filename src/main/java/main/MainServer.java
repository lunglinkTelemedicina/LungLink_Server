package main;
import Network.*;
import Network.ServerUI;
import jdbc.*;

public class MainServer {
    public static void main(String[] args) {

        new JDBCConnectionManager();  //inicializar connection manager para que se cree la base de datos

        int port = 9000;
        //Create the server socket
        ServerConnection server = new ServerConnection(port);

        //Run the server on a separate thread
        Thread serverThread = new Thread(server::start);
        serverThread.start();

        // Start admin interface (console UI)
        ServerUI ui = new ServerUI(server);
        ui.start();
    }
}
