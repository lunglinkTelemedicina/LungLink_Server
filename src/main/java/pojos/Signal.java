package pojos;

import java.util.LinkedList;
import java.util.List;

public class Signal {

    private List<Integer> values;
    private String signalFile;
    private TypeSignal type;
    private static final int samplingRate = 100; //en hercios
    private int clientId;

    public Signal(TypeSignal type, int clientId) {
        this.values = new LinkedList<>();
        this.type = type;
        this.clientId = clientId;
    }

    public List<Integer> getValues() {
        return values;
    }

    public void setValues(List<Integer> values) {
        this.values = values;
    }

    public String getSignalFile() {
        return signalFile;
    }

    public void setSignalFile(String signalFile) {
        this.signalFile = signalFile;
    }

    public TypeSignal getType() {
        return type;
    }

    public void setType(TypeSignal type) {
        this.type = type;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "Signal{" +
                "values=" + values +
                ", signalFile='" + signalFile + '\'' +
                ", type=" + type +
                ", clientId=" + clientId +
                '}';
    }

    public void valuesToList(String values) {
        List<Integer> newValues = new LinkedList<>();
        String[] element = values.split(" ");
        for (String e : element) {
            newValues.add(Integer.parseInt(e));
        }
        this.values = newValues;
    }

    //es para que al guardarlo en la db se giarde en forma de cadena de texto en la columan correpsondiente
    public String valuesToDB() {
        StringBuilder sb = new StringBuilder();
        String sep = " ";

        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i));
            if (i < values.size() - 1) {
                sb.append(sep);
            }
        }
        return sb.toString();
    }
}


