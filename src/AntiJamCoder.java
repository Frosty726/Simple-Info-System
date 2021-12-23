public class AntiJamCoder {

    private DataToTrans input;
    private DataToTrans output;

    private static final char[][] G = {
            {1, 0, 0, 0, 1, 1, 1},
            {0, 1, 0, 0, 1, 1, 0},
            {0, 0, 1, 0, 1, 0, 1},
            {0, 0, 0, 1, 0, 1, 1}
    };

    public void receive(DataToTrans data) {
        input = data;
        output = new DataToTrans(
                new char[((data.hafCount / 16 + 1) / 4 + 1) * 7],
                new char[((data.douHafCount / 16 + 1) / 4 + 1) * 7],
                new char[((data.uniCount / 16 + 1) / 4 + 1) * 7],
                data.hafCount,
                data.douHafCount,
                data.uniCount,
                data.codesTree,
                data.douHafTree,
                data.uniTree
        );
    }

    public DataToTrans send() {
        return output;
    }

    public void code() {
        codeBin(input.haffman, output.haffman, input.hafCount);
        codeBin(input.douHaffman, output.douHaffman, input.douHafCount);
        codeBin(input.uniformCode, output.uniformCode, input.uniCount);
    }


    private void codeBin(char[] data, char[] out, int length){

        char temp;
        char[] codeBox = new char[]{0, 0, 0, 0, 0, 0, 0};
        char[] inBox = new char[]{0, 0, 0, 0};
        int iindex = 0; // current index of input
        int oindex = 0; // current index of output
        int itimer = 12; // count digits to shift
        int otimer = 16; // output char box remaining capacity

        while (length > 0) {

            temp = data[iindex];
            temp >>= itimer;

            for (int i = 0; i < 4; ++i) {
                inBox[3 - i] = (char)(temp & 1);
                temp >>= 1;
            }

            for (int i = 0; i < 7; ++i) {
                codeBox[i] = 0;
                for (int j = 0; j < 4; ++j) {
                    codeBox[i] ^= inBox[j] & G[j][i];
                }
            }

            if (otimer < 7) {
                for (int i = 0; i < otimer; ++i) {
                    out[oindex] <<= 1;
                    if (codeBox[i] == 1)
                        out[oindex]++;
                }

                ++oindex;

                for (int i = otimer; i < 7; ++i) {
                    out[oindex] <<= 1;
                    if (codeBox[i] == 1)
                        out[oindex]++;
                }

                otimer = 9 + otimer; // 16 - (7 - otimer)
            }
            else {
                for (int i = 0; i < 7; ++i) {
                    out[oindex] <<= 1;
                    if (codeBox[i] == 1)
                        out[oindex]++;
                }

                otimer -= 7;
            }

            if (itimer > 0) {
                itimer -= 4;
            }
            else {
                itimer = 12;
                ++iindex;
            }

            length -= 4;
        }

        out[oindex] <<= otimer;
    }

    public DataToTrans getData() {
        return output;
    }
}
