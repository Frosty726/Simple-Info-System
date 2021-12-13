import java.util.ArrayList;
import java.util.HashMap;

public class EqTree {
    private EqTree.Node root;

    public EqTree(ArrayList<PrePair> arr) {
        ArrayList<EqTree.Node> nodes = new ArrayList<>();
        for (PrePair pair : arr)
            nodes.add(new EqTree.Node(((Pair)pair).ch, null, null));

        int remaining = 1;
        while (remaining < arr.size())
            remaining <<= 1;
        for (int i = 0; i < remaining - arr.size(); ++i)
            nodes.add(new EqTree.Node('$', null, null));

        while (nodes.size() != 1)
            for (int i = 0; i < nodes.size() - 1; ++i) {
                nodes.set(i, new EqTree.Node('\0', nodes.get(i), nodes.get(i + 1)));
                nodes.remove(i + 1);
            }

        root = nodes.get(0);
    }

    public class Node {
        public char character;
        public EqTree.Node left;
        public EqTree.Node right;

        public Node(char ch, EqTree.Node left, EqTree.Node right) {
            this.character  = ch;
            this.left       = left;
            this.right      = right;
        }
    }

    public HashMap<Character, ArrayList<Character>> getCodesMap() {
        HashMap<Character, ArrayList<Character>> result = new HashMap<>();
        goDeep(result, new ArrayList<>(), root);

        return result;
    }

    private void goDeep(HashMap<Character, ArrayList<Character>> map, ArrayList<Character> code, EqTree.Node node) {
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

    public EqTree.Node getRoot() {
        return root;
    }
}
