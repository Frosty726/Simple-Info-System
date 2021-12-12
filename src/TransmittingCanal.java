import java.util.Random;

public class TransmittingCanal {
    private char[] data;
    private HafTree codesTree;
    private int codedLength;

    /** Chance of noise to appear **/
    private double p;

    public TransmittingCanal() {
        data        = null;
        codesTree   = null;
        codedLength = 0;
        p           = 0;
    }

    public TransmittingCanal(double p) {
        data        = null;
        codesTree   = null;
        codedLength = 0;

        this.p = p;
    }

    public void receive(char[] data, HafTree codesTree, int codedLength) {
        this.data        = data;
        this.codesTree   = codesTree;
        this.codedLength = codedLength;
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

    public HafTree transmitCodes() {
        return codesTree;
    }

    public int transmitCodedLength() {
        return codedLength;
    }
}
