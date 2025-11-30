package jdbcInterfaces;

import pojos.Signal;
import pojos.TypeSignal;

import java.util.*;

/**
 * Manages persistence and retrieval operations for the {@code Signal} entity
 * in the database, including complex lookups by client and medical record.
 */
public interface SignalManager {

    /**
     * Inserts a new signal record into the database.
     *
     * @param signal The {@code Signal} object to add.
     * @throws Exception if an error occurs during the database insertion.
     */
    void addSignal(Signal signal) throws Exception;

    /**
     * Retrieves a specific signal record from the database using its unique ID.
     *
     * @param signalId The unique ID of the signal to retrieve.
     * @return The populated {@code Signal} object, or null if the signal is not found.
     */
    Signal getSignalById(int signalId);

    /**
     * Retrieves all signal records associated with a specific medical history record ID.
     *
     * @param recordId The ID of the medical history record whose signals are to be retrieved.
     * @return A list of {@code Signal} objects associated with the record. Returns an empty list if none are found.
     */
    List<Signal> getSignalsByRecordId(int recordId);

    /**
     * Retrieves a list of all signal records associated with a specific client ID.
     *
     * @param clientId The ID of the client whose signals are to be retrieved.
     * @return A list of {@code Signal} objects associated with the client.
     */
    List<Signal> getSignalsByClientId(int clientId);

    /**
     * Retrieves the type (ECG or EMG) of the signal associated with a given medical history record ID.
     *
     * @param recordId The ID of the medical history record.
     * @return The {@code TypeSignal} enum value (ECG or EMG), or null if no signal type is found for the record.
     */
    TypeSignal getSignalTypeByRecordId(int recordId);

    /**
     * Retrieves the client ID associated with a specific signal ID.
     *
     * @param signalId The ID of the signal whose owner is to be determined.
     * @return The ID of the client who owns the signal, or -1 if the association is not found.
     */
    int getClientIdBySignalId(int signalId);
}