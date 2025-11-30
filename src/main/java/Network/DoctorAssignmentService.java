package Network;

import jdbc.JDBCClient;
import jdbc.JDBCDoctor;
import pojos.Doctor;
import pojos.DoctorSpecialty;
import pojos.TypeSignal;

import java.util.List;

/**
 * Service used to select the most suitable doctor for a patient
 * based on the type of signal being recorded (ECG, EMG, etc.).
 * It also balances the workload by choosing the doctor within
 * the required specialty who currently has the fewest assigned patients.
 */

public class DoctorAssignmentService {

    private JDBCClient jdbcClient = new JDBCClient();
    private JDBCDoctor jdbcDoctor = new JDBCDoctor();

    /**
     * Creates a new doctor assignment service.
     */

    public DoctorAssignmentService() {}

    /**
     * Determines which doctor should receive the new signal or symptoms.
     * Chooses the right doctor based on what the patient sends. Symptoms go to
     * General Medicine, ECG signals go to a cardiologist, and EMG signals go to
     * a neurophysiologist. Once the specialty is known, the method selects the
     * doctor with the lightest workload.
     *
     * @param signalType the type of physiological signal being processed (or null for symptoms)
     * @return the selected doctor, or null if none match the required specialty
     */

    public Doctor getDoctorForSignal(TypeSignal signalType) {

        DoctorSpecialty required = null;
        Doctor bestDoctor = null;
        int minPatients = Integer.MAX_VALUE;

        if (signalType == null) {
            // when only symptoms are saved in the medical history
            required = DoctorSpecialty.GENERAL_MEDICINE;
        } else {
            switch (signalType) {
                case ECG:
                    required = DoctorSpecialty.CARDIOLOGIST;
                    break;

                case EMG:
                    required = DoctorSpecialty.NEUROPHYSIOLOGIST;
                    break;
            }
        }

        if (required == null) return null;

        // find the doctor in that specialty with the fewest patients
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