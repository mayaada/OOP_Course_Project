import java.util.ArrayList;
import java.util.List;

public abstract class Vehicle implements Comparable<Vehicle>, Expensable {
    public boolean isFree;
    private int id;
    private int speed;
    private double usageCost;
    protected int capacity;
    private int numOfDetectives;
    private int numOfInvestigators;
    private List<Agent> agents = new ArrayList<>();

    public Vehicle(int id, int speed, double usageCost) {
        this.id = id;
        this.speed = speed;
        this.usageCost = usageCost;
        this.isFree = true;
    }

    public void addDetective(Detective detective) throws VehicleFullException { //adds detective to vehicle
        if (agents.size() < capacity) { //while car not full
            agents.add(detective);
            numOfDetectives++;
        } else {
            throw new VehicleFullException();
        }
    }

    public void addInvestigator(Investigator investigator) { //adds investigator to vehicle
        if (agents.size() < capacity) { //while car not full
            agents.add(investigator);
            numOfInvestigators++;
        } else {
            if (investigator.isCanDrive()) { //replace automatically when investigator can drive
                agents.remove(0);
                agents.add(0, investigator);
                numOfInvestigators++;
            } else {
                Integer driverIndex = this.findPassengerWhoCanDrive();
                if (driverIndex != null) { //if found driver
                    for (int i = 0; i < agents.size(); i++) { //replace detective who can't drive with investigator
                        if (driverIndex != i) {
                            agents.remove(i);
                            agents.add(i, investigator);
                            numOfInvestigators++;
                            break;
                        }
                    }
                }
            }
        }
    }

    private Integer findPassengerWhoCanDrive() { // finds index of driver in list
        for (int i = 0; i < this.agents.size(); i++) {
            if (this.agents.get(i).isCanDrive()) {
                return i;
            }
        }

        return null;
    }

    @Override
    public int compareTo(Vehicle other) { //compares between two vehicles by speed
        return this.speed - other.speed;
    }

    @Override
    public double getExpenses(){ // expenses getter
        return usageCost;
    }

    public int getCapacity() { // capacity getter
        return capacity;
    }

    public int getEmptySeats(){
        return capacity - agents.size();
    }

    public List<Agent> getAgents() { // agents list getter
        return agents;
    }

    public int getId() { // id getter
        return id;
    }

    public int getNumOfDetectives() {
        return numOfDetectives;
    }

    public int getNumOfInvestigators() {
        return numOfInvestigators;
    }
}


