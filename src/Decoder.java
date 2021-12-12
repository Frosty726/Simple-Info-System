public class Decoder {
    private HafTree codesTree;
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

    public void receiveCodes(HafTree codesTree) {
        this.codesTree = codesTree;
    }

    public void receiveCodedLength(int codedLength) {
        this.codedLength = codedLength;
    }

    public void decode() {

        HafTree.Node node = codesTree.getRoot();
        int index = 0;
        int timer = 16;

        while (codedLength >= 0) {
            if (node.character != '\0') {
                output += node.character;
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
