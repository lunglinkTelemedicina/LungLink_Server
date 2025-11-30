package jdbcInterfaces;

import pojos.Client;
import java.util.List;
/**
 * Manages persistence and retrieval operations for the {@code Client} entity
 * in the database.
 */
public interface ClientManager {

    /**
     * Inserts a new client record into the database.
     * * @param client The {@code Client} object to add. Its ID will be updated upon successful insertion.
     * @return The auto-generated ID of the newly inserted client, or -1 if the operation fails.
     */
    int addClient(Client client);

    /**
     * Retrieves a client from the database using the associated user ID.
     *
     * @param userId The ID of the user linked to the client profile.
     * @return The retrieved {@code Client} object, or null if no client is found for the given user ID.
     */
    Client getClientByUserId(int userId);

    /**
     * Updates the weight and height fields for an existing client identified by their ID.
     *
     * @param clientId The ID of the client to update.
     * @param weight The new weight value (double).
     * @param height The new height value (double).
     */
    void updateHeightWeight(int clientId, double weight, double height);

    /**
     * Assigns or unassigns a doctor to a specific client.
     *
     * @param clientId The ID of the client to modify.
     * @param doctorId The ID of the doctor to assign. Use a value <= 0 to unassign (set to NULL).
     */
    void updateDoctorForClient(int clientId, int doctorId);

    /**
     * Counts the total number of clients currently assigned to a given doctor.
     *
     * @param doctorId The ID of the doctor whose clients are to be counted.
     * @return The number of clients assigned to the doctor.
     */
    int countClientsByDoctor(int doctorId);

}