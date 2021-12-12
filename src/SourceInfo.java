public class SourceInfo {

    /** Data to send **/
    private String data;

    SourceInfo(String input) {
        data = input;
    }
    SourceInfo(char[] input) {
        data = new String(input);
    }

    String send() {
        return data;
    }
}
