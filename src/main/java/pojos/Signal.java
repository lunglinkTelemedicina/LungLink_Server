package pojos;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Signal {

    private TypeSignal type;         // ECG o EMG
    private int clientId;            // Paciente
    private int recordId;            // (solo servidor lo usa)
    private List<Integer> values;    // Muestras de la señal
    private String signalFile;

    public Signal() {
        this.values = new ArrayList<>();
    }

    public Signal(TypeSignal type, int clientId) {
        this.type = type;
        this.clientId = clientId;
        this.values = new ArrayList<>();
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

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public List<Integer> getValues() {
        return values;
    }

    public void setValues(List<Integer> values) {
        this.values = values;
    }

    public void addSample(int sample) {
        values.add(sample);
    }

    public String getSignalFile() {return signalFile;}

    public void setSignalFile(String signalFile) {this.signalFile = signalFile;}

    // Reconstruir señal desde BYTES (servidor)


    public void fromByteArray(byte[] raw) {
        values.clear();
        if(raw==null||raw.length==0) return;
        for (int i = 0; i < raw.length; i += 2) {
            int val = ((raw[i] & 0xFF) << 8) | (raw[i + 1] & 0xFF);
            values.add(val);
        }
    }
    public String saveAsFile() throws IOException {

        String folder = "signals/";
        String fileName = type.name() + "_record" + recordId + ".csv";

        java.io.File dir = new java.io.File(folder);
        if (!dir.exists()) dir.mkdirs();

        FileWriter fw = new FileWriter(folder + fileName);

        for (int v : values) fw.write(v + ",");
        fw.close();

        return fileName; // This goes into SQL in column signal_file
    }

}
