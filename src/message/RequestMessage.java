package src.message;
import java.nio.ByteBuffer;

class RequestMessage implements Message {
    @Override
    public Message.Type getMessageType() {
        return Message.Type.REQUEST;
    }

    @Override
    public ByteBuffer getPayload() {
        // Choke message typically has an empty payload
        return ByteBuffer.allocate(0);
    }
}