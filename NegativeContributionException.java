public class NegativeContributionException extends Exception{
    public NegativeContributionException() {
        super("Contribution must be a positive number.");
    }
}
