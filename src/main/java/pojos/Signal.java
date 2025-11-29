package pojos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Signal {

    private int signalId;
    private TypeSignal type;
    private List<Integer> signal_values;    // signal samples
    private String signalFile;
    private int samplingRate = 100;
    private int recordId;

    public Signal() {
        this.signal_values = new ArrayList<>();
    }

    public Signal(TypeSignal type) {
        this.type = type;
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

    public TypeSignal getType() {return type;}

    public void setType(TypeSignal type) {this.type = type;}

    public int getRecordId() {return recordId;}

    public void setRecordId(int recordId) {this.recordId = recordId;}

    public List<Integer> getValues() {return signal_values;}

    public void setValues(List<Integer> values) {this.signal_values = values;}

    public void addSample(int sample) {signal_values.add(sample);}

    public String getSignalFile() {return signalFile;}

    public void setSignalFile(String signalFile) {this.signalFile = signalFile;}

    public int getSignalId() {return signalId;}

    public void setSignalId(int signalId) {this.signalId = signalId;}

    public int getSamplingRate() {return samplingRate;}

    public void setSamplingRate(int samplingRate) {this.samplingRate = samplingRate;}

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

        File dir = new File(folder);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, fileName);

        try (FileWriter fw = new FileWriter(file)) {
            for (int v : signal_values) {
                fw.write(v + ",");
            }
        }


        return fileName;
    }


}
