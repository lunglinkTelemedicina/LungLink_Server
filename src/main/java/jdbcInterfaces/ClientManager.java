package jdbcInterfaces;

import pojos.Client;
import java.util.List;

public interface ClientManager {

    public int addClient(Client client);
    public Client getClientById(int ClientId);
    public List<Client> getClients();
    public void updateClient(Client client);
    public void deleteClient(int ClientId);
    Client getClientByUserId(int userId);

}
