package jdbc;

import jdbcInterfaces.*;
import pojos.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JDBCDoctor implements DoctorManager {

    private static JDBCDoctor instance;

    public static synchronized JDBCDoctor getInstance() {
        if (instance == null) {
            instance = new JDBCDoctor();
        }
        return instance;
    }

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

