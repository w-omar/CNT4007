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

    // Peers who we choked and who we are choked from
    public boolean isChoked = true;
    public boolean chokedFrom = false;

    public PeerData(Client cliSock, String peerID) {
        this.cliSock = cliSock;
        this.id = peerID;
    }

    // Calculate download rate for a peer
    public double calculateDownloadRate() {
        int downloaded = piecesDownloaded - piecesLastIteration;
        piecesLastIteration = piecesDownloaded;
        return downloaded;
    }
}
