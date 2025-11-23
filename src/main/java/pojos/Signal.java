package pojos;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Signal {

    private int signalId;
    private TypeSignal type;
    private int clientId; // Paciente
    private List<Integer> signal_values;    // Muestras de la señal
    private String signalFile;
    private int samplingRate = 100;
    private int recordId;  // (solo servidor lo usa)

    public Signal() {
        this.signal_values = new ArrayList<>();
    }

    public Signal(TypeSignal type,int clientId) {
        this.type = type;
        this.clientId=clientId;
        this.signal_values = new ArrayList<>();
    }

    public Signal(int signalId, TypeSignal type, List<Integer> signal_values, String signalFile, int samplingRate, int recordId) {
        this.signalId = signalId;
        this.type = type;
        this.signal_values = signal_values;
        this.signalFile = signalFile;
        this.samplingRate = samplingRate;
        this.recordId = recordId;
    }

    public TypeSignal getType() {
        return type;
    }

    public void setType(TypeSignal type) {
        this.type = type;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public List<Integer> getValues() {
        return signal_values;
    }

    public void setValues(List<Integer> values) {
        this.signal_values = values;
    }

    public void addSample(int sample) {
        signal_values.add(sample);
    }

    public String getSignalFile() {return signalFile;}

    public void setSignalFile(String signalFile) {this.signalFile = signalFile;}

    public int getSignalId() {return signalId;}

    public void setSignalId(int signalId) {this.signalId = signalId;}

    public int getSamplingRate() {return samplingRate;}

    public void setSamplingRate(int samplingRate) {this.samplingRate = samplingRate;}

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
// Reconstruir señal desde BYTES (servidor)


    public void fromByteArray(byte[] raw) {
        signal_values.clear();
        if(raw==null||raw.length==0) return;
        for (int i = 0; i < raw.length; i += 2) {
            int val = ((raw[i] & 0xFF) << 8) | (raw[i + 1] & 0xFF);
            signal_values.add(val);
        }
    }
    public String saveAsFile() throws IOException {

        String folder = "signals/";
        String fileName = type.name() + "_record" + recordId + ".csv";

        java.io.File dir = new java.io.File(folder);
        if (!dir.exists()) dir.mkdirs();

        FileWriter fw = new FileWriter(folder + fileName);

        for (int v : signal_values) fw.write(v + ",");
        fw.close();

        return fileName; // This goes into SQL in column signal_file
    }

    public String valuesToDB() {
        if (signal_values == null || signal_values.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < signal_values.size(); i++) {
            sb.append(signal_values.get(i));
            if (i < signal_values.size() - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public void valuesToList(String valuesString) {
        signal_values.clear();
        if (valuesString == null || valuesString.isEmpty()) return;

        String[] parts = valuesString.split(" ");
        for (String p : parts) {
            signal_values.add(Integer.parseInt(p));
        }
    }


}
