import java.util.*;

public class Coder {
    private char[] input;

    private HashMap<Character, Integer> countMap;
    private HashMap<ArrayList<Character>, Integer> douCountMap;
    private HashMap<Character, Integer> eqCountMap;

    private HafTree codesTree;
    private DoubleHafTree doubleCodesTree;
    private EqTree eqTree;

    private HashMap<Character, ArrayList<Character>> codesMap;
    private HashMap<ArrayList<Character>, ArrayList<Character>> doubleCodesMap;
    private HashMap<Character, ArrayList<Character>> eqCodesMap;

    private char[] data;
    private char[] douData;
    private char[] eqData;

    /** Function to compare Pairs **/
    private boolean compare(PrePair first, PrePair second) {
        if (first.num > second.num)
            return true;
        else
            return false;
    }

    public void receive(String input) {
        this.input = input.toCharArray();
        DataToTrans.textLength = this.input.length;
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

        if (input.length % 2 == 1) {
            key.add(input[input.length - 1]);
            douCountMap.put(key, 1);
        }
    }

    private void eqCount() {
        eqCountMap = new HashMap<>();

        for (char ch : input)
            if (eqCountMap.containsKey(ch))
                eqCountMap.replace(ch, eqCountMap.get(ch) + 1);
            else
                eqCountMap.put(ch, 1);
    }

    private void doHaffman() {
        count();

        ArrayList<PrePair> codes = new ArrayList<>();
        for (char ch : countMap.keySet()) {
            codes.add(new Pair(ch, countMap.get(ch)));
        }

        sortArr(codes);

        codesTree = new HafTree(codes);
    }

    private void doDouHaffman() {
        doubleCount();

        ArrayList<PrePair> codes = new ArrayList<>();
        for (ArrayList<Character> ch : douCountMap.keySet()) {
            codes.add(new DoublePair(ch, douCountMap.get(ch)));
        }

        sortArr(codes);

        doubleCodesTree = new DoubleHafTree(codes);
    }

    private void doEqCoding() {
        eqCount();

        ArrayList<PrePair> codes = new ArrayList<>();
        for (char ch : eqCountMap.keySet()) {
            codes.add(new Pair(ch, eqCountMap.get(ch)));
        }

        sortArr(codes);

        eqTree = new EqTree(codes);
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

    /** Count total length of bites to transmit **/
    private int codedLenCount() {
        int result = 0;
        for (char ch : countMap.keySet())
            result += codesMap.get(ch).size() * countMap.get(ch);

        return result;
    }

    private int douCodedLenCount() {
        int result = 0;
        for (ArrayList<Character> chs : douCountMap.keySet())
            result += doubleCodesMap.get(chs).size() * douCountMap.get(chs);

        return result;
    }

    private int eqLenCount() {
        int result = 0;
        for (char ch : eqCountMap.keySet())
            result += eqCodesMap.get(ch).size() * eqCountMap.get(ch);

        return result;
    }

    /** Getting map to code data **/
    private void codeChars() {
        doubleCodesMap = doubleCodesTree.getCodesMap();
        codesMap = codesTree.getCodesMap();
        eqCodesMap = eqTree.getCodesMap();
    }

    /** Handle all coding process **/
    public DataToTrans code() {

        /** Creating coding tables **/
        doHaffman();
        doDouHaffman();
        doEqCoding();
        codeChars();

        /** Code process **/
        codeHaffman();
        codeDoubleHaffman();
        codeUniformCode();

        return new DataToTrans(data, douData, eqData,
                codedLenCount(), douCodedLenCount(), eqLenCount(),
                codesTree, doubleCodesTree, eqTree);
    }

    private void codeHaffman() {

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
    }

    private void codeDoubleHaffman() {

        int douCodedLength = douCodedLenCount();
        douData = new char[douCodedLength / 16 + 1];

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
                    douData[index] <<= 1;
                    if (doubleCodesMap.get(chs).get(i) == '1')
                        douData[index] += 1;
                }
                timer -= length;
            }
            else {
                for (int i = 0; i < timer; ++i) {
                    douData[index] <<= 1;
                    if (doubleCodesMap.get(chs).get(i) == '1')
                        douData[index] += 1;
                }

                length -= timer;
                timer = 16;
                ++index;

                for (int i = 0; i < length; ++i) {
                    douData[index] <<= 1;
                    if (doubleCodesMap.get(chs).get(doubleCodesMap.get(chs).size() - length + i) == '1')
                        douData[index] += 1;
                }

                timer -= length;
            }

