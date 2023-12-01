package com.peer;

import Logs.Logs;
import com.client.Client;
import com.server.Server;
import com.peer.PeerData;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Math;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

public class Peer {
    //to be determined over course of runtime
    public ArrayList<String> interestedPeers = new ArrayList<>();
    private ArrayList<String> preferredNeighbors = new ArrayList<>();
    private String optimisticNeighbor;

    //determined by Config.cfg
    private int numberOfPreferredNeighbors;
    private int unchokingInterval;
    private int optimisticUnchokingInterval;
    private int fileSize;
    private int pieceSize;
    public int pieceCount;
    private String fileName;

    //dictated by peerProcess
    private String ID;
    private int portNumber;
    private boolean hasFile;

    //Each peer should write its log into the log file ‘log_peer_[peerID].log’ at the working directory
    private String logFileName;

    public boolean[] bitfield;

    // Tracks the peers who are connected and their respective client sockets
    public HashMap<String, PeerData> peerHM = new HashMap<>();

    public Peer(String peerId, int port, boolean hasFile) throws FileNotFoundException {
        this.ID = peerId;
        this.portNumber = port;
        this.hasFile = hasFile;
        readCFG();
        initBitfield();
        init();
    }

    // Initiates listening server
    private void init(){
        //Start server
        Server server = new Server(this, portNumber);
        Thread thread = new Thread(server);
        thread.start();

        //schedule selecting neighbors
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        Runnable choosePreferredNeighbors = this::determinePreferredNeighbors;
        Runnable chooseOptimisticUnchokedNeighbor = this::changeOptimisticNeighbor;

        scheduler.scheduleAtFixedRate(choosePreferredNeighbors, 0, unchokingInterval, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(chooseOptimisticUnchokedNeighbor, 0, optimisticUnchokingInterval, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::shutdown));
    }

    public int getPortNumber() {
        return portNumber;
    }

    //read config file helper
    private void readCFG() throws FileNotFoundException {
        ArrayList <String> cfgVars = new ArrayList<>();
        try {
            File cfg = new File("Common.cfg");
            Scanner scanner = new Scanner(cfg);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\s+");

                if (parts.length == 2) {
                    cfgVars.add(parts[1]);
                    //System.out.println(parts[1]);
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
        this.pieceCount = (int) Math.ceil((double) fileSize / (double) pieceSize);
    }
    //writes to log

    //initializes bitfields
    public void initBitfield(){
        boolean[] bfArr = new boolean[pieceCount];
        for (int i = 0; i < pieceCount; i++) {
            bfArr[i] = hasFile;
        }
        this.bitfield = bfArr;
    }

    //connect to peer
    public void establishConnection(String peerID, String hostName, int port) throws IOException, InterruptedException {

        // Opens new socket to the specified peer
        Client client = new Client(hostName, port);
        PeerData newPeer = new PeerData(client);
        Thread thread = new Thread(client);
        thread.start();

        // Registers peer in HM and initiates handshake
        peerHM.put(peerID, newPeer);


        Logs log = new Logs();
        //ID is peer1 and peerID is peer 2.
        log.TCPLog(ID, peerID);

        // Keeps retrying until message is actually sent
        // ** Implement a timeout function for safety **
        while(!client.sendMessage(handshakeMsg()));
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
    private void determinePreferredNeighbors() {
        if (interestedPeers.size() <= numberOfPreferredNeighbors){
            preferredNeighbors = interestedPeers;
            return;
        }

        HashMap<String, Double> downloadRatesHM = new HashMap<>();

        // Calculate download rates for each interested peer
        for (String peer : interestedPeers) {
            double downloadRate = peerHM.get(peer).calculateDownloadRate();
            downloadRatesHM.put(peer, downloadRate);
        }

        // Select the top k peers based on download rate
        ArrayList<String> preferredNeighborsArr = new ArrayList<>();
        for (int i = 0; i < numberOfPreferredNeighbors; i++) {
            String maxID = null;
            double maxRate = Double.MIN_VALUE;
            for (Map.Entry<String, Double> entry : downloadRatesHM.entrySet()) {
                if (entry.getValue() > maxRate) {
                    maxID = entry.getKey();
                    maxRate = entry.getValue();
                }
            }
            preferredNeighborsArr.add(maxID);
            downloadRatesHM.remove(maxRate);
        }

        // Logging the preferred neighbors list
        Logs log = new Logs();
        log.changeOfPreferredNeighborsLog(ID, preferredNeighborsArr);
        this.preferredNeighbors = preferredNeighborsArr;
    }

    public boolean determineInterest(boolean[] peerBitfield) {
        for (int i = 0; i < pieceCount; i++) {
            if (!bitfield[i] && peerBitfield[i]) return true;
        } return false;
    }

    // Selects random index of a piece that the peer needs from another peer
    private int selectPiece(Boolean[] peerBitField, String peer2ID) {
        // Populates an array with all indices of valid pieces
        Logs log = new Logs();
        ArrayList<Integer> availableIndexes = new ArrayList<Integer>();
        for (int i = 0; i < pieceCount; i++) {
            if (peerBitField[i] && !bitfield[i]) {
                availableIndexes.add(i);
            }
        }

        // Returns random index from available pieces
        if (!availableIndexes.isEmpty()) {
            Random rand = new Random();
            Integer pieceInd = availableIndexes.get(rand.nextInt(availableIndexes.size()));
            //Download Log
            log.downloadingLog(ID, peer2ID, pieceInd, pieceSize);

            return pieceInd;
        } else{
            return -1;
        }
    }

    // Checks if peer has all parts of a file
    private boolean hasCompleteFile() {
        for (int i = 0; i < bitfield.length; i++) {
            if (!bitfield[i]) return false;
        }
        Logs log = new Logs();
        log.completedDownloadLog(ID);

        return true;
    }

    // Returns raw handshake message for current peer
    public byte[] handshakeMsg() {
        byte[] byteArray = new byte[32];
        String initialString = "P2PFILESHARINGPROJ";
        System.arraycopy(initialString.getBytes(), 0, byteArray, 0, Math.min(initialString.length(), 18));
        for (int i = 18; i < 28; i++) { byteArray[i] = 0; }
        String currPeerID = this.ID;
        System.arraycopy(currPeerID.getBytes(), 0, byteArray, 28, Math.min(currPeerID.length(), 4));
        return byteArray;
    }

    //need a way to call this every interval and to determine if neighbor is not already unchoked
    private void changeOptimisticNeighbor() {
        ArrayList<String> potentialOptimisticNeighbors = new ArrayList<>(interestedPeers);
        potentialOptimisticNeighbors.removeAll(preferredNeighbors);

        if (!potentialOptimisticNeighbors.isEmpty()) {
            Random rand = new Random();
            int randomIndex = rand.nextInt(potentialOptimisticNeighbors.size());

            String newOptimisticNeighbor = potentialOptimisticNeighbors.get(randomIndex);

            // Log the change of optimistic unchoked neighbor
            Logs log = new Logs();
            log.changeOfOptimisticallyUnchokedNeighborLog(ID, newOptimisticNeighbor);

            this.optimisticNeighbor = newOptimisticNeighbor;
        }
    }

    public boolean getHasFile() { return hasFile; }
}
