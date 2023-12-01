package com.server;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import Message.Message;
import Message.Message.Type;
import com.peer.Peer;
import Logs.Logs;
import jdk.swing.interop.SwingInterOpUtils;

public class Server implements Runnable{

	private int port;
	private static Peer currPeer;
	private static HashMap<String, String> idHM = new HashMap<>();

	public Server(Peer currPeer, int port) {
		this.currPeer = currPeer;
		this.port = port;
	}

	public void run() {
		ServerSocket listener = null;
		// Initializes listening (server) socket
		try {
			listener = new ServerSocket(port);
			System.out.println("The server is running at port: " + port);
		} catch (Exception e) {
			System.out.println("Could not start socket server: " + e.toString());
		}
		int clientNum = 1;
		try {
			while (true) {
				new Handler(listener.accept(), clientNum).start();
				clientNum++;
			}
		} catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
			try {
				listener.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * A handler thread class.  Handlers are spawned from the listening
	 * loop and are responsible for dealing with a single client's requests.
	 */
	private static class Handler extends Thread {
		private Message message;    //message received from the client
		private String MESSAGE;    //uppercase message send to the client
		private Socket connection;
		private ObjectInputStream in;	//stream read from the socket
		private ObjectOutputStream out;    //stream write to the socket
		private int no;		//The index number of the client

		public Handler(Socket connection, int no) {
			this.connection = connection;
			this.no = no;
		}

		public void run() {
			try{
				//initialize Input and Output streams
				out = new ObjectOutputStream(connection.getOutputStream());
				out.flush();
				in = new ObjectInputStream(connection.getInputStream());
				try{
					while(true)
					{
						// Raw message received from incoming client socket
						byte[] byteArray = (byte[]) in.readObject();

						String uniqueIdent =
								connection.getInetAddress().getCanonicalHostName() + ":" +
								connection.getPort();

						// Receives handshake
						if (compareBytesToString(byteArray, "P2PFILESHARINGPROJ", 18)) {
							String peerID = new String(byteArray, byteArray.length - 4, 4);
							System.out.println("Received HANDSHAKE from " + peerID);

							// Establishes client socket to peer
							if (!currPeer.peerHM.containsKey(peerID)) {
								idHM.put(uniqueIdent, peerID);
								String[] peerInfo = getPeerInfo(peerID);
								currPeer.establishConnection(peerID, peerInfo[0], Integer.parseInt(peerInfo[1]));
							} else {
								// The return handshake has been received, can now send bitfield
								idHM.put(uniqueIdent, peerID);
								currPeer.peerHM.get(peerID).handShook = true;
								byte[] bitfieldMsg = Message.buildMsg(Type.BITFIELD, currPeer.bitfield);
								currPeer.peerHM.get(peerID).cliSock.sendMessage(bitfieldMsg);
							}
						}
						// All other regular messages parsed here
						else {
							// The peer ID for the incoming message
							String peerID = idHM.get(uniqueIdent);
							message = new Message(byteArray);
							System.out.println("Received " + message.getType() + " message from " + peerID);
							Logs log = new Logs();

							//TODO: Response Logic
							switch (message.getType()) {
								case CHOKE:
									break;
								case UNCHOKE:
									break;
								case INTERESTED:
									if (!currPeer.interestedPeers.contains(peerID)) {
										currPeer.interestedPeers.add(peerID);
									}
									break;
								case NOTINTERESTED:
									currPeer.interestedPeers.remove(peerID);
									break;
								case HAVE:
									// Update local copy of this peer's bitfield
									int pieceIdx = ByteBuffer.wrap(message.getPayload()).getInt();
									currPeer.peerHM.get(peerID).bitfield[pieceIdx] = true;

									// Determine if interested after receiving new bit
									if (currPeer.determineInterest(currPeer.peerHM.get(peerID).bitfield)) {
										// Only send interested msg if peer wasn't already interesting
										if (!currPeer.peerHM.get(peerID).interesting) {
											byte[] interestedMsg = Message.buildMsg(Type.INTERESTED);
											currPeer.peerHM.get(peerID).cliSock.sendMessage(interestedMsg);
											currPeer.peerHM.get(peerID).interesting = true;
										}
									} else {
										// Not interested anymore, send not interested msg
										byte[] notInterestedMsg = Message.buildMsg(Type.NOTINTERESTED);
										currPeer.peerHM.get(peerID).cliSock.sendMessage(notInterestedMsg);
										currPeer.peerHM.get(peerID).interesting = false;
									}
									break;
								case BITFIELD:
									// Hasn't sent own bitfield yet
									if (!currPeer.peerHM.get(peerID).handShook) {
										currPeer.peerHM.get(peerID).handShook = true;
										byte[] bitfieldMsg = Message.buildMsg(Type.BITFIELD, currPeer.bitfield);
										currPeer.peerHM.get(peerID).cliSock.sendMessage(bitfieldMsg);
									}
									// Record incoming bitfield
									boolean[] peerBF = Message.getBFFromMsg(message, currPeer.pieceCount);
									currPeer.peerHM.get(peerID).bitfield = peerBF;

									// Determines if interested in this new peer
									if (currPeer.determineInterest(peerBF)) {
										byte[] interestMsg = Message.buildMsg(Type.INTERESTED);
										currPeer.peerHM.get(peerID).interesting = true;
										currPeer.peerHM.get(peerID).cliSock.sendMessage(interestMsg);
									}
									break;
								case REQUEST:
									break;
								case PIECE:
									break;
							}
						}
						// (REMOVE LATER) Displays peers currently connected to
						System.out.println("Current Peer List");
						for(String peerID : currPeer.peerHM.keySet()) {
							System.out.println(peerID);
						}
					}
				} catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				} catch (EOFException eofException) {
					System.out.println("Disconnected with Client " + no);
				} catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
			catch(IOException ioException){
				System.out.println("Disconnect with Client " + no);
			}
			finally{
				//Close connections
				try{
					in.close();
					out.close();
					connection.close();
				}
				catch(IOException ioException){
					System.out.println("Disconnect with Client " + no);
				}
			}
		}

		// Compares raw byte streams to a string
		private static boolean compareBytesToString(byte[] byteArray, String targetString, int length) {
			if (byteArray.length < length) return false;
			String byteArraySubstring = new String(byteArray, 0, length);
			return byteArraySubstring.equals(targetString);
		}

		//send a message to the output stream
		public void sendMessage(String msg)
		{
			try{
				out.writeObject(msg);
				out.flush();
				System.out.println("Send message: " + msg + " to Client " + no);
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}

		// Extracts peer info from PeerInfo.cfg for a specific handshake
		public String[] getPeerInfo(String peerID) {
			String[] peerInfo = new String[3];
			try {
				File cfg = new File("PeerInfo.cfg");
				Scanner scanner = new Scanner(cfg);
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					String[] parts = line.split("\\s+");
					if (parts[0].equals(peerID)) {
						peerInfo[0] = parts[1];
						peerInfo[1] = parts[2];
						peerInfo[2] = parts[3];
						break;
					}
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return peerInfo;
		}
	}
}