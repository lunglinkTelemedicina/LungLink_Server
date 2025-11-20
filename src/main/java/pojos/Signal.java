package pojos;

import java.util.ArrayList;
import java.util.List;

public class Signal {

    private TypeSignal type;         // ECG o EMG
    private int clientId;            // Paciente
    private int recordId;            // (solo servidor lo usa)
    private List<Integer> values;    // Muestras de la señal

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


    // Reconstruir señal desde BYTES (servidor)


    public void fromByteArray(byte[] raw) {
        values.clear();
        for (int i = 0; i < raw.length; i += 2) {
            int val = ((raw[i] & 0xFF) << 8) | (raw[i + 1] & 0xFF);
            values.add(val);
        }
    }


    // Guardar señal como CSV en BD


    public String toCSV() {
        if (values.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int v : values) sb.append(v).append(",");
        return sb.substring(0, sb.length() - 1);
    }


    // Cargar señal desde CSV de BD


    public void fromCSV(String csv) {
        values.clear();
        if (csv == null || csv.isBlank()) return;

        String[] parts = csv.split(",");
        for (String p : parts) {
            values.add(Integer.parseInt(p.trim()));
        }
    }
}
