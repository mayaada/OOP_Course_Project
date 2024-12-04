import java.util.ArrayList;
import java.util.List;

public class Operation implements Comparable<Operation> {
    private int level;
    private String codeName;
    private int numOfInvestigators;
    private int numOfDetectives;
    private List<Vehicle> vehicles = new ArrayList<>();

    public Operation(int level, String codeName) throws LevelOutOfBoundsException { // operation constructor
        if (level < 1 || level > 5) { //checks correct level range
            throw new LevelOutOfBoundsException();
        }
        this.level = level;
        this.codeName = codeName;

    }

    public int numOfInvestigators() { // investigators assigned getter
        for (Vehicle vehicle : vehicles) {
            this.numOfInvestigators += vehicle.getNumOfInvestigators();
        }
        return numOfInvestigators;
    }

    public int getNumOfDetectives() { // detectives assigned getter
        for (Vehicle vehicle : vehicles) {
            this.numOfDetectives += vehicle.getNumOfDetectives();
        }
        return numOfDetectives;
    }

    @Override
    public int compareTo(Operation other) { //compares between two operations by level
        return this.level - other.level;
    }

    public int getRequiredDetectivesCount() { // gets required amount of detectives by level
        int currentLevel = this.level;
        int result = 0;
        switch (currentLevel) {
            case 1:
                result = 0;
                break;
            case 2:
                result = 2;
                break;
            case 3:
                result = 5;
                break;
            case 4:
                result = 6;
                break;
            case 5:
                result = 8;
                break;
        }
        return result;
    }

    public int getRequiredInvestigatorsCount() { // gets required amount of investigators by level
        int currentLevel = this.level;
        int result = 0;

        switch (currentLevel) {
            case 1:
                result = 2;
                break;
            case 2:
                result = 3;
                break;
            case 3:
                result = 1;
                break;
            case 4:
                result = 4;
                break;
            case 5:
                result = 7;
                break;
        }

        return result;
    }

    public String getOperationType() { //returns operation type name by level
        String result = "";
        int currentLevel = this.level;
        switch (currentLevel) {
            case 1:
                result = "inquiry";
                break;
            case 2:
                result = "Background check";
                break;
            case 3:
                result = "surveillance";
                break;
            case 4:
                result = "fraud and illegal activity";
                break;
            case 5:
                result = "missing people ";
                break;
        }

        return result;
    }

    public String getCodeName() { //codeName getter
        return codeName;
    }

    public List<Vehicle> getVehicles() { // vehicles getter
        return vehicles;
    }

    public int getLevel() { //level getter
        return level;
    }

    public void removeAllAgentsAndVehicles() { //removes all agents and vehicles from operation
        for (Vehicle vehicle : vehicles) {
            List<Agent> agents = vehicle.getAgents();
            for (Agent agent : agents) {
                agent.isFree = true; //free up agent
            }
            agents.removeAll(agents);
            vehicle.isFree = true; // free up vehicle
        }
        vehicles.removeAll(vehicles);
    }
}