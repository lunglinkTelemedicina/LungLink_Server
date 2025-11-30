package Network;

import utils.UIUtils;

/**
 * Console-based interface that allows the server administrator to
 * check active connections and stop the server when needed. It communicates
 * with the ServerConnection class to carry out its function.
 */
public class ServerUI {
    private final ServerConnection serverConnection;
    private final String ADMIN_PASSWORD = "admin123";

    /**
     * Creates a ServerUI linked to a specific server connection.
     * @param serverConnection the server controller that this interface will manage
     */
    public ServerUI(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }
    /**
     * Starts the admin interface loop. The user can view the number of clients
     * that are currently connected or shut down the server after confirming.
     */
    public void start() {

        System.out.println("\nSERVER ADMIN PANEL");

        while (true) {

            System.out.println("Choose an option:\n");
            System.out.println("1) View connected users");
            System.out.println("2) Stop server");

            int option = UIUtils.readInt("\nOption: ");

            switch (option) {
                case 1:
                    showConnectedClients();
                    break;

                case 2:
                    attemptShutdown();
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    /**
     * Prints how many users are currently connected to the server.
     */
    private void showConnectedClients() {
        int count = serverConnection.getConnectedClientCount();
        System.out.println("Connected users: " + count);
    }
    /**
     * Attempts to shut down the server.
     * <p>
     * The admin must enter the correct password. If there are active clients,
     * the method warns the admin and asks for confirmation before continuing.
     * If confirmed, all connected clients are notified before the server stops.
     */
    private void attemptShutdown() {

        String entered = UIUtils.readString("Enter admin password: ");

            if (!entered.equals(ADMIN_PASSWORD)) {
                System.out.println("Incorrect password.");
                return;
            }

            int clients = serverConnection.getConnectedClientCount();

            if (clients > 0) {
                System.out.println("WARNING: There are " + clients + " connected clients");
                String answer = UIUtils.readString("Are you sure you want to stop the server? (yes/no): ");

                if(!answer.equalsIgnoreCase("yes")) {
                    System.out.println("Shutdown cancelled");
                    return;
                }
            }
            //notify clients before shutting down
            serverConnection.broadcastShutdownMessage();
            try {
                Thread.sleep(200); //small delay for message to be delivered
            }catch(InterruptedException e) {
                throw new RuntimeException(e);
            }
            serverConnection.stopServer();
            System.exit(0);
    }
}
