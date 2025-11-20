package jdbc;

import jdbcInterfaces.MedicalHistoryManager;
import pojos.MedicalHistory;
import pojos.Signal;
import pojos.TypeSignal;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;


public class JDBCMedicalHistory implements MedicalHistoryManager {

    @Override
    public int addMedicalHistory(MedicalHistory m) {
        String sql = """
            INSERT INTO MedicalHistory (date, client_id, doctor_id, observations)
            VALUES (?, ?, ?, ?)
        """;

        int generatedId = -1;
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getDate() != null ? m.getDate().toString() : null);
//            ps.setInt(2, m.getClientId());
//            ps.setInt(3, m.getDoctorId());
            if (m.getClientId() > 0) {
                ps.setInt(2, m.getClientId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            if (m.getDoctorId() > 0) {
                ps.setInt(3, m.getDoctorId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setString(4, m.getObservations());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    m.setRecordId(generatedId);
                }
            }

            System.out.println("Historial médico insertado con ID: " + generatedId);

        } catch (SQLException e) {
            System.err.println("Error al insertar historial médico:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }

        return generatedId;
    }

    @Override
    public MedicalHistory getMedicalHistoryById(int recordId) {
        String sql = "SELECT * FROM medicalhistory WHERE record_id = ?";
        MedicalHistory mh = null;
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recordId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                mh = new MedicalHistory();
                mh.setRecordId(rs.getInt("record_id"));

                String dateStr = rs.getString("date");
                if (dateStr != null) mh.setDate(LocalDate.parse(dateStr));

                mh.setClientId(rs.getInt("client_id"));
                mh.setDoctorId(rs.getInt("doctor_id"));
                mh.setObservations(rs.getString("observations"));

                String symptomsStr = rs.getString("symptomsList");
                if (symptomsStr != null && !symptomsStr.isEmpty()) {
                    mh.setSymptomsList(Arrays.asList(symptomsStr.split(",")));
                }

                loadSignalsForHistory(mh, conn);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener historial médico por ID:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }

        return mh;
    }

    @Override
    public List<MedicalHistory> getMedicalHistoryByClientId(int clientId) {
        List<MedicalHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM medicalhistory WHERE client_id = ?";
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MedicalHistory mh = new MedicalHistory();
                mh.setRecordId(rs.getInt("record_id"));

                String dateStr = rs.getString("date");
                if (dateStr != null) mh.setDate(LocalDate.parse(dateStr));

                mh.setClientId(rs.getInt("client_id"));
                mh.setDoctorId(rs.getInt("doctor_id"));
                mh.setObservations(rs.getString("observations"));

                String symptomsStr = rs.getString("symptomsList");
                if (symptomsStr != null && !symptomsStr.isEmpty()) {
                    mh.setSymptomsList(Arrays.asList(symptomsStr.split(",")));
                }

                loadSignalsForHistory(mh, conn);

                list.add(mh);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener historiales médicos por cliente:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }

        return list;
    }

    @Override
    public List<MedicalHistory> getMedicalHistories() {
        List<MedicalHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM MedicalHistory";
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                MedicalHistory mh = new MedicalHistory();
                mh.setRecordId(rs.getInt("record_id"));

                String dateStr = rs.getString("date");
                if (dateStr != null) mh.setDate(LocalDate.parse(dateStr));

                mh.setClientId(rs.getInt("client_id"));
                mh.setDoctorId(rs.getInt("doctor_id"));
                mh.setObservations(rs.getString("observations"));

                loadSignalsForHistory(mh, conn);
                list.add(mh);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener todos los historiales médicos:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }

        return list;
    }

    public void addSymptoms(int recordId, List<String> symptomsList) {
        if (symptomsList == null || symptomsList.isEmpty()) {
            System.out.println("No hay síntomas para añadir.");
            return;
        }

        // Convertimos la lista en una sola cadena separada por comas
        String symptomsStr = String.join(",", symptomsList);

        String sql = "UPDATE medicalhistory SET symptomsList = ? WHERE record_id = ?";
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, symptomsStr);
            ps.setInt(2, recordId);

            ps.executeUpdate();

            System.out.println("Síntomas añadidos correctamente al historial " + recordId);

        } catch (SQLException e) {
            System.err.println("Error al añadir síntomas:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }
    }


    @Override
    public void deleteMedicalHistory(int recordId) {
        String sql = "DELETE FROM MedicalHistory WHERE record_id = ?";
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recordId);
            ps.executeUpdate();

            System.out.println("Historial médico eliminado correctamente.");

        } catch (SQLException e) {
            System.err.println("Error al eliminar historial médico:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }
    }

    public void addSignalToMedicalHistory(int recordId, Signal signal) {
        if (signal == null) {
            System.out.println("Null singal, it cannot be added.");
            return;
        }

        String sql = """
        INSERT INTO signal (type, values, signal_file, sampling_rate, client_id, record_id)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {


            if (signal.getType() != null) {
                ps.setString(1, signal.getType().name());
            } else {
                ps.setNull(1, Types.VARCHAR);
            }
            ps.setString(2, signal.valuesToDB());
            ps.setString(3, signal.getSignalFile());
            ps.setInt(4, 100);
            ps.setInt(5, signal.getClientId());
            ps.setInt(6, recordId);

            ps.executeUpdate();
            System.out.println("Signal added to the record " + recordId);

        } catch (SQLException e) {
            System.err.println("Error while adding the signal:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }
    }

    public void loadSignalsForHistory(MedicalHistory mh, Connection conn) {
        if (mh == null) return;

        String sql = "SELECT * FROM signal WHERE record_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mh.getRecordId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Signal signal = new Signal();

                String typeStr = rs.getString("type");
                if (typeStr != null) {
                    signal.setType(TypeSignal.valueOf(typeStr.toUpperCase()));
                }

                // values (usando el metodo valuesToList de la clase Signal)
                String valuesString = rs.getString("values");
                if (valuesString != null) {
                    signal.valuesToList(valuesString);
                }

                signal.setSignalFile(rs.getString("signal_file"));
                signal.setClientId(rs.getInt("client_id"));

                // Ahora asignamos la señal al MedicalHistory
                if (signal.getType() == TypeSignal.EMG) {
                    mh.setSignalEMG(signal);
                } else if (signal.getType() == TypeSignal.ECG) {
                    mh.setSignalECG(signal);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar señales del historial médico:");
            e.printStackTrace();
        }
    }



}
