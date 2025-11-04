package src.jdbcInterfaces;

import src.pojos.Client;
import java.util.List;

public interface ClientManager {

    public void addClient(Client client);
    public Client getClientById(int id);
    public List<Client> getClients();
    public void updateClient(Client client);
    public void deleteClient(int id);

    //habra que decidir cuales más poner o cuales más nos hacen falta

}
