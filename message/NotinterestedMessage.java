package message;
import java.nio.ByteBuffer;

class NotinterestedMessage implements Message {
    @Override
    public Message.Type getMessageType() {
        return Message.Type.NOTINTERESTED;
    }

    @Override
    public ByteBuffer getPayload() {
        // Choke message typically has an empty payload
        return ByteBuffer.allocate(0);
    }
}