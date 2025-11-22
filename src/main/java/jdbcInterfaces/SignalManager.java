package jdbcInterfaces;

import pojos.Signal;
import java.util.*;

public interface SignalManager {

    void addSignal(Signal signal) throws Exception;
    Signal getSignalById(int signalId);
    List<Signal> getSignalsByRecordId(int recordId);
    void deleteSignal(int signalId);

}
