package src.message;
import java.nio.ByteBuffer;

class InterestedMessage implements Message {
    @Override
    public Message.Type getMessageType() {
        return Message.Type.INTERESTED;
    }

    @Override
    public ByteBuffer getPayload() {
        // Choke message typically has an empty payload
        return ByteBuffer.allocate(0);
    }
}