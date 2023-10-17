package src.message;
import java.nio.ByteBuffer;

class PieceMessage implements Message {
    @Override
    public Message.Type getMessageType() {
        return Message.Type.PIECE;
    }

    @Override
    public ByteBuffer getPayload() {
        // Choke message typically has an empty payload
        return ByteBuffer.allocate(0);
    }
}