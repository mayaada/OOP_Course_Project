public class CapacityNotInRangeException extends Exception{
    public CapacityNotInRangeException() {
        super("Capacity must be between 3 to 6.");
    }
}
