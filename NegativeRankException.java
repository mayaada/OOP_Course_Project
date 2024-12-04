public class NegativeRankException extends Exception{
    public NegativeRankException() {
        super("Rank must be a positive number.");
    }
}
