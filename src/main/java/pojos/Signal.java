package pojos;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Signal {

    private int signalId;
    private TypeSignal type;// Paciente
    private String signalFile;
    private int recordId;  // (solo servidor lo usa)

    public Signal() {
    }

    public Signal(TypeSignal type) {
        this.type = type;
    }

    public Signal(TypeSignal type, String signalFile, int recordId) {
        this.type = type;
        this.signalFile = signalFile;
        this.recordId = recordId;
    }

    public Signal(int signalId, TypeSignal type, String signalFile, int recordId) {
        this.signalId = signalId;
        this.type = type;
        this.signalFile = signalFile;
        this.recordId = recordId;
    }

    public TypeSignal getType() {return type;}

    public void setType(TypeSignal type) {this.type = type;}

    public int getRecordId() {return recordId;}

    public void setRecordId(int recordId) {this.recordId = recordId;}

    public String getSignalFile() {return signalFile;}

    public void setSignalFile(String signalFile) {this.signalFile = signalFile;}

    public int getSignalId() {return signalId;}

    public void setSignalId(int signalId) {this.signalId = signalId;}


// Reconstruir señal desde BYTES (servidor)


//    public void fromByteArray(byte[] raw) {
//        signal_values.clear();
//        if(raw==null||raw.length==0) return;
//        for (int i = 0; i < raw.length; i += 2) {
//            int val = ((raw[i] & 0xFF) << 8) | (raw[i + 1] & 0xFF);
//            signal_values.add(val);
//        }
//    }
//    public String saveAsFile() throws IOException {
//
//        String folder = "signals/";
//        String fileName = type.name() + "_record" + recordId + ".csv";
//
//        java.io.File dir = new java.io.File(folder);
//        if (!dir.exists()) dir.mkdirs();
//
//        FileWriter fw = new FileWriter(folder + fileName);
//
//        for (int v : signal_values) fw.write(v + ",");
//        fw.close();
//
//        return fileName; // This goes into SQL in column signal_file
//    }

//    public String valuesToDB() {
//        if (signal_values == null || signal_values.isEmpty()) {
//            return "";
//        }
//
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < signal_values.size(); i++) {
//            sb.append(signal_values.get(i));
//            if (i < signal_values.size() - 1) {
//                sb.append(" ");
//            }
//        }
//        return sb.toString();
//    }

//    public void valuesToList(String valuesString) {
//        signal_values.clear();
//        if (valuesString == null || valuesString.isEmpty()) return;
//
//        String[] parts = valuesString.split(" ");
//        for (String p : parts) {
//            signal_values.add(Integer.parseInt(p));
//        }
//    }


}
