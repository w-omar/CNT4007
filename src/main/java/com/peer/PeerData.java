package com.peer;
import com.client.Client;
import java.util.ArrayList;

public class PeerData {
    public Client cliSock;
    public boolean handShook = false;
    public boolean[] bitfield;

    public PeerData(Client cliSock) {
        this.cliSock = cliSock;
    }
}
