package Network;

/**
 * Contains all commands used in the client–server communication protocol.
 * Each value represents an action that the server can interpret and process.
 */
public enum CommandType {
    SEND_SYMPTOMS,
    ADD_EXTRA_INFO,
    GET_HISTORY,
    GET_SIGNALS,
    SEND_ECG,
    SEND_EMG,
    DISCONNECT,
    UNKNOWN,
    LOGIN_USER,
    REGISTER_USER,
    CHECK_CLIENT,
    CREATE_CLIENT,
    CREATE_DOCTOR,
    CHECK_DOCTOR,
    GET_DOCTOR_PATIENTS,
    GET_PATIENT_HISTORY_DOCTOR,
    GET_PATIENT_SIGNALS_DOCTOR,
    ADD_OBSERVATIONS,
    GET_SIGNAL_FILE;

    /**
     * Converts a string into the corresponding CommandType.
     * Returns UNKNOWN if the string does not match any valid command.
     * @param s the string received from the network
     * @return the matching command type or UNKNOWN if no match exists
     */

    //Converts the string into an enum
    public static CommandType fromString(String s) {
        if (s == null) return UNKNOWN;

        s = s.trim().toUpperCase();
        try {
            return CommandType.valueOf(s);
        } catch (Exception e) {
            return UNKNOWN;
        }
    }
}