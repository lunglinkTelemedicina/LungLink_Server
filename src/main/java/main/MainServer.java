package main;
import Network.*;
import jdbc.*;

public class MainServer {
    public static void main(String[] args) {

        new JDBCConnectionManager();  //inicializar connection manager para que se cree la base de datos

        ServerConnection server = new ServerConnection(9000);
        server.start();
    }
}
