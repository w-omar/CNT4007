package com.peer;

import Logs.Logs;
import Message.Message;
import com.client.Client;
import com.server.Server;
import com.peer.PeerData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.lang.Math;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
    private String filePath;
    private String fileName;
    private final RandomAccessFile theFile;
    //dictated by peerProcess
    public final String ID;
    private final int portNumber;
    private boolean hasFile;
    public boolean[] bitfield;

    // Tracks the peers who are connected and their respective client sockets
    public HashMap<String, PeerData> peerHM = new HashMap<>();

    public Peer(String peerId, int port, boolean hasFile) throws FileNotFoundException, IOException {
        this.ID = peerId;
        this.portNumber = port;
        readCFG();
        this.hasFile = hasFile;
        this.theFile = openP2PFile();
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

    //opens file to read/write to
    private RandomAccessFile openP2PFile() throws IOException {
        Path fullPath = Paths.get(filePath + "/" + fileName);
        if (!hasFile) {
            Path parentDir = Paths.get(filePath);
            Files.createDirectories(parentDir);
            Files.createFile(fullPath);
        }
        RandomAccessFile theFile = null;
        theFile = new RandomAccessFile(fullPath.toFile(), "rw");
        theFile.setLength((long) pieceCount * pieceSize);
        return theFile;
    }
    public int getPortNumber() {
        return portNumber;
    }
    public int getPieceSize() {
        return pieceSize;
    }
    //returns bitfield[index] or false if index is OOB
    public boolean hasPiece(int index){
        if (index < 0 || index >= pieceCount)
            return false;
        return bitfield[index];
    }

    //get peerID's for all unchoked neighbors
    public ArrayList<String> getUnchokedNeighborIDs(){
        ArrayList<String> unchokedNeighborIDs = new ArrayList<>(preferredNeighbors);
        unchokedNeighborIDs.add(optimisticNeighbor);
        return unchokedNeighborIDs;
    }
    //gets bytes from FD corresponding to passed index
    public byte[] getPiece(int index) throws IOException {
        if ( index < 0 || index >= pieceCount) {
            throw new IndexOutOfBoundsException("Requested piece index out of bounds");
        }
        int byteOffset = index * pieceSize;
        byte[] piece = new byte[pieceSize];
        this.theFile.seek(byteOffset);
        this.theFile.read(piece);
        this.theFile.seek(0);
        return piece;
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
            System.out.println("Could not read Common.cfg");
            e.printStackTrace();
        }
        this.numberOfPreferredNeighbors = Integer.parseInt(cfgVars.get(0));
        this.unchokingInterval = Integer.parseInt(cfgVars.get(1));
        this.optimisticUnchokingInterval = Integer.parseInt(cfgVars.get(2));
        this.filePath = "peer_" + this.ID;
        this.fileName = cfgVars.get(3);
        this.fileSize = Integer.parseInt(cfgVars.get(4));
        this.pieceSize = Integer.parseInt(cfgVars.get(5));
        this.pieceCount = (int) Math.ceil((double) fileSize / (double) pieceSize);
    }

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
        PeerData newPeer = new PeerData(client, peerID);
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

    //writes received piece to theFile
    public void writePiece(int index, byte[] piece) throws IOException{
        if (index < 0 || index >= pieceCount)
            throw new IndexOutOfBoundsException("Tried to write a piece to an OOB index");
        //write piece
        theFile.seek(index * pieceSize);
        theFile.write(piece);
        theFile.seek(0);
        //update bitfield
        bitfield[index] = true;
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
        // If the number of interested peers is less than number of preferred peers
        if (interestedPeers.size() <= numberOfPreferredNeighbors){
            preferredNeighbors = interestedPeers;
        }
        // If current peer has the file, randomly selected preferred peers
        else if (hasCompleteFile()) {
            ArrayList<String> tempPeers = interestedPeers;
            ArrayList<String> selectedPeers = new ArrayList<>();
            for (int i = 0; i < numberOfPreferredNeighbors; i++) {
                Random rand = new Random();
                int randIdx = rand.nextInt(tempPeers.size());
                selectedPeers.add(tempPeers.get(randIdx));
                tempPeers.remove(randIdx);
            }
            preferredNeighbors = selectedPeers;
        }
        // All other cases
        else {
            // Calculate download rates for each interested peer
            HashMap<String, Double> downloadRatesHM = new HashMap<>();
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
                    // If same value, randomly break tie
                    else if (entry.getValue() == maxRate) {
                        Random rand = new Random();
                        if (rand.nextInt(2) == 1) {
                            maxID = entry.getKey();
                            maxRate = entry.getValue();
                        }
                    }
                }
                preferredNeighborsArr.add(maxID);
                downloadRatesHM.remove(maxRate);
            }

            // Logging the preferred neighbors list
            Logs log = new Logs();
            log.changeOfPreferredNeighborsLog(ID, preferredNeighborsArr);
            preferredNeighbors = preferredNeighborsArr;
        }

        // Unchoke those who are now preferred (if they need to be unchoked)
        for (String peerID : preferredNeighbors) {
            if (peerHM.get(peerID).isChoked) {
                peerHM.get(peerID).isChoked = false;
                byte[] unchokeMsg = Message.buildMsg(Message.Type.UNCHOKE);
                peerHM.get(peerID).cliSock.sendMessage(unchokeMsg);
            }
        }

        // Choke those who are not preferred (if they need to be choked)
        ArrayList<String> chokedPeers = interestedPeers;
        chokedPeers.removeAll(preferredNeighbors);
        for (String peerID : chokedPeers) {
            if (!peerHM.get(peerID).isChoked) {
                peerHM.get(peerID).isChoked = true;
                byte[] chokeMsg = Message.buildMsg(Message.Type.CHOKE);
                peerHM.get(peerID).cliSock.sendMessage(chokeMsg);
            }
        }
    }

    public boolean determineInterest(boolean[] peerBitfield) {
        for (int i = 0; i < pieceCount; i++) {
            if (!bitfield[i] && peerBitfield[i]) return true;
        } return false;
    }

    // Selects random index of a piece that the peer needs from another peer
    public int selectPiece(boolean[] peerBitField) {
        // Populates an array with all indices of valid pieces
        Logs log = new Logs();
        ArrayList<Integer> availableIndexes = new ArrayList<>();
        for (int i = 0; i < pieceCount; i++) {
            if (peerBitField[i] && !bitfield[i]) {
                availableIndexes.add(i);
            }
        }

        // Returns random index from available pieces
        if (!availableIndexes.isEmpty()) {
            Random rand = new Random();
            return availableIndexes.get(rand.nextInt(availableIndexes.size()));
        } else{
            return -1;
        }
    }

    // Checks if peer has all parts of a file
    public boolean hasCompleteFile() {
        if (hasFile) return true;
        for (boolean b : bitfield) {
            if (!b) return false;
        }
        hasFile = true;
        Logs log = new Logs();
        log.completedDownloadLog(ID);
        return true;
    }

    // Returns raw handshake message for current peer
    public byte[] handshakeMsg() {
        byte[] byteArray = new byte[32];
        String initialString = "P2PFILESHARINGPROJ";
        System.arraycopy(initialString.getBytes(), 0, byteArray, 0, initialString.length());
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

            // Choke the old optimistic neighbor
            if (!peerHM.get(optimisticNeighbor).isChoked) {
                peerHM.get(optimisticNeighbor).isChoked = true;
                byte[] chokeMsg = Message.buildMsg(Message.Type.CHOKE);
                peerHM.get(optimisticNeighbor).cliSock.sendMessage(chokeMsg);
            }

            optimisticNeighbor = newOptimisticNeighbor;
            // Unchoke the new optimistic neighbor
            if(peerHM.get(optimisticNeighbor).isChoked) {
                peerHM.get(optimisticNeighbor).isChoked = false;
                byte[] unchokeMsg = Message.buildMsg(Message.Type.UNCHOKE);
                peerHM.get(optimisticNeighbor).cliSock.sendMessage(unchokeMsg);
            }
        }
    }

    public boolean getHasFile() { return hasFile; }
}
