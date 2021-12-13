import java.io.*;
import java.util.Scanner;

public class SimpleInfoSystem {
    public static void main(String[] args) {
        /** Reading chance of noise from console **/
        double p = 0;
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter chance of noise to appear [0, 1]:");
        p = scan.nextDouble();

        String inFileName  = "input.txt";
        String haffmanFile = "haffman.txt";
        String douHaffmanFile = "doubleHaffman.txt";
        String uniformFile = "uniformCode.txt";

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
        TransmittingCanal canal = new TransmittingCanal(p);
        canal.receive(coder.code());
        coder.analysis();

        /** Receiving and decoding data **/
        Decoder decoder = new Decoder();
        decoder.receive(canal.transmit());
        decoder.decode();

        /** Receiving decoded data **/
        Receiver receiver = new Receiver();
        receiver.receive(decoder.send());

        /** Saving data in output file **/
        OutText result = receiver.getData();

        try (FileOutputStream fout = new FileOutputStream(haffmanFile)) {
            byte[] buffer = result.haffman.getBytes();
            fout.write(buffer);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try (FileOutputStream fout = new FileOutputStream(douHaffmanFile)) {
            byte[] buffer = result.douHaffman.getBytes();
            fout.write(buffer);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try (FileOutputStream fout = new FileOutputStream(uniformFile)) {
            byte[] buffer = result.uniform.getBytes();
            fout.write(buffer);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
