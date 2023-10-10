package src;
public abstract class Message {
    public static final int CHOKE = 0;
    public static final int UNCHOKE = 1;
    public static final int INTERESTED = 2;
    public static final int NOT_INTERESTED = 3;
    public static final int HAVE = 4;
    public static final int BITFIELD = 5;
    public static final int REQUEST = 6;
    public static final int PIECE = 7;

    private int type;

    public Message(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    // Inherited Functions Go Here
    public abstract void sampleInheritedFunc();
}

// Choke
public class ChokeMessage extends Message {
    public ChokeMessage() {
        super(CHOKE);
    }

    @Override
    public void sampleInheritedFunc() {
        System.out.println("Sample Inherited Function!");
    }
}

// Unchoke
public class UnchokeMessage extends Message {
    public UnchokeMessage() {
        super(UNCHOKE);
    }

    @Override
    public void sampleInheritedFunc() {
        System.out.println("Sample Inherited Function!");
    }
}

// Interested
public class InterestedMessage extends Message {
    public InterestedMessage() {
        super(INTERESTED);
    }

    @Override
    public void sampleInheritedFunc() {
        System.out.println("Sample Inherited Function!");
    }
}

// Not Interested
public class NotInterestedMessage extends Message {
    public NotInterestedMessage() {
        super(NOT_INTERESTED);
    }

    @Override
    public void sampleInheritedFunc() {
        System.out.println("Sample Inherited Function!");
    }
}

// Have
public class HaveMessage extends Message {
    private int pieceIndex;

    public HaveMessage(int pieceIndex) {
        super(HAVE);
        this.pieceIndex = pieceIndex;
    }

    @Override
    public void sampleInheritedFunc() {
        System.out.println("Sample Inherited Function!");
    }
}

// Bitfield
public class BitfieldMessage extends Message {
    private int pieceIndex;

    public BitfieldMessage(int pieceIndex) {
        super(BITFIELD);
        this.pieceIndex = pieceIndex;
    }

    @Override
    public void sampleInheritedFunc() {
        System.out.println("Sample Inherited Function!");
    }
}

// Request
public class RequestMessage extends Message {
    private int pieceIndex;

    public RequestMessage(int pieceIndex) {
        super(REQUEST);
        this.pieceIndex = pieceIndex;
    }

    @Override
    public void sampleInheritedFunc() {
        System.out.println("Sample Inherited Function!");
    }
}

// Piece
public class PieceMessage extends Message {
    private int pieceIndex;

    public PieceMessage(int pieceIndex) {
        super(PIECE);
        this.pieceIndex = pieceIndex;
    }

    @Override
    public void sampleInheritedFunc() {
        System.out.println("Sample Inherited Function!");
    }
}