package com.peer;
import com.client.Client;
import java.util.ArrayList;

public class PeerData {
    public Client cliSock;
    public boolean handShook = false;
    public ArrayList<Boolean> bitfield = new ArrayList<>();

    public PeerData(Client cliSock) {
        this.cliSock = cliSock;
    }
}
