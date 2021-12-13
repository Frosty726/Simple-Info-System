public class Receiver {
    private OutText data;

    public void receive(OutText data) {
        this.data = data;
    }

    public OutText getData() {
        return data;
    }
}
