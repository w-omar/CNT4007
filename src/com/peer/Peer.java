package src.com.peer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import src.com.server.Server;
import src.com.client.Client;

public class Peer {
    //to be determined over course of runtime
    private ArrayList<String> peersList;
    private ArrayList<String> interestedPeers;
    private ArrayList<String> preferredNeighbors;
    private String optimisticNeighbor;

    //determined by Config.cfg
    private int numberOfPreferredNeighbors;
    private int unchokingInterval;
    private int optimisticUnchokingInterval;
    private int fileSize;
    private int pieceSize;
    private String fileName;

    //dictated by peerProcess
    private String ID;
    private int portNumber;
    private boolean hasFile;

    //Each peer should write its log into the log file ‘log_peer_[peerID].log’ at the working directory
    private String logFileName;

    private ArrayList<Boolean> bitField;

    public Peer(String peerId, int port, boolean hasFile) throws FileNotFoundException {
        this.ID = peerId;
        this.portNumber = port;
        this.hasFile = hasFile;
        readCFG();
        initBitfield();
        init();
    }

    //starts server and client
    private void init(){
        Server server = new Server(portNumber);
        Thread thread = new Thread(server);
        thread.start();
    }

    public int getPortNumber() {
        return portNumber;
    }

    //read config file helper
    private void readCFG() throws FileNotFoundException {
        ArrayList <String> cfgVars = new ArrayList<>();
        try {
            File cfg = new File("src/Common.cfg");
            Scanner scanner = new Scanner(cfg);
        
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\s+");

                if (parts.length == 2) {
                    cfgVars.add(parts[1]);
                    System.out.println(parts[1]);
                }
            }
            scanner.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        this.numberOfPreferredNeighbors = Integer.parseInt(cfgVars.get(0));
        this.unchokingInterval = Integer.parseInt(cfgVars.get(1));
        this.optimisticUnchokingInterval = Integer.parseInt(cfgVars.get(2));
        this.fileName = cfgVars.get(3);
        this.fileSize = Integer.parseInt(cfgVars.get(4));
        this.pieceSize = Integer.parseInt(cfgVars.get(5));
    }
    //writes to log

    //initializes bitfields
    public void initBitfield(){
        int pieceCount = fileSize / pieceSize;
        for (int i = 0; i < pieceCount; i++) {
            bitField.add(hasFile);
        }
    }

    //connect to peer
    public void establishConnection(String peerID, String hostName, int port) throws IOException {
        //TODO update peersList
        peersList.add(peerID);

        //TODO connect to peer
        Client client = new Client(hostName, port);
        Thread thread = new Thread(client);
        thread.start();
    }

    /*  peer A calculates the downloading rate from each of its neighbors,
        respectively, during the previous unchoking interval. Among neighbors that are interested
        in its data, peer A picks k neighbors that has fed its data at the highest rate. If more than
        two peers have the same rate, the tie should be broken randomly. Then it unchokes those
        preferred neighbors by sending ‘unchoke’ messages and it expects to receive ‘request’
        messages from them. If a preferred neighbor is already unchoked, then peer A does not
        have to send ‘unchoke’ message to it. All other neighbors previously unchoked but not
        selected as preferred neighbors at this time should be choked unless it is an optimistically
        unchoked neighbor. To choke those neighbors, peer A sends ‘choke’ messages to them
        and stop sending pieces
    */
    private ArrayList<Peer> determinePreferredNeighbors(ArrayList<Peer> interestedPeers) {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }

    /*  Suppose that peer A receives an ‘unchoke’ message from peer B. Peer A selects a piece
        randomly among the pieces that peer B has, and peer A does not have, and peer A has
        not requested yet.
    */
    private int selectPiece(ArrayList<Boolean> peerBitField) {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }
}