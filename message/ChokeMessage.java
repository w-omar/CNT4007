package message;
import java.nio.ByteBuffer;

class ChokeMessage implements Message {
    @Override
    public Message.Type getMessageType() {
        return Message.Type.CHOKE;
    }

    @Override
    public ByteBuffer getPayload() {
        // Choke message typically has an empty payload
        return ByteBuffer.allocate(0);
    }
}