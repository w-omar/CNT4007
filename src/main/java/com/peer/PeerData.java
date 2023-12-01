package com.peer;
import com.client.Client;
import java.util.ArrayList;

public class PeerData {
    public Client cliSock;
    public String id;
    public boolean handShook = false;
    public boolean interesting = false;
    public boolean[] bitfield;

    // For calculating preferred peers
    public int piecesDownloaded = 0;
    public int piecesLastIteration = 0;


    public PeerData(Client cliSock) {
        this.cliSock = cliSock;
    }

    // Calculate download rate for a peer
    public double calculateDownloadRate() {
        int downloaded = piecesDownloaded - piecesLastIteration;
        piecesLastIteration = piecesDownloaded;
        return downloaded;
    }
}
