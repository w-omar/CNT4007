package src;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import src.com.server.Server;
import src.com.peer.Peer;

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
}