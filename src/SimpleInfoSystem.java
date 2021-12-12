import java.io.*;

public class SimpleInfoSystem {
    public static void main(String[] args) {
        String inFileName  = "input.txt";
        String outFileName = "output.txt";

        String data = "";

        /** Reading input data from file **/
        try (FileInputStream fin = new FileInputStream(inFileName)) {
            int ch = 0;
            while ((ch = fin.read()) != -1)
                data += (char)ch;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        /** Preparing data for transmit and code **/
        SourceInfo source = new SourceInfo(data);
        Coder coder = new Coder();

        /** Initializing coder device **/
        coder.receive(source.send());

        /** Coding and transmitting data, doing analysis **/
        TransmittingCanal canal = new TransmittingCanal();
        canal.receive(coder.code(), coder.getCodesTree(), coder.codedLenCount());
        coder.analysis();

        /** Receiving and decoding data **/
        Decoder decoder = new Decoder();
        decoder.receive(canal.transmit());
        decoder.receiveCodes(canal.transmitCodes());
        decoder.receiveCodedLength(canal.transmitCodedLength());
        decoder.decode();

        /** Receiving decoded data **/
        Receiver receiver = new Receiver();
        receiver.receive(decoder.send());

        /** Saving data in output file **/
        String result = receiver.getData();
        try (FileOutputStream fout = new FileOutputStream(outFileName)) {
            byte[] buffer = result.getBytes();
            fout.write(buffer);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
