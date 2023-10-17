package src.message;
import java.nio.ByteBuffer;

class UnhokeMessage implements Message {
    @Override
    public Message.Type getMessageType() {
        return Message.Type.UNCHOKE;
    }

    @Override
    public ByteBuffer getPayload() {
        // Choke message typically has an empty payload
        return ByteBuffer.allocate(0);
    }
}