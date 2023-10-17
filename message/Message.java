package message;
import java.nio.ByteBuffer;

public interface Message {
    enum Type {
        CHOKE,
        UNCHOKE,
        INTERESTED,
        NOTINTERESTED,
        HAVE,
        BITFIELD,
        REQUEST,
        PIECE
    }
    Type getMessageType();
    ByteBuffer getPayload();
}