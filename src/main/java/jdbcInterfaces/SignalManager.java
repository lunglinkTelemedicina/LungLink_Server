package jdbcInterfaces;

import pojos.Signal;
import java.util.*;

public interface SignalManager {

    void addSignal(Signal signal) throws Exception;
    Signal getSignalById(int signalId) throws Exception;
    List<Signal> getSignalsByClient(int clientId) throws Exception;
    void deleteSignal(int signalId) throws Exception;

}
