import java.util.Random;

public class TransmittingCanal {
    private DataToTrans data;

    /** Chance of noise to appear **/
    private double p;

    public TransmittingCanal(double p) {
        this.p = p;
    }

    public void receive(DataToTrans data) {
        this.data = data;
    }

    /** Noise creation **/
    private void interfere() {
        Random rand = new Random();

        for (int i = 0; i < data.hafCount; ++i) {
            if (rand.nextDouble() < p)
                data.haffman[i / 16] ^= 1 << (15 - i % 16);
        }

        for (int i = 0; i < data.douHafCount; ++i) {
            if (rand.nextDouble() < p)
                data.douHaffman[i / 16] ^= 1 << (15 - i % 16);
        }

        for (int i = 0; i < data.uniCount; ++i) {
            if (rand.nextDouble() < p)
                data.uniformCode[i / 16] ^= 1 << (15 - i % 16);
        }
    }

    public DataToTrans transmit() {
        interfere();
        return data;
    }
}
