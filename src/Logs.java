package src;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Logs {
    private LocalDateTime currentDateTime;

    public Logs() {
        currentDateTime = LocalDateTime.now();
    }

    private String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDateTime.format(formatter);
    }

        /*
    try {
        FileWriter writer = new FileWriter(fileName);

        // Write content to the file
        writer.write("Hello, World!\n");
        writer.write("This is a Java file write example.");

        // Close the file writer
        writer.close();

        System.out.println("Data has been written to " + fileName);
    } catch (IOException e) {
        e.printStackTrace();
    } 

    */

    private void writeToLogFile(String peerID, String message) {
        String fileName = "log_peer_" + peerID + ".log";
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(message + "\n");
            System.out.println("Data has been written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void TCPLog(String peerID, String peer2ID) {
        String output = getFormattedTime() + ": Peer " + peerID + " makes a connection to Peer " + peer2ID;
        writeToLogFile(peerID, output);
    }

    public void changeOfPreferredNeighborsLog(String peerID, List<String> neighborList) {
        StringBuilder outputBuilder = new StringBuilder();
        outputBuilder.append(getFormattedTime()).append(": Peer ").append(peerID).append(" has the preferred neighbors");

        for (int i = 0; i < neighborList.size(); i++) {
            outputBuilder.append(" ").append(neighborList.get(i));
            if (i == neighborList.size() - 1) {
                outputBuilder.append(".");
            } else {
                outputBuilder.append(",");
            }
        }

        writeToLogFile(peerID, outputBuilder.toString());
    }

    public void changeOfOptimisticallyUnchokedNeighborLog(String peerID, String peer2ID, int neighborID) {
        String output = getFormattedTime() + ": Peer " + peerID + " has the optimistically unchoked neighbor " + neighborID;
        writeToLogFile(peerID, output);
    }

    public void chokingLog(String peerID, String peer2ID) {
        String output = getFormattedTime() + ": Peer " + peerID + " is choked by " + peer2ID;
        writeToLogFile(peerID, output);
    }

    public void unchokingLog(String peerID, String peer2ID) {
        String output = getFormattedTime() + ": Peer " + peerID + " is unchoked by " + peer2ID;
        writeToLogFile(peerID, output);
    }

    public void haveLog(String peerID, String peer2ID, int pieceIndex) {
        String output = getFormattedTime() + ": Peer " + peerID + " received the 'have' message from " + peer2ID + " for the piece " + pieceIndex;
        writeToLogFile(peerID, output);
    }

    public void interestedLog(String peerID, String peer2ID) {
        String output = getFormattedTime() + ": Peer " + peerID + " received the 'interested' message from " + peer2ID;
        writeToLogFile(peerID, output);
    }

    public void notInterestedLog(String peerID, String peer2ID) {
        String output = getFormattedTime() + ": Peer " + peerID + " received the 'not interested' message from " + peer2ID;
        writeToLogFile(peerID, output);
    }

    public void downloadingLog(String peerID, String peer2ID, int pieceIndex, int numPieces) {
        String output = getFormattedTime() + ": Peer " + peerID + " has downloaded the piece " + pieceIndex + " from " + peer2ID + ". Now the number of pieces it has is " + numPieces;
        writeToLogFile(peerID, output);
    }

    public void completedDownloadLog(String peerID) {
        String output = getFormattedTime() + ": Peer " + peerID + " has downloaded the complete file.";
        writeToLogFile(peerID, output);
    }
}
