package jdbc;
import jdbcInterfaces.*;
import pojos.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@code DoctorManager} interface using JDBC
 * for managing Doctor and associated Client data persistence in the database.
 *
 * This class follows the Singleton pattern.
 */
public class JDBCDoctor implements DoctorManager {

    private static JDBCDoctor instance;
    /**
     * Retrieves the single instance of the JDBCDoctor class (Singleton pattern).
     *
     * @return The JDBCDoctor instance.
     */
    public static synchronized JDBCDoctor getInstance() {
        if (instance == null) {
            instance = new JDBCDoctor();
        }
        return instance;
    }
    /**
     * Inserts a new Doctor into the 'doctor' table and sets the generated doctor ID
     * back into the provided Doctor object.
     *
     * @param doctor The Doctor object to insert.
     */
    @Override
    public void addDoctor(Doctor doctor) {

        String sql = "INSERT INTO doctor (name, surname, email, specialty, user_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, doctor.getName());
            ps.setString(2, doctor.getSurname());
            ps.setString(3, doctor.getEmail());
            ps.setString(4, doctor.getSpecialty() != null ? doctor.getSpecialty().name() : null);
            ps.setInt(5, doctor.getUserId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    doctor.setDoctorId(rs.getInt(1));
                }
            }

            System.out.println("Doctor inserted with ID: " + doctor.getDoctorId());

        } catch (SQLException e) {
            System.err.println("Error when inserting doctor:");
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a Doctor object from the database using its associated User ID.
     *
     * @param userId The ID of the user associated with the doctor.
     * @return The Doctor object corresponding to the user ID, or null if not found or an error occurs.
     */
    public Doctor getDoctorByUserId(int userId) {

        String sql = "SELECT * FROM doctor WHERE user_id = ?";
        Doctor d = null;

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                d = new Doctor();
                d.setDoctorId(rs.getInt("doctor_id"));
                d.setName(rs.getString("name"));
                d.setSurname(rs.getString("surname"));
                String specStr = rs.getString("specialty");
                if (specStr != null) {
                    d.setSpecialty(DoctorSpecialty.valueOf(specStr));
                }
                d.setUserId(rs.getInt("user_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error when obtaining doctor by user_id");
            e.printStackTrace();
        }

        return d;
    }
    /**
     * Retrieves a list of all Doctor entries from the database.
     *
     * @return A List of all Doctor objects found.
     */
    @Override
    public List<Doctor> getDoctors() {

        String sql = "SELECT * FROM doctor";
        List<Doctor> doctors = new ArrayList<>();

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setDoctorId(rs.getInt("doctor_id"));
                doctor.setName(rs.getString("name"));
                doctor.setSurname(rs.getString("surname"));
                doctor.setEmail(rs.getString("email"));
                String specialtyStr = rs.getString("specialty");
                if (specialtyStr != null) {
                    doctor.setSpecialty(DoctorSpecialty.valueOf(specialtyStr));
                }
                doctor.setUserId(rs.getInt("user_id"));
                doctors.add(doctor);
            }

        } catch (SQLException e) {
            System.err.println("Error when obtaining the list of doctors:");
            e.printStackTrace();
        }

        return doctors;
    }
    /**
     * Retrieves a list of all Client objects assigned to a specific doctor ID.
     *
     * @param doctorId The ID of the doctor whose clients are to be retrieved.
     * @return A List of Client objects assigned to the doctor.
     */
    public List<Client> getClientsByDoctorId(int doctorId) {

        List<Client> list = new ArrayList<>();
        String sql = "SELECT * FROM client WHERE doctor_id = ?";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Client c = new Client();
                c.setClientId(rs.getInt("client_id"));
                c.setName(rs.getString("name"));
                c.setSurname(rs.getString("surname"));

                String dob = rs.getString("dob");
                if (dob != null) c.setDob(LocalDate.parse(dob));

                c.setMail(rs.getString("mail"));

                String sexStr = rs.getString("sex");
                if (sexStr != null) c.setSex(Sex.valueOf(sexStr));

                c.setWeight(rs.getDouble("weight"));
                c.setHeight(rs.getDouble("height"));
                c.setDoctorId(rs.getInt("doctor_id"));
                c.setUserId(rs.getInt("user_id"));

                list.add(c);
            }

        } catch (SQLException e) {
            System.err.println("Error when obtaining clients of doctor " + doctorId);
            e.printStackTrace();
        }

        return list;
    }
    /**
     * Checks if a specific client is currently assigned to a specific doctor.
     *
     * @param doctorId The ID of the doctor to check assignment against.
     * @param clientId The ID of the client to verify.
     * @return true if the client is assigned to the doctor, false otherwise.
     */
    public boolean isPatientAssignedToDoctor(int doctorId, int clientId) {
        String sql = "SELECT COUNT(*) FROM client WHERE client_id = ? AND doctor_id = ?";

        JDBCConnectionManager cm = JDBCConnectionManager.getInstance();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            ps.setInt(2, doctorId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    /**
     * Inserts a default doctor ("Alfredo Jiménez") into the database if one does not already exist,
     * relying on a preexisting "AlfredoJimenez" user entry.
     *
     * @param conn The active database connection.
     * @implNote This method requires that the corresponding user ("AlfredoJimenez") already exists in the 'user' table.
     */
    public void insertDoctorByDefault(Connection conn) {

        String email = "ajimenez@lunglink.com";

        String checkSql = "SELECT COUNT(*) FROM doctor WHERE email = ?";

        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Default doctor already exists.");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        int userId = JDBCUser.getInstance().getUserIdByUsernameOnlyIfExists("AlfredoJimenez");

        if (userId == -1) {
            System.err.println("ERROR: Default doctor user not found.");
            return;
        }

        String insertSql =
                "INSERT INTO doctor (name, surname, email, specialty, user_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, "Alfredo");
            ps.setString(2, "Jiménez");
            ps.setString(3, email);
            ps.setString(4, DoctorSpecialty.GENERAL_MEDICINE.name());
            ps.setInt(5, userId);

            ps.executeUpdate();
            System.out.println("Default doctor inserted.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the doctor ID of the default doctor ("ajimenez@lunglink.com").
     *
     * @return The doctor ID of the default doctor, or -1 if the doctor is not found or an error occurs.
     */
    public int getDefaultDoctorId() {
        String sql = "SELECT doctor_id FROM doctor WHERE email = ?";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "ajimenez@lunglink.com"); //default doctor
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("doctor_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }


}

