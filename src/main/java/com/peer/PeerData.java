package com.peer;
import com.client.Client;
import java.util.ArrayList;

public class PeerData {
    public Client cliSock;
    public String id;
    public boolean handShook = false;
    public boolean interesting = false;
    public boolean[] bitfield;
    public int piecesLeft = Integer.MAX_VALUE;
    // For calculating preferred peers
    public int piecesDownloaded = 0;
    public int piecesLastIteration = 0;

    // Peers who we choked and who we are choked from
    public boolean isChoked = true;
    public boolean chokedFrom = false;

    public PeerData(Client cliSock, String peerID, boolean hasFile, int pieceCount) {
        this.cliSock = cliSock;
        this.id = peerID;
        if (hasFile) piecesLeft = 0;
    }

    // Calculate download rate for a peer
    public double calculateDownloadRate() {
        int downloaded = piecesDownloaded - piecesLastIteration;
        piecesLastIteration = piecesDownloaded;
        return downloaded;
    }

    public void calculatePiecesLeft() {
        piecesLeft = 0;
        for (int i = 0; i < bitfield.length; i++) {
            if (!bitfield[i]) piecesLeft++;
        }
    }
}
