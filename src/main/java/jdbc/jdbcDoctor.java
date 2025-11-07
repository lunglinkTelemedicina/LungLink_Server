package jdbc;

import jdbcInterfaces.DoctorManager;
import pojos.Doctor;

import java.util.List;


public class jdbcDoctor implements DoctorManager {
    @Override
    public void addDoctor(Doctor doctor) {

    }

    @Override
    public Doctor getDoctorById(int id) {
        return null;
    }

    @Override
    public List<Doctor> getDoctors() {
        return List.of();
    }

    @Override
    public void updateDoctor(Doctor doctor) {

    }

    @Override
    public void deleteDoctor(int id) {

    }
}
