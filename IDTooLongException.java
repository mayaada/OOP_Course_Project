public class IDTooLongException extends Exception{
    public IDTooLongException() {
        super("ID must be 5 characters.");
    }
}
