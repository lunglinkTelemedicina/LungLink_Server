package jdbcInterfaces;

import pojos.Signal;
import pojos.TypeSignal;

import java.util.*;

public interface SignalManager {

    void addSignal(Signal signal) throws Exception;
    Signal getSignalById(int signalId);
    List<Signal> getSignalsByRecordId(int recordId);
    List<Signal> getSignalsByClientId(int clientId);
    TypeSignal getSignalTypeByRecordId(int recordId);
    int getClientIdBySignalId(int signalId);


}
