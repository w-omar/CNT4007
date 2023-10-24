import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HexFormat;
import exceptions.InvalidMessageTypeException;
import exceptions.InvalidMessageByteStreamException;

public class Message {

    public enum Type {
        CHOKE,
        UNCHOKE,
        INTERESTED,
        NOTINTERESTED,
        HAVE,
        BITFIELD,
        REQUEST,
        PIECE
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
        // Message Length = 6 ; Type (1 Byte) + Payload (5 Bytes)
        // Message Type = 4 ; 0x04 -> HAVE
        // Message Payload = ASCII values for 'H', 'E', 'L', 'L', '0'
        byte[] byteArr = {0x00, 0x00, 0x00, 0x06, 0x04, 0x48, 0x45, 0x4C, 0x4C, 0x4F};

        Message myMessage = new Message(byteArr);
        System.out.println("Message Length:   " + myMessage.messageLength);
        System.out.println("Payload Length:   " + myMessage.payloadLength);
        System.out.println("Message Type:     " + myMessage.type);
  
        String payload = "";
        for (byte i : myMessage.payload){
            payload += (char) i;
        }

        System.out.println("Message Payload:  " + payload);
    }
}