public class Investigator extends Agent {
    private double contribution;
    private int rank;

    // investigator constructor
    public Investigator(String name, int ID, int experience, boolean canDrive, double contribution, int rank) throws IDTooLongException,
            NegativeContributionException, NegativeRankException {
        super(name, ID, experience, canDrive);
        if(contribution <= 0){
            throw new NegativeContributionException();
        }
        this.contribution = contribution;
        if(rank <= 0){
            throw new NegativeRankException();
        }
        this.rank = rank;
        this.salary = this.rank * this.contribution; //calculates salary
    }

    public double getSalary(){ // salary getter
        return salary;
    }
}
