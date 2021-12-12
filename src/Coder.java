import java.util.*;

public class Coder {
    private char[] input;
    private HashMap<Character, Integer> countMap;
    private HafTree codesTree;
    private HashMap<Character, ArrayList<Character>> codesMap;
    private char[] data;

    public Coder() {
        input       = null;
        codesTree   = null;
        countMap    = null;
        countMap    = null;
        data        = null;
    }

    /** Function to compare Pairs **/
    private boolean compare(Pair first, Pair second) {
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

        ArrayList<Pair> codes = new ArrayList<Pair>();
        for (char ch : countMap.keySet()) {
            codes.add(new Pair(ch, countMap.get(ch)));
        }

        sortArr(codes);

        codesTree = new HafTree(codes);
    }

    /** Using quickSort **/
    private void sortArr(ArrayList<Pair> arr) {
        sortSegment(arr, 0, arr.size() - 1);
    }

    private void sortSegment(ArrayList<Pair> arr, int left, int right) {
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

    private void swap(ArrayList<Pair> arr, int first, int second) {
        if (first == second)
            return;
        Pair temp = arr.get(first);
        arr.set(first, arr.get(second));
        arr.set(second, temp);
    }

    /** Count total length of bites to transmit **/
    public int codedLenCount() {
        int result = 0;
        for (char ch : codesMap.keySet())
            result += codesMap.get(ch).size() * countMap.get(ch);

        return result;
    }

    /** Getting map to code data **/
    private void codeChars() {
        codesMap = codesTree.getCodesMap();
    }

    /** Handle all coding process **/
    public char[] code() {
        doHaffman();
        codeChars();

        int codedLength = codedLenCount();
        data = new char[codedLength / 16 + 1];

        int timer = 16; // max 16
        int index = 0;
        int length; // length of character's code to add
        for (char ch : input) {
            length = codesMap.get(ch).size();
            if (length <= timer) {
                for (int i = 0; i < length; ++i) {
                    data[index] <<= 1;
                    if (codesMap.get(ch).get(i) == '1')
                        data[index] += 1;
                }
                timer -= length;
            }
            else {
                for (int i = 0; i < timer; ++i) {
                    data[index] <<= 1;
                    if (codesMap.get(ch).get(i) == '1')
                        data[index] += 1;
                }

                length -= timer;
                timer = 16;
                ++index;

                for (int i = 0; i < length; ++i) {
                    data[index] <<= 1;
                    if (codesMap.get(ch).get(codesMap.get(ch).size() - length + i) == '1')
                        data[index] += 1;
                }

                timer -= length;
            }
        }

        data[data.length - 1] <<= 16 - codedLength % 16;

        return data;
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
