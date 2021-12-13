import java.util.*;

public class DoubleHafTree {

    private DoubleHafTree.Node root;

    public DoubleHafTree(ArrayList<PrePair> arr) {
        ArrayList<DoubleHafTree.Node> nodes = new ArrayList<>();
        for (PrePair pair : arr)
            nodes.add(new DoubleHafTree.Node(((DoublePair)pair).chs, null, null));

        while (nodes.size() != 1)
            for (int i = 0; i < nodes.size() - 1; ++i) {
                nodes.set(i, new DoubleHafTree.Node(null, nodes.get(i), nodes.get(i + 1)));
                nodes.remove(i + 1);
            }

        root = nodes.get(0);
    }

    public class Node {
        public ArrayList<Character> characters;
        public DoubleHafTree.Node left;
        public DoubleHafTree.Node right;

        public Node(ArrayList<Character> ch, DoubleHafTree.Node left, DoubleHafTree.Node right) {
            this.characters = ch;
            this.left       = left;
            this.right      = right;
        }
    }

    public HashMap<ArrayList<Character>, ArrayList<Character>> getCodesMap() {
        HashMap<ArrayList<Character>, ArrayList<Character>> result = new HashMap<>();
        goDeep(result, new ArrayList<Character>(), root);

        return result;
    }

    private void goDeep(HashMap<ArrayList<Character>, ArrayList<Character>> map,
                        ArrayList<Character> code, DoubleHafTree.Node node) {
        if (node.characters != null) {
            map.put(node.characters, code);
            return;
        }

        ArrayList<Character> arrToLeft = (ArrayList<Character>)code.clone();
        arrToLeft.add('1');
        code.add('0');

        goDeep(map, arrToLeft, node.left);
        goDeep(map, code, node.right);
    }

    public DoubleHafTree.Node getRoot() {
        return root;
    }
}


class DoublePair extends PrePair {
    public ArrayList<Character> chs;

    DoublePair(ArrayList<Character> chs, int num) {
        this.chs  = chs;
        this.num = num;
    }
}
