public class Car extends Vehicle {
    private int fuelCap;

    public Car(int carID, int speed, double usageCost, int capacity, int fuelCap) throws CapacityNotInRangeException { //car constructor
        super(carID, speed, usageCost);
        this.fuelCap = fuelCap;
        if (capacity < 3 || capacity > 6) { // checks for correct capacity terms
            throw new CapacityNotInRangeException();
        } else {
            this.capacity = capacity;
        }
    }

    @Override
    public void addDetective(Detective detective) throws VehicleFullException { //adds detective to car
        super.addDetective(detective);
    }

    @Override
    public void addInvestigator(Investigator investigator) { //adds investigator to car
        super.addInvestigator(investigator);
    }
}
