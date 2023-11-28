//package p2pFileSharing;
import java.io.*;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Scanner;
import java.security.KeyPair;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.config.keys.loader.KeyPairResourceLoader;
import org.apache.sshd.common.util.security.SecurityUtils;

public class StartRemotePeers {

    private static final String scriptPrefix = "java p2pFileSharing/peerProcess ";

    public static class PeerInfo {

        private String peerID;
        private String hostName;

        public PeerInfo(String peerID, String hostName) {
            super();
            this.peerID = peerID;
            this.hostName = hostName;
        }

        public String getPeerID() {
            return peerID;
        }

        public void setPeerID(String peerID) {
            this.peerID = peerID;
        }

        public String getHostName() {
            return hostName;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

    }

    public static void main(String[] args) throws IOException {

        ArrayList<PeerInfo> peerList = new ArrayList<>();
        File sshConfig = new File("remoteLogin.cfg");
        Scanner scanner = new Scanner(sshConfig);
        ArrayList<String> sshConfigLines = new ArrayList<>();
        while (scanner.hasNextLine()) {
            sshConfigLines.add(scanner.nextLine());
        }
        String ciseUser = sshConfigLines.get(0); // change with your CISE username
        /**
         * Make sure the below peer hostnames and peerIDs match those in
         * PeerInfo.cfg in the remote CISE machines. Also make sure that the
         * peers which have the file initially have it under the 'peer_[peerID]'
         * folder.
         */

        peerList.add(new PeerInfo("1", "lin114-06.cise.ufl.edu"));
        peerList.add(new PeerInfo("2", "lin114-08.cise.ufl.edu"));
        peerList.add(new PeerInfo("3", "lin114-09.cise.ufl.edu"));
        peerList.add(new PeerInfo("4", "lin114-04.cise.ufl.edu"));
        peerList.add(new PeerInfo("5", "lin114-05.cise.ufl.edu"));
        //start ssh client
        SshClient client = null;
        try {
            client = SshClient.setUpDefaultClient();
            client.start();
        } catch (Exception e) {
            System.out.println(e.toString());
            return;
        }
        //start session for each peer
        ArrayList<ClientSession> sessions = new ArrayList<>();
        for (PeerInfo remotePeer : peerList) {
            try (ClientSession session = client.connect(ciseUser, remotePeer.getHostName(), 22)
                    .verify(Duration.ofSeconds(10))
                    .getSession()) {
                session.auth().verify(Duration.ofSeconds(10));
                System.out.println("Session to peer# " + remotePeer.getPeerID() + " at " + remotePeer.getHostName());
            } catch (Exception e) {
                System.out.println("Failed to connect to peer#" + remotePeer.getPeerID());
                System.out.println(e.toString());
            }
        }
    }
}