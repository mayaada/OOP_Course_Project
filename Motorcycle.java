
public class Motorcycle extends Vehicle{
    public Motorcycle(int motorID, int speed, double usageCost) { //motorcycle constructor
        super(motorID, speed, usageCost);
        this.capacity = generateRandomCapacity();
    }

    private int generateRandomCapacity(){ //returns a motorcycle capacity between 1-2 seats
        int capacity = (int) (Math.random() * 2 + 1);
        return capacity;

    }

    @Override
    public void addDetective(Detective detective) throws VehicleFullException { //adds detective
        super.addDetective(detective);
    }

    @Override
    public void addInvestigator(Investigator investigator) { //adds investigator
        super.addInvestigator(investigator);
    }
}
