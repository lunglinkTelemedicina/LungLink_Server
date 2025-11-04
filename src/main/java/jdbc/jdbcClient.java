package jdbc;

import jdbcInterfaces.ClientManager;
import pojos.Client;

import java.util.List;

public class jdbcClient implements ClientManager {


    @Override
    public void addClient(Client client) {

    }

    @Override
    public Client getClientById(int id) {
        return null;
    }

    @Override
    public List<Client> getClients() {
        return List.of();
    }

    @Override
    public void updateClient(Client client) {

    }

    @Override
    public void deleteClient(int id) {

    }
}
