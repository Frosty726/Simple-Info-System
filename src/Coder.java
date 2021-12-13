import java.util.*;

public class Coder {
    private char[] input;
    private HashMap<Character, Integer> countMap;
    private HashMap<ArrayList<Character>, Integer> douCountMap;
    private HafTree codesTree;
    private DoubleHafTree doubleCodesTree;
    private HashMap<Character, ArrayList<Character>> codesMap;
    private HashMap<ArrayList<Character>, ArrayList<Character>> doubleCodesMap;
    private char[] data;

    public Coder() {
        input           = null;
        codesTree       = null;
        doubleCodesTree = null;
        countMap        = null;
        data            = null;
    }

    /** Function to compare Pairs **/
    private boolean compare(PrePair first, PrePair second) {
        if (first.num > second.num)
            return true;
        else
            return false;
    }

    public void receive(String input) {
        this.input = input.toCharArray();
    }

    /** Count number of characters **/
    private void count() {
        countMap = new HashMap<>();

        for (char ch : input)
            if (countMap.containsKey(ch))
                countMap.replace(ch, countMap.get(ch) + 1);
            else
                countMap.put(ch, 1);
    }

    private void doHaffman() {
        count();

        ArrayList<PrePair> codes = new ArrayList<>();
        for (char ch : countMap.keySet()) {
            codes.add(new Pair(ch, countMap.get(ch)));
        }

        ArrayList<PrePair> doubleCodes = new ArrayList<>();
        ArrayList<Character> temp = null;
        for (int i = 0; i < codes.size(); ++i) {
            for (int j = 0; j < codes.size(); ++j) {
                temp = new ArrayList<Character>();
                temp.add(((Pair)codes.get(i)).ch);
                temp.add(((Pair)codes.get(j)).ch);
                doubleCodes.add(new DoublePair(temp, codes.get(i).num * codes.get(i).num));
            }
        }

        sortArr(doubleCodes);

        doubleCodesTree = new DoubleHafTree(doubleCodes);
    }

    /** Using quickSort **/
    private void sortArr(ArrayList<PrePair> arr) {
        sortSegment(arr, 0, arr.size() - 1);
    }

    private void sortSegment(ArrayList<PrePair> arr, int left, int right) {
        if (left >= right)
            return;
        int pivot = right;
        int i = left;
        while (i < pivot) {
            if (compare(arr.get(i), arr.get(pivot))) {
                swap(arr, i, pivot);
                swap(arr, i, pivot - 1);
                --pivot;
            }
            else
                ++i;
        }

        sortSegment(arr, left, pivot - 1);
        sortSegment(arr, pivot + 1, right);
    }

    private void swap(ArrayList<PrePair> arr, int first, int second) {
        if (first == second)
            return;
        PrePair temp = arr.get(first);
        arr.set(first, arr.get(second));
        arr.set(second, temp);
    }

    private void doubleCount() {
        douCountMap = new HashMap<>();
        ArrayList<Character> key = new ArrayList<>();
        for (int i = 0; i < input.length - 1; i += 2) {
            key.add(input[i]);
            key.add(input[i + 1]);

            if (douCountMap.containsKey(key))
                douCountMap.replace(key, douCountMap.get(key) + 1);
            else
                douCountMap.put(key, 1);

            key = new ArrayList<>();
        }
    }

    /** Count total length of bites to transmit **/
    public int codedLenCount() {
        int result = 0;
        for (ArrayList<Character> chs : douCountMap.keySet())
            result += doubleCodesMap.get(chs).size() * douCountMap.get(chs);

        return result;
    }

    /** Getting map to code data **/
    private void codeChars() {
        doubleCodesMap = doubleCodesTree.getCodesMap();
    }

    /** Handle all coding process **/
    public char[] code() {
        doHaffman();
        codeChars();
        doubleCount();

        int codedLength = codedLenCount();
        data = new char[codedLength / 16 + 1];

        int timer = 16; // max 16
        int index = 0;
        int length; // length of character's code to add
        ArrayList<Character> chs = new ArrayList<>();
        for (int j = 0; j < input.length - 1; j += 2) {
            chs.add(input[j]);
            chs.add(input[j + 1]);

            length = doubleCodesMap.get(chs).size();
            if (length <= timer) {
                for (int i = 0; i < length; ++i) {
                    data[index] <<= 1;
                    if (doubleCodesMap.get(chs).get(i) == '1')
                        data[index] += 1;
                }
                timer -= length;
            }
            else {
                for (int i = 0; i < timer; ++i) {
                    data[index] <<= 1;
                    if (doubleCodesMap.get(chs).get(i) == '1')
                        data[index] += 1;
                }

                length -= timer;
                timer = 16;
                ++index;

                for (int i = 0; i < length; ++i) {
                    data[index] <<= 1;
                    if (doubleCodesMap.get(chs).get(doubleCodesMap.get(chs).size() - length + i) == '1')
                        data[index] += 1;
                }

                timer -= length;
            }

            chs.clear();
        }

        data[data.length - 1] <<= 16 - codedLength % 16;

        return data;
    }

    /** Function to transmit codes to decode **/
    public DoubleHafTree getDouCodesTree() {
        return doubleCodesTree;
    }

    /** Function to transmit codes to decode **/
    public HafTree getCodesTree() {
        return codesTree;
    }

    /** Task doing function **/
    public void analysis() {
        int onesCount = 0;
        int codedLength = codedLenCount();
        for (char ch : data) {
            for (int i = 0; i < 16; ++i) {
                onesCount += (ch & (char)32768) >>> 15;
                ch <<= 1;
            }
        }

        System.out.println("Total characters: " + codedLength);
        System.out.println("Total '1': " + onesCount);
        System.out.println("Total '0': " + (codedLength - onesCount));
        System.out.println("Average code length: " + ((double)codedLength / input.length));
    }

    /** Function for convenient binary data printing **/
    static public void debugPrint(char[] input) {
        for (char ch : input) {
            for (int i = 0; i < 16; ++i) {
                System.out.print((ch & (char)32768) >>> 15);
                ch <<= 1;
            }
            System.out.print(' ');
        }

        System.out.println();
    }
}