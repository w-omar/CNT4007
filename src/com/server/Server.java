package src.com.server;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import src.Message;

public class Server implements Runnable{
	
	private int port;

	public Server(int port) {
		this.port = port;
	}

	public void run() {
		ServerSocket listener = null;
		//initialize socket server
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
						//receive the message sent from the client
						message = new Message((byte[]) in.readObject());
						//TODO: Log message received
						//TODO: response logic
					}
				}
				catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
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
	}
}