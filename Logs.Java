import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logs {
    public String peerID;
    public String peer2ID;
    

    public ID(int ID, int ID2){
        peerID = Integer.toString(ID);
        peer2ID = Integer.toString(ID2);
    }

    LocalDateTime currentDateTime = LocalDateTime.now();

        // Define a custom date and time format
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format and print the current date and time
    String time = currentDateTime.format(formatter);

    String fileName = "log_peer_" + peerID + ".log";
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

   public void TCPLog{
    String output = time + ": Peer " + peerID + " makes  a connection to Peer " + peer2ID;
    //Write to file
   }

   public void changeOfPreferredNeighborsLog{
    String output = time + ": Peer " + peerID + " has the preferred neighors"; // + list of neighbors
    //Write to file
   }

   public void changeOfOptimisticallyUnchokedNeighborLog{
    String output = time + ": Peer " + peerID + " has the optimistically unchoked neighbor "; // + neighborID
    //Write to file
   }

   public void chokingLog{
    String output = time + ": Peer " + peerID + "is choked by " + peer2ID;
    //write to file
   }

   public void UnchokingLog{
    String output = time + ": Peer " + peerID + "is unchoked by " + peer2ID;
    //write to file
   }

   public void haveLog{
    String output = time + ": Peer " + peerID + " received the \'have\' message from " + peer2ID + "for the piece"; // + piece index
    //Write to file
   }

   public void interestedLog{
    String output = time + ": Peer " + peerID + " received the \'interested\' message from " + peer2ID;
    //Write to file

   }

   public void interestedLog{
    String output = time + ": Peer " + peerID + " received the \'not interested\' message from " + peer2ID;
    //Write to file
   }

   public void downloadingLog{
    String output = time + ": Peer " + peerID + "has downloaded the piece " + /*piece index + */ " from " + peer2ID + ". Now the number of pieces it has is "; // + number of pieces 
    //Write to file 
   }

   public void completedDownloadLog{
        String output = time + ": Peer " + peerID + " has downloaded the complete file.";
        //Write to file
   }

}