            chs.clear();
        }

        if (input.length % 2 == 1) {
            chs.add(input[input.length - 1]);

            length = doubleCodesMap.get(chs).size();
            if (length <= timer) {
                for (int i = 0; i < length; ++i) {
                    douData[index] <<= 1;
                    if (doubleCodesMap.get(chs).get(i) == '1')
                        douData[index] += 1;
                }
                timer -= length;
            }
            else {
                for (int i = 0; i < timer; ++i) {
                    douData[index] <<= 1;
                    if (doubleCodesMap.get(chs).get(i) == '1')
                        douData[index] += 1;
                }

                length -= timer;
                timer = 16;
                ++index;

                for (int i = 0; i < length; ++i) {
                    douData[index] <<= 1;
                    if (doubleCodesMap.get(chs).get(doubleCodesMap.get(chs).size() - length + i) == '1')
                        douData[index] += 1;
                }
            }
        }

        douData[douData.length - 1] <<= 16 - douCodedLength % 16;
    }

    private void codeUniformCode() {

        int eqLength = eqLenCount();
        eqData = new char[eqLength / 16 + 1];

        int timer = 16; // max 16
        int index = 0;
        int length; // length of character's code to add

        for (char ch : input) {
            length = eqCodesMap.get(ch).size();
            if (length <= timer) {
                for (int i = 0; i < length; ++i) {
                    eqData[index] <<= 1;
                    if (eqCodesMap.get(ch).get(i) == '1')
                        eqData[index] += 1;
                }
                timer -= length;
            }
            else {
                for (int i = 0; i < timer; ++i) {
                    eqData[index] <<= 1;
                    if (eqCodesMap.get(ch).get(i) == '1')
                        eqData[index] += 1;
                }

                length -= timer;
                timer = 16;
                ++index;

                for (int i = 0; i < length; ++i) {
                    eqData[index] <<= 1;
                    if (eqCodesMap.get(ch).get(eqCodesMap.get(ch).size() - length + i) == '1')
                        eqData[index] += 1;
                }

                timer -= length;
            }
        }

        eqData[eqData.length - 1] <<= 16 - eqLength % 16;
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

        System.out.println("Haffman Algorithm");
        System.out.println("Total characters: " + codedLength);
        System.out.println("Total '1': " + onesCount);
        System.out.println("Total '0': " + (codedLength - onesCount));
        System.out.println("Average code length: " + ((double)codedLength / input.length));
        System.out.println();

        onesCount = 0;
        codedLength = douCodedLenCount();
        for (char ch : douData) {
            for (int i = 0; i < 16; ++i) {
                onesCount += (ch & (char)32768) >>> 15;
                ch <<= 1;
            }
        }

        System.out.println("Haffman Algorithm for double character blocks");
        System.out.println("Total characters: " + codedLength);
        System.out.println("Total '1': " + onesCount);
        System.out.println("Total '0': " + (codedLength - onesCount));
        System.out.println("Average code length: " + ((double)codedLength / input.length));
        System.out.println();

        onesCount = 0;
        codedLength = eqLenCount();
        for (char ch : eqData) {
            for (int i = 0; i < 16; ++i) {
                onesCount += (ch & (char)32768) >>> 15;
                ch <<= 1;
            }
        }

        System.out.println("Uniform code");
        System.out.println("Total characters: " + codedLength);
        System.out.println("Total '1': " + onesCount);
        System.out.println("Total '0': " + (codedLength - onesCount));
        System.out.println("Average code length: " + ((double)codedLength / input.length));
        System.out.println();
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

    static public void debugPrint(char ch) {
        for (int i = 0; i < 16; ++i) {
            System.out.print((ch & (char)32768) >>> 15);
            ch <<= 1;
        }

        System.out.println();
    }
}

class DataToTrans {
    public char[] haffman;
    public char[] douHaffman;
    public char[] uniformCode;

    public int hafCount;
    public int douHafCount;
    public int uniCount;

    public HafTree codesTree;
    public DoubleHafTree douHafTree;
    public EqTree uniTree;

    /** Data for analysis **/
    public static int textLength;

    public DataToTrans(char[] haffman, char[] douHaffman, char[] uniformCode,
                       int hafCount, int douHafCount, int uniCount,
                       HafTree codesTree, DoubleHafTree douHafTree, EqTree uniTree) {
        this.haffman        = haffman;
        this.douHaffman     = douHaffman;
        this.uniformCode    = uniformCode;
        this.hafCount       = hafCount;
        this.douHafCount    = douHafCount;
        this.uniCount       = uniCount;
        this.codesTree      = codesTree;
        this.douHafTree     = douHafTree;
        this.uniTree        = uniTree;
    }
}