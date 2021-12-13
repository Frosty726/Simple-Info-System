import java.util.Random;

public class TransmittingCanal {
    private char[] data;
    private HafTree codesTree;
    private DoubleHafTree doubleCodesTree;
    private int codedLength;

    /** Chance of noise to appear **/
    private double p;

    public TransmittingCanal() {
        data            = null;
        codesTree       = null;
        doubleCodesTree = null;
        codedLength     = 0;
        p               = 0;
    }

    public TransmittingCanal(double p) {
        data            = null;
        codesTree       = null;
        doubleCodesTree = null;
        codedLength     = 0;

        this.p = p;
    }

    public void receive(char[] data, DoubleHafTree codesTree, int codedLength) {
        this.data               = data;
        this.doubleCodesTree    = codesTree;
        this.codedLength        = codedLength;
    }

    /** Noise creation **/
    private void interfere() {
        Random rand = new Random();
        for (int i = 0; i < codedLength; ++i) {
            if (rand.nextDouble() < p)
                data[i / 16] ^= 1 << (15 - i % 16);
        }
    }

    public char[] transmit() {
        interfere();
        return data;
    }

    public DoubleHafTree transmitCodes() {
        return doubleCodesTree;
    }

    public int transmitCodedLength() {
        return codedLength;
    }
}
