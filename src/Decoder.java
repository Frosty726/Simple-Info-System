public class Decoder {
    private DataToTrans data;
    private OutText output;

    public Decoder() {
        output = new OutText();
    }

    public void receive(DataToTrans data) {
        this.data = data;
    }

    public void decode() {

        /** Decoding Haffman **/
        HafTree.Node node = data.codesTree.getRoot();
        int index = 0;
        int timer = 16;

        while (data.hafCount >= 0) {
            if (node.character != '\0') {
                output.haffman += node.character;
                node = data.codesTree.getRoot();
            }
            if ((data.haffman[index] & (char)32768) != 0)
                node = node.left;
            else
                node = node.right;

            data.haffman[index] <<= 1;

            --timer;
            --data.hafCount;

            if (timer == 0) {
                timer = 16;
                ++index;
            }
        }

        /** Decoding double Haffman **/
        DoubleHafTree.Node douNode = data.douHafTree.getRoot();
        index = 0;
        timer = 16;

        while (data.douHafCount >= 0) {
            if (douNode.characters != null) {
                output.douHaffman += douNode.characters.get(0);
                if (douNode.characters.size() > 1)
                    output.douHaffman += douNode.characters.get(1);
                douNode = data.douHafTree.getRoot();
            }
            if ((data.douHaffman[index] & (char)32768) != 0)
                douNode = douNode.left;
            else
                douNode = douNode.right;

            data.douHaffman[index] <<= 1;

            --timer;
            --data.douHafCount;

            if (timer == 0) {
                timer = 16;
                ++index;
            }
        }
    }

    public OutText send() {
        return output;
    }
}

class OutText {
    String haffman;
    String douHaffman;

    public OutText() {
        haffman     = "";
        douHaffman  = "";
    }
}