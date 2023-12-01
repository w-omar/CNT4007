package com.client;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import Message.Message;

public class Client implements Runnable {
    Socket requestSocket;           //socket connect to the server
    ObjectOutputStream out;         //stream write to the socket
    ObjectInputStream in;          //stream read from the socket
    String message;                //message send to the server
    String MESSAGE;                //capitalized message read from the server

    String servHostName;
    int servPort;

    public Client(String servHostName, int servPort) {
        this.servHostName = servHostName;
        this.servPort = servPort;
    }

    public void run()
    {
        try{
            //create a socket to connect to the server
            requestSocket = new Socket(servHostName, servPort);
            //initialize inputStream and outputStream
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());

            //get Input from standard input
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            // Keeps thread from closing
            while(true);
        } catch (ConnectException e) {
            System.err.println("Connection refused. You need to initiate a server first.");
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Close connections
            try{
                in.close();
                out.close();
                requestSocket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }
    // Sends a message via client socket output stream
    public boolean sendMessage(byte[] msg) {
        try{
            // Message can't send if out stream isn't initialized
            if (this.out == null) return false;
            // Stream write the message
            out.writeObject(msg);
            out.flush();
            // (REMOVE LATER) prints message sent
            String hexMsg = "";
            for(int i = 0; i < msg.length; i++) {
                hexMsg += String.format("%02X", msg[i]) + " ";
            }
            System.out.println("Sent this message: " + hexMsg);

        } catch(IOException ioException) {
            ioException.printStackTrace();
        }
        return true;
    }

    //main method
    public static void main(String args[]) throws FileNotFoundException {
        Client client = new Client("localhost", 8000);
        client.run();
    }

}

