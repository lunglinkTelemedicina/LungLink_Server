package jdbc;

import jdbcInterfaces.*;
import pojos.Doctor;
import pojos.DoctorSpecialty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCDoctor implements DoctorManager {

    @Override
    public void addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctor (name, surname, email, specialty, user_id) VALUES (?, ?, ?, ?, ?)";
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, doctor.getName());
            ps.setString(2, doctor.getSurname());
            ps.setString(3, doctor.getEmail());
            ps.setString(4, doctor.getSpecialty() != null ? doctor.getSpecialty().toString() : null);
            ps.setInt(5, doctor.getUserId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    doctor.setDoctorId(rs.getInt(1));
                }
            }

            System.out.println("Doctor insertado con ID: " + doctor.getDoctorId());

        } catch (SQLException e) {
            System.err.println("Error al insertar doctor:");
            e.printStackTrace();
        }
    }

    @Override
    public Doctor getDoctorById(int id) {
        String sql = "SELECT * FROM doctor WHERE doctor_id = ?";
        JDBCConnectionManager cm = new JDBCConnectionManager();
        Doctor doctor = null;

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                doctor = new Doctor();
                doctor.setDoctorId(rs.getInt("doctor_id"));
                doctor.setName(rs.getString("name"));
                doctor.setSurname(rs.getString("surname"));
                doctor.setEmail(rs.getString("email"));
                String specialtyStr = rs.getString("specialty");
                if (specialtyStr != null) {
                    doctor.setSpecialty(DoctorSpecialty.valueOf(specialtyStr));
                }
                doctor.setUserId(rs.getInt("user_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener doctor por ID:");
            e.printStackTrace();
        }

        return doctor;
    }

    @Override
    public List<Doctor> getDoctors() {
        String sql = "SELECT * FROM doctor";
        List<Doctor> doctors = new ArrayList<>();
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
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
            System.err.println("Error al obtener la lista de doctores:");
            e.printStackTrace();
        }

        return doctors;
    }

    @Override
    public void updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctor SET name = ?, surname = ?, email = ?, specialty = ?, user_id = ? WHERE doctor_id = ?";
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, doctor.getName());
            ps.setString(2, doctor.getSurname());
            ps.setString(3, doctor.getEmail());
            ps.setString(4, doctor.getSpecialty() != null ? doctor.getSpecialty().toString() : null);
            ps.setInt(5, doctor.getUserId());
            ps.setInt(6, doctor.getDoctorId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Doctor actualizado correctamente (ID " + doctor.getDoctorId() + ")");
            } else {
                System.out.println("No se encontró ningún doctor con ese ID.");
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar doctor:");
            e.printStackTrace();
        }
    }

    @Override
    public void deleteDoctor(int id) {
        String sql = "DELETE FROM doctor WHERE doctor_id = ?";
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Doctor eliminado correctamente (ID " + id + ")");
            } else {
                System.out.println("No se encontró ningún doctor con ese ID.");
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar doctor:");
            e.printStackTrace();
        }
    }
}
