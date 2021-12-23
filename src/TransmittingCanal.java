import java.util.Random;

public class TransmittingCanal {
    private DataToTrans data;

    /** Chance of noise to appear **/
    private double p;

    public TransmittingCanal(double p) {
        this.p = p;
    }

    public void receive(DataToTrans data) {
        this.data = new DataToTrans(
            new char[data.haffman.length],
            new char[data.douHaffman.length],
            new char[data.uniformCode.length],
            data.hafCount,
            data.douHafCount,
            data.uniCount,
            data.codesTree,
            data.douHafTree,
            data.uniTree
        );

        for (int i = 0; i < data.haffman.length; ++i)
            this.data.haffman[i] = data.haffman[i];

        for (int i = 0; i < data.douHaffman.length; ++i)
            this.data.douHaffman[i] = data.douHaffman[i];

        for (int i = 0; i < data.uniformCode.length; ++i)
            this.data.uniformCode[i] = data.uniformCode[i];
    }

    /** Noise creation **/
    private void interfere() {
        Random rand = new Random();

        for (int i = 0; i < data.haffman.length; ++i) {
            if (rand.nextDouble() < p)
                data.haffman[i / 16] ^= 1 << (15 - i % 16);
        }

        for (int i = 0; i < data.douHaffman.length; ++i) {
            if (rand.nextDouble() < p)
                data.douHaffman[i / 16] ^= 1 << (15 - i % 16);
        }

        for (int i = 0; i < data.uniformCode.length; ++i) {
            if (rand.nextDouble() < p)
                data.uniformCode[i / 16] ^= 1 << (15 - i % 16);
        }
    }

    public DataToTrans transmit() {
        interfere();
        return data;
    }
}
