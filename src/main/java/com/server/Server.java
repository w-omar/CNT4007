package com.server;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import Message.Message;
import com.peer.Peer;
import Logs.Logs;

public class Server implements Runnable{

	private int port;
	private static Peer currPeer;
	private static HashMap<String, String> idHM = new HashMap<>();

	public Server(Peer currPeer, int port) {
		System.out.println("Making new server for this peer");
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
				System.out.println("Client " + clientNum + " is connected!");
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
							System.out.println("Received handshake from: " + peerID);

							// Establishes client socket to peer
							if (!currPeer.peerHM.containsKey(peerID)) {
								idHM.put(uniqueIdent, peerID);
								String[] peerInfo = getPeerInfo(peerID);
								currPeer.establishConnection(peerID, peerInfo[0], Integer.parseInt(peerInfo[1]));
							} else {
								System.out.println("Already connected to this peer");
							}
						}
						// All other regular messages parsed here
						else {
							// The peer ID for the incoming message
							String peerID = idHM.get(uniqueIdent);
							message = new Message(byteArray);

							Logs log = new Logs();

							//TODO: Response Logic
							switch (message.getType()) {
								case CHOKE:
									break;
								case UNCHOKE:
									break;
								case INTERESTED:
									break;
								case NOTINTERESTED:
									break;
								case HAVE:
									break;
								case BITFIELD:
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