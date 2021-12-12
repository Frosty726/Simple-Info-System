public class TransmittingCanal {
    private char[] data;
    private HafTree codesTree;
    private int codedLength;

    public TransmittingCanal() {
        data        = null;
        codesTree   = null;
        codedLength = 0;
    }

    public void receive(char[] data, HafTree codesTree, int codedLength) {
        this.data        = data;
        this.codesTree   = codesTree;
        this.codedLength = codedLength;
    }

    public char[] transmit() {
        return data;
    }

    public HafTree transmitCodes() {
        return codesTree;
    }

    public int transmitCodedLength() {
        return codedLength;
    }
}
