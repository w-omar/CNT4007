package message;
import java.nio.ByteBuffer;

class BitfieldMessage implements Message {
    @Override
    public Message.Type getMessageType() {
        return Message.Type.BITFIELD;
    }

    @Override
    public ByteBuffer getPayload() {
        // Choke message typically has an empty payload
        return ByteBuffer.allocate(0);
    }
}