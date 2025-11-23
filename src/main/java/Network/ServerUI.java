package Network;

import java.io.*;



public class ServerUI {
    private final ServerConnection serverConnection;
    private final BufferedReader reader;
    private final String ADMIN_PASSWORD = "admin123";  // change before delivering

    public ServerUI(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start() {

        System.out.println("SERVER ADMIN PANEL");

        while (true) {

            System.out.println("\nChoose an option:\n");
            System.out.println("1) View connected clients\n");
            System.out.println("2) Stop server\n");

            String option;
            try {
                option = reader.readLine();
            } catch (IOException e) {
                System.out.println("Error reading input. Try again.");
                continue;
            }

            switch (option) {
                case "1":
                    showConnectedClients();
                    break;

                case "2":
                    attemptShutdown();
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void showConnectedClients() {
        int count = serverConnection.getConnectedClientCount();
        System.out.println("Connected clients: " + count);
    }

    private void attemptShutdown() {

        try {
            System.out.print("Enter admin password: ");
            String entered = reader.readLine();

            if (!entered.equals(ADMIN_PASSWORD)) {
                System.out.println("Incorrect password.");
                return;
            }

            int clients = serverConnection.getConnectedClientCount();

            if (clients > 0) {
                System.out.println("WARNING: There are " + clients + " connected clients");
                System.out.println("Are you sure you want to stop the server? (yes/no): ");
                String answer = reader.readLine();

                if(!answer.equalsIgnoreCase("yes")) {
                    System.out.println("Shutdown cancelled");
                    return;
                }
            }


            serverConnection.broadcastShutdownMessage();
            Thread.sleep(200);
            serverConnection.stopServer();
            System.exit(0);


        } catch (IOException e) {
            System.out.println("Error reading password.");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
