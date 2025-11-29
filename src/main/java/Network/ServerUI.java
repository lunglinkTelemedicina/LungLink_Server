package Network;

import utils.UIUtils;


public class ServerUI {
    private final ServerConnection serverConnection;
    private final String ADMIN_PASSWORD = "admin123";

    public ServerUI(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

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

    private void showConnectedClients() {
        int count = serverConnection.getConnectedClientCount();
        System.out.println("Connected users: " + count);
    }

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

            serverConnection.broadcastShutdownMessage();
            try {
                Thread.sleep(200);
            }catch(InterruptedException e) {
                throw new RuntimeException(e);
            }

            serverConnection.stopServer();
            System.exit(0);
    }
}
