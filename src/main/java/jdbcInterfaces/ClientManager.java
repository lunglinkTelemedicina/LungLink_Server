package jdbcInterfaces;

import pojos.Client;
import java.util.List;

public interface ClientManager {

    int addClient(Client client);
    Client getClientByUserId(int userId);
    void updateHeightWeight(int clientId, double weight, double height);
    void updateDoctorForClient(int clientId, int doctorId);
    int countClientsByDoctor(int doctorId);

}
