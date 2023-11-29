import java.io.*;
import java.util.*;

import com.server.Server;
import com.peer.Peer;

public class peerProcess {
    //java peerProcess <peer ID>
    public static void main(String[] args) throws Exception {
        System.out.println("Running Main from peerProcess.java");
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: peerProcess <peer ID>");
        }
        String peerID = args[0];
        int port = -1;
        boolean hasFile = false;
        //read PeerInfo.cfg
        ArrayList<String[]> peersToConnect = new ArrayList<>();
        try {
            File cfg = new File("PeerInfo.cfg");
            Scanner scanner = new Scanner(cfg);

            boolean done = false;
            while (scanner.hasNextLine() && !done) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\s+");

                if (parts[0].equals(peerID)) {
                    port = Integer.parseInt(parts[2]);
                    hasFile = Boolean.parseBoolean(parts[3]);
                    done = true;
                } else {
                    peersToConnect.add(parts);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Peer peer = new Peer(peerID, port, hasFile);
        for (String[] peerToConnect : peersToConnect) {
            peer.establishConnection(peerToConnect[0], peerToConnect[1], Integer.parseInt(peerToConnect[2]));
        }
    }

    private void log(String msg) {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }
    //TODO
    /*
    * type              value
    * choke              0
    * unchoke            1
    * interested         2
    * not interested     3
    * have               4
    * downloading        5
    * completed download 6
    * TCP                7
    * unchoke neighbor   8
    * prefered neighbor  9
    *
    //POTENTIAL IMPLEMENTATION
    Log log = new Log();
    if(type == 0){
        log.chokingLog(peerID, peer2ID);
    }
    else if (type == 1){
        log.unchokingLog(peerID, peer2ID);
    }
    else if (type == 2){
        log.interestedLog(peerID, peer2ID);
    }
    else if (type == 3){
        log.notInterestedLog(peerID, peer2ID);
    }
    else if (type == 4){
        log.haveLog(peerID, peer2ID, pieceIndex);
    }
    else if (type == 5){
        //NO BITFIELD LOG
        log.downloadingLog(peerID, peer2ID, pieceIndex, numPieces);
     }
    else if (type == 6){
        log.completedLog(peerID);
     }
    else if (type == 7){
        log.TCPLog(peerID, peer2ID);
    }
    else if (type == 8){
    *   log.changeOfOptimisticallyUnchokedNeighborLog(peerID, neighborID);
    }
    else if (type == 9){
        log.changeOfPreferredNeighborsLog(peerID, neighborsList);
     }
     */
    private void sendMessage(String peerID/*, Message.Message msg*/) {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }
    
    private void listenForMessages() {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }
}