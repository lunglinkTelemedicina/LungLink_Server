package Network;

import pojos.Doctor;
import pojos.DoctorSpecialty;
import pojos.TypeSignal;

import java.util.List;

public class DoctorAssignmentService {

    private List<Doctor> doctors;

    public DoctorAssignmentService(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    public Doctor getDoctorForSignal(TypeSignal signalType) {

        DoctorSpecialty required = null;

        switch (signalType) {
            case ECG:
                required = DoctorSpecialty.CARDIOLOGIST;
                break;

            case EMG:
                required = DoctorSpecialty.RHEUMATOLOGIST;
                break;
        }

        if (required == null) return null;

        for (Doctor d : doctors) {
            if (d.getSpecialty() == required) {
                return d;
            }
        }

        return null; // Ningún doctor disponible
    }
}