package Network;

import jdbc.JDBCClient;
import jdbc.JDBCDoctor;
import pojos.Doctor;
import pojos.DoctorSpecialty;
import pojos.TypeSignal;

import java.util.List;

public class DoctorAssignmentService {

    //private List<Doctor> doctors;
    private JDBCClient jdbcClient = new JDBCClient();
    private JDBCDoctor jdbcDoctor = new JDBCDoctor();

//    public DoctorAssignmentService(List<Doctor> doctors) {
//        this.doctors = doctors;
//    }
    public DoctorAssignmentService() {}

    public Doctor getDoctorForSignal(TypeSignal signalType) {

        DoctorSpecialty required = null;
        Doctor bestDoctor = null;
        int minPatients = Integer.MAX_VALUE;

        switch (signalType) {
            case ECG:
                required = DoctorSpecialty.CARDIOLOGIST;
                break;

            case EMG:
                required = DoctorSpecialty.NEUROPHYSIOLOGIST;
                break;
        }

        if (required == null) return null;

        //Find the doctor in that specialty with the fewest patients

        List<Doctor> doctors = jdbcDoctor.getDoctors();

        for (Doctor d : doctors) {
            if (d.getSpecialty() == required) {
                int count = jdbcClient.countClientsByDoctor(d.getDoctorId());

                if (count < minPatients) {
                    minPatients = count;
                    bestDoctor = d;
                }
            }
        }

        return bestDoctor;
    }
}