import java.util.ArrayList;

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
    
    //Each peer should write its log into the log file ‘log_peer_[peerID].log’ at the working directory
    private String logFileName;

    private ArrayList<Boolean> bitField;

    //writes to log
    private void log(String msg) {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }

    //starts the socket server
    public void initialize(String commonCFG) {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }

    //creates TCP connections to peers, populate peersList
    private void peerConnect(String peerCFG) {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }

    /*
    * type              value
    * choke             0
    * unchoke           1
    * interested        2
    * not interested    3
    * have              4
    * bitfield          5
    * request           6
    * piece             7
    */
    private void sendMessage(String peerID, Message msg) {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }
    
    private void listenForMessages() {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
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