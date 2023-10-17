import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logs {
    private String peerID;
    private String peer2ID;
    private LocalDateTime currentDateTime;

    public Logs(int ID, int ID2) {
        peerID = Integer.toString(ID);
        peer2ID = Integer.toString(ID2);
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

    private void writeToLogFile(String message) {
        String fileName = "log_peer_" + peerID + ".log";
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(message + "\n");
            System.out.println("Data has been written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void TCPLog() {
        String output = getFormattedTime() + ": Peer " + peerID + " makes a connection to Peer " + peer2ID;
        writeToLogFile(output);
    }

    public void changeOfPreferredNeighborsLog() {
        String output = getFormattedTime() + ": Peer " + peerID + " has the preferred neighbors"; // + list of neighbors
        writeToLogFile(output);
    }

    public void changeOfOptimisticallyUnchokedNeighborLog(int neighborID) {
        String output = getFormattedTime() + ": Peer " + peerID + " has the optimistically unchoked neighbor " + neighborID;
        writeToLogFile(output);
    }

    public void chokingLog() {
        String output = getFormattedTime() + ": Peer " + peerID + " is choked by " + peer2ID;
        writeToLogFile(output);
    }

    public void unchokingLog() {
        String output = getFormattedTime() + ": Peer " + peerID + " is unchoked by " + peer2ID;
        writeToLogFile(output);
    }

    public void haveLog(int pieceIndex) {
        String output = getFormattedTime() + ": Peer " + peerID + " received the 'have' message from " + peer2ID + " for the piece " + pieceIndex;
        writeToLogFile(output);
    }

    public void interestedLog() {
        String output = getFormattedTime() + ": Peer " + peerID + " received the 'interested' message from " + peer2ID;
        writeToLogFile(output);
    }

    public void notInterestedLog() {
        String output = getFormattedTime() + ": Peer " + peerID + " received the 'not interested' message from " + peer2ID;
        writeToLogFile(output);
    }

    public void downloadingLog(int pieceIndex, int numPieces) {
        String output = getFormattedTime() + ": Peer " + peerID + " has downloaded the piece " + pieceIndex + " from " + peer2ID + ". Now the number of pieces it has is " + numPieces;
        writeToLogFile(output);
    }

    public void completedDownloadLog() {
        String output = getFormattedTime() + ": Peer " + peerID + " has downloaded the complete file.";
        writeToLogFile(output);
    }
}
