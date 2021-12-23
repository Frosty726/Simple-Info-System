public class AntiJamDecoder {

    private DataToTrans input;
    private DataToTrans output;

    private class ErrAnalysis {
        public int errs;
        public int corr;
    }

    private ErrAnalysis hafErr;
    private ErrAnalysis douHafErr;
    private ErrAnalysis uniErr;

    private static final char[][] H = {
            {1, 1, 1},
            {1, 1, 0},
            {1, 0, 1},
            {0, 1, 1},
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
    };

    public AntiJamDecoder() {
        hafErr = new ErrAnalysis();
        douHafErr = new ErrAnalysis();
        uniErr = new ErrAnalysis();
    }

    public void receive(DataToTrans data) {
        input = data;
        output = new DataToTrans(
                new char[data.hafCount / 16 + 1],
                new char[data.douHafCount / 16 + 1],
                new char[data.uniCount / 16 + 1],
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

    public void decode() {
        decodeBin(input.haffman, output.haffman, input.hafCount, hafErr);
        decodeBin(input.douHaffman, output.douHaffman, input.douHafCount, douHafErr);
        decodeBin(input.uniformCode, output.uniformCode, input.uniCount, uniErr);
    }


    private void decodeBin(char[] data, char[] out, int length, ErrAnalysis EA) {

        char temp = data[0];
        boolean errFound = false;
        char[] inBox = new char[]{0, 0, 0, 0, 0, 0, 0};
        char[] checkBox = new char[]{0, 0, 0};
        int syndrome = 0;
        int iindex = 0; // current index of input
        int oindex = 0; // current index of output
        int itimer = 16; // remaining numbers to decode in current index
        int otimer = 16; // output char box remaining capacity

        while (length > 0) {

            if (itimer >= 7) {
                for (int i = 0; i < 7; ++i) {
                    inBox[i] = (char) ((temp >> (itimer - i - 1)) & 1);
                }
                itimer -= 7;
            }
            else {
                for (int i = 0; i < itimer; ++i) {
                    inBox[i] = (char) ((temp >> (itimer - i - 1)) & 1);
                }

                ++iindex;
                temp = data[iindex];

                for (int i = itimer; i < 7; ++i) {
                    inBox[i] = (char) ((temp >> (16 + itimer - i - 1)) & 1);
                }

                itimer = 9 + itimer; // 16 - (7 - itimer)
            }

            /** Error checking **/
            for (int i = 0; i < 3; ++i) {
                checkBox[i] = 0;
                for (int j = 0; j < 7; ++j) {
                    checkBox[i] ^= inBox[j] & H[j][i];
                }
            }

            for (int i = 0; i < 3; ++i) {
                if (checkBox[i] == 1) {
                    errFound = true;
                    EA.errs++;
                    break;
                }
            }

            if (errFound) {
                syndrome = 0;

                for (int i = 0; i < 7; ++i) {
                    if (eqVec(checkBox, H[i])) {
                        syndrome = i;
                        break;
                    }
                }

                if (syndrome != 0) {
                    inBox[syndrome] ^= 1;
                    EA.corr++;
                }
            }

            if (otimer < 4) {
                ++oindex;
                otimer = 16;
            }

            for (int i = 0; i < 4; ++i) {
                out[oindex] <<= 1;
                if (inBox[i] == 1)
                    out[oindex]++;
            }

            otimer -= 4;

            length -= 4;
        }
    }

    private boolean eqVec(char[] first, char[] second) {
        for (int i = 0; i < first.length; ++i) {
            if (first[i] != second[i])
                return false;
        }

        return true;
    }

    /** Task doing function **/
    public void analysis(DataToTrans data) {

        int errHaf = 0;
        int errDouHaf = 0;
        int errUni = 0;
        char chComp;

        for (int i = 0; i < data.haffman.length; ++i) {
            chComp = (char)(data.haffman[i] ^ input.haffman[i]);

            for (int j = 0; j < 16; ++j) {
                if ((chComp & (char)(1 << j)) > 0)
                    ++errHaf;
            }
        }

        for (int i = 0; i < data.douHaffman.length; ++i) {
            chComp = (char)(data.douHaffman[i] ^ input.douHaffman[i]);
            for (int j = 0; j < 16; ++j) {
                if ((chComp & (1 << j)) > 0)
                    ++errDouHaf;
            }
        }

        for (int i = 0; i < data.uniformCode.length; ++i) {
            chComp = (char)(data.uniformCode[i] ^ input.uniformCode[i]);
            for (int j = 0; j < 16; ++j) {
                if ((chComp & (1 << j)) > 0)
                    ++errUni;
            }
        }

        int onesCount = 0;
        int codedLength = (input.hafCount / 4 + input.hafCount % 4) * 7 ;
        for (char ch : input.haffman) {
            for (int i = 0; i < 16; ++i) {
                onesCount += (ch & (char)32768) >>> 15;
                ch <<= 1;
            }
        }

        System.out.println("Haffman Algorithm");
        System.out.println("Total characters: " + codedLength);
        System.out.println("Total '1': " + onesCount);
        System.out.println("Total '0': " + (codedLength - onesCount));
        System.out.println("Average code length: " + ((double)codedLength / DataToTrans.textLength));
        System.out.println("Errors found: " + hafErr.errs);
        System.out.println("Errors corrected: " + hafErr.corr);
        System.out.println("Errors not found: " + (errHaf - hafErr.errs));
        System.out.println();

        onesCount = 0;
        codedLength = (input.douHafCount / 4 + input.douHafCount % 4) * 7 ;
        for (char ch : input.douHaffman) {
            for (int i = 0; i < 16; ++i) {
                onesCount += (ch & (char)32768) >>> 15;
                ch <<= 1;
            }
        }

        System.out.println("Haffman Algorithm for double character blocks");
        System.out.println("Total characters: " + codedLength);
        System.out.println("Total '1': " + onesCount);
        System.out.println("Total '0': " + (codedLength - onesCount));
        System.out.println("Average code length: " + ((double)codedLength / DataToTrans.textLength));
        System.out.println("Errors found: " + douHafErr.errs);
        System.out.println("Errors corrected: " + douHafErr.corr);
        System.out.println("Errors not found: " + (errDouHaf - douHafErr.errs));
        System.out.println();

        onesCount = 0;
        codedLength = (input.uniCount / 4 + input.uniCount % 4) * 7 ;
        for (char ch : input.uniformCode) {
            for (int i = 0; i < 16; ++i) {
                onesCount += (ch & (char)32768) >>> 15;
                ch <<= 1;
            }
        }

        System.out.println("Uniform code");
        System.out.println("Total characters: " + codedLength);
        System.out.println("Total '1': " + onesCount);
        System.out.println("Total '0': " + (codedLength - onesCount));
        System.out.println("Average code length: " + ((double)codedLength / DataToTrans.textLength));
        System.out.println("Errors found: " + uniErr.errs);
        System.out.println("Errors corrected: " + uniErr.corr);
        System.out.println("Errors not found: " + (errUni - uniErr.errs));
        System.out.println();
    }
}
