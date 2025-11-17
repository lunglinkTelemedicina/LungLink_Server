package Network;

//No es un pojo. No es un dato, es parte del protocolo de comunicación entre clietne y servidor

public enum CommandType {
    SEND_SYMPTOMS,
    ADD_EXTRA_INFO,
    GET_HISTORY,
    GET_SIGNALS,
    SEND_ECG,
    SEND_EMG,
    DISCONNECT,
    UNKNOWN;

    //Convierte el string en enum
    public static CommandType fromString(String s) {
        try {
            return CommandType.valueOf(s);
        } catch (Exception e) {
            return UNKNOWN;
        }
    }
}

