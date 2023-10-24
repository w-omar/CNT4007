package src;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import src.com.server.Server;
import src.com.peer.Peer;
import src.exceptions.InvalidPeerProcessArgumentsException;

public class peerProcess {
    //java peerProcess <peer ID> <port> <has file>
    public static void main(String[] args) throws Exception {
        System.out.println("Running Main from peerProcess.java");
        if (args.length != 3) {
            throw new InvalidPeerProcessArgumentsException("Usage: peerProcess <peer ID> <port> <has file>");
        }
        Peer peer = new Peer(args[0], Integer.parseInt(args[1]), Boolean.parseBoolean(args[2]));
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
    private void sendMessage(String peerID/*, Message msg*/) {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }
    
    private void listenForMessages() {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }
}

class ClientThread implements Runnable {
    public void run() {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }
}