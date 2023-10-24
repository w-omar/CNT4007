package src;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import src.com.server.Server;
import src.com.peer.Peer;

public class peerProcess {
    //java peerProcess <peer ID> <port> <has file>
    public static void main(String[] args) throws Exception {
        System.out.println("Running Main from peerProcess.java");
        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: peerProcess <peer ID> <port> <has file>");
        }
        Peer peer = new Peer(args[0], Integer.parseInt(args[1]), Boolean.parseBoolean(args[2]));
    }

    private void log(String msg) {
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
    private void sendMessage(String peerID/*, Message msg*/) {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }
    
    private void listenForMessages() {
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }
}

class ClientThread implements Runnable {
    public void run() {

    }
}