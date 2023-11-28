package src;

import src.com.peer.Peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

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
            File cfg = new File("src/PeerInfo.cfg");
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
     */
    private void sendMessage(String peerID/*, Message msg*/) {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }
    
    private void listenForMessages() {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }
}