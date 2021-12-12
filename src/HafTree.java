import java.util.ArrayList;
import java.util.HashMap;

public class HafTree {
    private Node root;

    public HafTree(ArrayList<Pair> arr) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        for (Pair pair : arr)
            nodes.add(new Node(pair.ch, null, null));

        while (nodes.size() != 1)
            for (int i = 0; i < nodes.size() - 1; ++i) {
                nodes.set(i, new Node('\0', nodes.get(i), nodes.get(i + 1)));
                nodes.remove(i + 1);
            }

        root = nodes.get(0);
    }

    public class Node {
        public char character;
        public Node left;
        public Node right;

        public Node(char ch, Node left, Node right) {
            this.character  = ch;
            this.left       = left;
            this.right      = right;
        }
    }

    public HashMap<Character, ArrayList<Character>> getCodesMap() {
        HashMap<Character, ArrayList<Character>> result = new HashMap<>();
        goDeep(result, new ArrayList<Character>(), root);

        return result;
    }

    private void goDeep(HashMap<Character, ArrayList<Character>> map, ArrayList<Character> code, Node node) {
        if (node.character != '\0') {
            map.put(node.character, code);
            return;
        }

        ArrayList<Character> arrToLeft = (ArrayList<Character>)code.clone();
        arrToLeft.add('1');
        code.add('0');

        goDeep(map, arrToLeft, node.left);
        goDeep(map, code, node.right);
    }

    public Node getRoot() {
        return root;
    }
}


class Pair {
    public char ch;
    public int num;

    Pair(char ch, int num) {
        this.ch  = ch;
        this.num = num;
    }
}
