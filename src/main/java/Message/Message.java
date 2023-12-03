package Message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import exceptions.InvalidMessageTypeException;
import exceptions.InvalidMessageByteStreamException;

public class Message {

    public enum Type {
        CHOKE(0x00),
        UNCHOKE(0x01),
        INTERESTED(0x02),
        NOTINTERESTED(0x03),
        HAVE(0x04),
        BITFIELD(0x05),
        REQUEST(0x06),
        PIECE(0x07);

        public int hex;

        Type(int hex) { this.hex = hex; }
    }

    private int messageLength;
    private int payloadLength;
    private Type type;
    private byte[] payload;

    public Message(byte[] msg_stream) throws Exception{

        // All messages are at least 5 bytes long: Length (4) + Type (1)
        if (msg_stream.length < 5) {
            throw new InvalidMessageByteStreamException("message byte stream is too short");
        }

        // Parse the first 4 bytes to get length
        byte[] lengthByteArr = new byte[4];
        System.arraycopy(msg_stream, 0, lengthByteArr, 0, 4);
        ByteBuffer buffer = ByteBuffer.wrap(lengthByteArr);
        buffer.order(ByteOrder.BIG_ENDIAN);
        int length = buffer.getInt();
        this.messageLength = length;
        this.payloadLength = length-1;

        // Ensure that indicated message length concurrs with actual byte stream length
        if ((this.messageLength + 4)!= msg_stream.length) {
            throw new InvalidMessageByteStreamException("message byte stream does not match the indicated message length");
        }

        // Type is the 5th byte in stream
        int typeInt = Byte.toUnsignedInt(msg_stream[4]);

        // Set message type
        if (typeInt == 0) this.type = Type.CHOKE;
        else if (typeInt == 1) this.type = Type.UNCHOKE;
        else if (typeInt == 2) this.type = Type.INTERESTED;
        else if (typeInt == 3) this.type = Type.NOTINTERESTED;
        else if (typeInt == 4) this.type = Type.HAVE;
        else if (typeInt == 5) this.type = Type.BITFIELD;
        else if (typeInt == 6) this.type = Type.REQUEST;
        else if (typeInt == 7) this.type = Type.PIECE;
        else {
            throw new InvalidMessageTypeException("invalid message type given");
        }
        
        // Parse message payload
        if (payloadLength < 1) {
            this.payload = null;
        } else {
            byte[] content = new byte[this.payloadLength];
            System.arraycopy(msg_stream, 5, content, 0, payloadLength);
            this.payload = content;
        }
    }

    // Msg for choke, unchoke, interested, and not interested
    public static byte[] buildMsg(Type type) {
        return buildPrefix(type, 1);
    }

    // Msg for have and request
    public static byte[] buildMsg(Type type, int index) {
        byte[] prefix = buildPrefix(type, 5);
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(index);
        byte[] payload = bb.array();
        byte[] retMsg = new byte[9];
        System.arraycopy(prefix, 0, retMsg, 0, 5);
        System.arraycopy(payload, 0, retMsg, 5, 4);
        return retMsg;
    }

    //Msg for piece
    public static byte[] buildMsg(byte[] data) {
        byte[] prefix = buildPrefix(Type.PIECE, 1 + data.length);
        byte[] retMsg = new byte[prefix.length + data.length];
        System.arraycopy(prefix, 0, retMsg, 0, prefix.length);
        System.arraycopy(data, 0, retMsg, prefix.length, data.length);

        return retMsg;
    }
    // Msg for bitfield
    public static byte[] buildMsg(Type type, boolean[] bitfield) {
        int length = bitfield.length;
        int hexArrLen = (int) Math.ceil((double)length/8.0);
        byte[] hexArray = new byte[hexArrLen];

        for (int i = 0; i < length; i += 8) {
            String binStr = "";
            for (int j = i; j < i+8; j++) {
                if(j > length - 1 || !bitfield[j]) {
                    binStr += "0";
                } else binStr += "1";
            }
            int intValue = Integer.parseInt(binStr, 2);
            hexArray[i/8] = (byte) intValue;
        }

        byte[] retMsg = new byte[5+hexArrLen];
        byte[] prefix = buildPrefix(type, hexArrLen + 1);
        System.arraycopy(prefix, 0, retMsg, 0, 5);
        System.arraycopy(hexArray, 0, retMsg, 5, hexArrLen);
        return retMsg;
    }

    private static byte[] buildPrefix(Type type, int length) {
        ByteBuffer bb = ByteBuffer.allocate(5);
        bb.putInt(length);
        bb.put((byte) type.hex);
        return bb.array();
    }

    // Extracts bitfield from BITFIELD message. (Only use on bitfield messages)
    public static boolean[] getBFFromMsg(Message bfMsg, int pieceCount) {
        if (bfMsg.type != Type.BITFIELD) return null;
        boolean[] retBF = new boolean[pieceCount];
        int index = 0;
        for (int i = 0; i < bfMsg.payloadLength; i++) {
            String binStr = Integer.toBinaryString((bfMsg.payload[i] & 0xFF) + 0x100).substring(1);
            for(int j = 0; j < binStr.length(); j++) {
                if (index > pieceCount-1) return retBF;
                retBF[index] = (binStr.charAt(j) == '1');
                index++;
            }
        }
        return retBF;
    }

    public int getMessageLength() {
        return this.messageLength;
    }

    public int getPayloadLength() {
        return this.payloadLength;
    }

    public Type getType() {
        return this.type;
    }
    
    public byte[] getPayload() {
        return this.payload;
    }

    // Main method for testing
    public static void main(String[] args) throws Exception {
        // Message.Message Length = 6 ; Type (1 Byte) + Payload (5 Bytes)
        // Message.Message Type = 4 ; 0x04 -> HAVE
        // Message.Message Payload = ASCII values for 'H', 'E', 'L', 'L', '0'
        byte[] byteArr = {0x00, 0x00, 0x00, 0x06, 0x04, 0x48, 0x45, 0x4C, 0x4C, 0x4F};

        Message myMessage = new Message(byteArr);
        System.out.println("Message.Message Length:   " + myMessage.messageLength);
        System.out.println("Payload Length:   " + myMessage.payloadLength);
        System.out.println("Message.Message Type:     " + myMessage.type);
  
        String payload = "";
        for (byte i : myMessage.payload){
            payload += (char) i;
        }
        System.out.println("Message.Message Payload:  " + payload);

        // Test bitfield message builder
        boolean[] bitfield = {
                true,true,true,true,true,true,true,true,
                false,false,false,true,false,false,false,false
        };
        byte[] message = buildMsg(Type.BITFIELD, bitfield);
        for(int i = 0; i < message.length; i++) {
            String hexValue = String.format("%02X", message[i]);
            System.out.print(hexValue + " ");
        }
        System.out.println("\n");

        //Test getBFFromMsg
        int pieceCount = 23;
        byte[] bfMsgArr = {0x00, 0x00, 0x00, 0x04, 0x05, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        Message bfMsg = new Message(bfMsgArr);
        boolean[] tempBF = new boolean[pieceCount];
        tempBF = getBFFromMsg(bfMsg, pieceCount);
        for(int i = 0; i < tempBF.length; i++) {
            if(tempBF[i]) System.out.print("1");
            else System.out.print("0");
        }
        System.out.println("\n");
        System.out.println("Size of bitfield: " + tempBF.length);
    }
}