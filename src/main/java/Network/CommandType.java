package Network;

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

