public class Decoder {
    private DoubleHafTree codesTree;
    private char[] input;
    private String output;
    private int codedLength;

    public Decoder() {
        output      = new String();
        codedLength = 0;
        input       = null;
        codesTree   = null;
    }

    public void receive(char[] data) {
        this.input = data;
    }

    public void receiveCodes(DoubleHafTree codesTree) {
        this.codesTree = codesTree;
    }

    public void receiveCodedLength(int codedLength) {
        this.codedLength = codedLength;
    }

    public void decode() {

        DoubleHafTree.Node node = codesTree.getRoot();
        int index = 0;
        int timer = 16;

        while (codedLength >= 0) {
            if (node.characters != null) {
                output += node.characters.get(0);
                output += node.characters.get(1);
                node = codesTree.getRoot();
            }
            if ((input[index] & (char)32768) != 0)
                node = node.left;
            else
                node = node.right;

            input[index] <<= 1;

            --timer;
            --codedLength;

            if (timer == 0) {
                timer = 16;
                ++index;
            }
        }
    }

    public String send() {
        return output;
    }
}