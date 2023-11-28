package src.com.peer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Math;


import src.Logs;
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
    private int pieceCount;
    private String fileName;

    //dictated by peerProcess
    private String ID;
    private int portNumber;
    private boolean hasFile;

    //Each peer should write its log into the log file ‘log_peer_[peerID].log’ at the working directory
    private String logFileName;

    private ArrayList<Boolean> bitField;

    private Map<String, Integer> piecesDownloaded;


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
        this.pieceCount = (int) Math.ceil(fileSize / pieceSize);
    }
    //writes to log

    //initializes bitfields
    public void initBitfield(){
        bitField = new ArrayList<Boolean>();
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

        Logs log = new Logs();
        //ID is peer1 and peerID is peer 2.
        log.TCPLog(ID, peerID);
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
        //Will probably have to move this log block to where preferredneighbors are changed
        Logs log = new Logs();
        ArrayList<String> IDList = new ArrayList<>();
        for(Peer peer: interestedPeers){
            IDList.add(peer.ID);
        }
        log.changeOfPreferredNeighborsLog(ID, IDList);
        //End of log block
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");

    }

    // Selects random index of a piece that the peer needs from another peer
    private int selectPiece(ArrayList<Boolean> peerBitField, String peer2ID) {
        // Populates an array with all indices of valid pieces
        Logs log = new Logs();
        ArrayList<Integer> availableIndexes = new ArrayList<Integer>();
        for (int i = 0; i < pieceCount; i++) {
            if (peerBitField.get(i) && !bitField.get(i)) {
                availableIndexes.add(i);
            }
        }
        //start pieces downloaded counter
        if (!piecesDownloaded.containsKey(ID)){
            piecesDownloaded.put(ID, 1);
        }
        else{
            Integer temp = piecesDownloaded.get(ID);
            piecesDownloaded.put(ID, temp + 1);
        }

        // Returns random index from available pieces
        if (!availableIndexes.isEmpty()) {
            Random rand = new Random();
            Integer pieceInd = availableIndexes.get(rand.nextInt(availableIndexes.size()));
            //Download Log
            log.downloadingLog(ID, peer2ID, pieceInd, pieceSize);

            return pieceInd;
        } else{
            //reset piece counter to 0 for next download
            piecesDownloaded.put(ID, 0);
            return -1;
        }
    }

    // Checks if peer has all parts of a file
    private boolean hasCompleteFile() {
        for (int i = 0; i < bitField.size(); i++) {
            if (!bitField.get(i)) return false;
        }
        Logs log = new Logs();
        log.completedDownloadLog(ID);

        return true;

    }
}