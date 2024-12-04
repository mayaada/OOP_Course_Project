import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Agency {
    private final List<Agent> agents = new ArrayList<>();
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<Operation> openOperations = new ArrayList<>();

    //agency constructor
    public Agency(String agents, String vehicles) throws IDTooLongException, CapacityNotInRangeException, NegativeRankException, NegativeContributionException {
        readAgentsFromFile(agents);
        readVehiclesFromFile(vehicles);
    }

    //reads vehicle txt file
    private void readVehiclesFromFile(String vehicles) throws CapacityNotInRangeException {
        BufferedReader inFile2 = null;
        try {
            FileReader file = new FileReader(vehicles);
            inFile2 = new BufferedReader(file);
            boolean header = true;
            String line = inFile2.readLine();
            while (line != null) { //check not last line
                if (header) { //skip header line
                    header = false;
                } else {
                    addVehicle(line);
                }
                line = inFile2.readLine(); // read next line
            }
        } catch (FileNotFoundException exception) {
            System.out.println("The file " + vehicles + " was not found.");
        } catch (IOException exception) {
            System.out.println(exception);
        } finally {
            try {
                if (inFile2 != null) {
                    inFile2.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    //reads agents txt file
    private void readAgentsFromFile(String agents) throws IDTooLongException, NegativeRankException, NegativeContributionException {
        BufferedReader inFile = null;
        try {
            FileReader file = new FileReader(agents);
            inFile = new BufferedReader(file);
            boolean header = true;
            String line = inFile.readLine();
            while (line != null) { //check not last line
                if (header) { //skip header line
                    header = false;
                } else {
                    addAgent(line);
                }
                line = inFile.readLine(); // read next line
            }
        } catch (FileNotFoundException exception) {
            System.out.println("The file " + agents + " was not found.");
        } catch (IOException exception) {
            System.out.println(exception);
        } finally {
            try {
                if (inFile != null) {
                    inFile.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    //reads and creates new agents by type in agency
    private void addAgent(String line) throws IDTooLongException, NegativeRankException, NegativeContributionException {
        Agent agent;
        String[] words = line.split("\\s+"); //split line into separate words
        String name = words[0];
        int id = Integer.parseInt(words[1]);
        String type = words[2];
        int experience = Integer.parseInt(words[3]);
        boolean canDrive = Boolean.parseBoolean(words[4]);
        if (type.equals("Detective")) { //check type of agent to save and save identifying fields
            boolean weaponLicense = Boolean.parseBoolean(words[5]);
            agent = new Detective(name, id, experience, canDrive, weaponLicense);
        } else { // creates new investigator
            double contribution = Double.parseDouble(words[5]);
            int rank = Integer.parseInt(words[6]);
            agent = new Investigator(name, id, experience, canDrive, contribution, rank);
        }
        this.agents.add(agent); //save vehicle to agents arraylist
    }

    // reads and creates new vehicles by type in agency
    private void addVehicle(String line) throws CapacityNotInRangeException {
        Vehicle vehicle;
        String[] words = line.split("\\s+"); //split line into separate words
        int id = Integer.parseInt(words[0]);
        String type = words[1];
        int speed = Integer.parseInt(words[2]);
        double usageCost = Double.parseDouble(words[3]);
        if (type.equals("Car")) { //check type of vehicle to save and save identifying fields
            int capacity = Integer.parseInt(words[4]);
            int fuelCap = Integer.parseInt(words[5]);
            vehicle = new Car(id, speed, usageCost, capacity, fuelCap);
        } else { // creates new motocycle
            vehicle = new Motorcycle(id, speed, usageCost);
        }
        this.vehicles.add(vehicle); //save vehicle to vehicles arraylist
    }

    public static Comparable getMax(List<Comparable> comparables) { //compares between two comparable object lists and returns max object
        Comparable max = null;
        for (Comparable comparable : comparables) {
            if (max == null) { // set first in list as max
                max = comparable;
            } else {
                if (comparable.compareTo(max) > 0) {
                    max = comparable;
                }
            }
        }

        return max;
    }

    //creates new operation and assigns vehicles and agents to it
    public boolean openOperation(int level, String codeName) throws LevelOutOfBoundsException{
        Operation operation = new Operation(level, codeName);
        int requiredDetectives = operation.getRequiredDetectivesCount();
        int requiredInvestigators = operation.getRequiredInvestigatorsCount();
        int requiredVehicleSeats = requiredDetectives + requiredInvestigators;

        List<Vehicle> availableVehiclesBySpeed = getAvailableVehicles();

        if (!assignVehiclesToOperation(level, operation, requiredVehicleSeats, availableVehiclesBySpeed)) { //not able to assign enough vehicles
            return false;
        }

        List<Detective> availableDetectivesByExperience = getAvailableAgents(Detective.class);
        List<Investigator> availableInvestigatorsByExperience = getAvailableAgents(Investigator.class);

        boolean isEnoughAvailableAgents = availableDetectivesByExperience.size() < requiredDetectives || availableInvestigatorsByExperience.size() < requiredInvestigators;
        if (isEnoughAvailableAgents) {
            return false;
        }

        int assignedDetectives = 0;
        int assignedInvestigators = 0;
        for (Vehicle vehicle : operation.getVehicles()) { //assign a driver to each vehicle
            assignedDetectives = assignDetectiveDriver(level, requiredDetectives, availableDetectivesByExperience, assignedDetectives, vehicle);

            boolean foundDriver = vehicle.getAgents() != null;
            assignedInvestigators = assignInvestigatorDriver(requiredInvestigators, availableInvestigatorsByExperience, assignedInvestigators, vehicle, foundDriver);
        }

        for (Vehicle vehicle : operation.getVehicles()) { //fill vehicle with agents by need
            assignedDetectives = fillVehicleWithDetectives(level, requiredDetectives, availableDetectivesByExperience, assignedDetectives, vehicle);

            assignedInvestigators = fillVehicleWithInvestigators(requiredInvestigators, availableInvestigatorsByExperience, assignedInvestigators, vehicle);
        }

        boolean notAllAgentsAssigned = assignedDetectives < requiredDetectives || assignedInvestigators < requiredInvestigators;
        if (notAllAgentsAssigned) {
            return false;
        }

        openOperations.add(operation);
        return true;
    }

    //fill and assign investigators to open operation
    private int fillVehicleWithInvestigators(int requiredInvestigators, List<Investigator> availableInvestigatorsByExperience, int assignedInvestigators, Vehicle vehicle) {
        if (assignedInvestigators < requiredInvestigators) {
            for (int i = 0; i < availableInvestigatorsByExperience.size() && vehicle.getEmptySeats() != 0 && assignedInvestigators < requiredInvestigators; i++) {
                Investigator investigator = availableInvestigatorsByExperience.get(i);
                assignedInvestigators = assignInvestigator(vehicle, investigator, availableInvestigatorsByExperience, assignedInvestigators);
            }
        }
        return assignedInvestigators;
    }

    // fill and assign detective to open operation
    private int fillVehicleWithDetectives(int level, int requiredDetectives, List<Detective> availableDetectivesByExperience, int assignedDetectives, Vehicle vehicle) {
        if (assignedDetectives < requiredDetectives) {
            for (int i = 0; i < availableDetectivesByExperience.size() && vehicle.getEmptySeats() != 0 && assignedDetectives < requiredDetectives; i++) {
                Detective detective = availableDetectivesByExperience.get(i);
                if (level != 5 || detective.isWeaponLicense()) {
                    assignedDetectives = assignDetective(vehicle, detective, availableDetectivesByExperience, assignedDetectives);
                }
            }
        }
        return assignedDetectives;
    }

    //assign detective to vehicle
    private int assignDetective(Vehicle vehicle, Detective detective, List<Detective> availableDetectivesByExperience, int assignedDetectives) {
        vehicle.addDetective(detective);
        availableDetectivesByExperience.remove(detective);
        assignedDetectives++;
        detective.isFree = false;
        return assignedDetectives;
    }

    //assign investigator driver to vehicle
    private int assignInvestigatorDriver(int requiredInvestigators, List<Investigator> availableInvestigatorsByExperience, int assignedInvestigators, Vehicle vehicle, boolean foundDriver) {
        if (foundDriver && assignedInvestigators < requiredInvestigators) {
            for (int i = 0; i < availableInvestigatorsByExperience.size(); i++) {
                Investigator investigator = availableInvestigatorsByExperience.get(i);
                if (investigator.isCanDrive()) {
                    assignedInvestigators = assignInvestigator(vehicle, investigator, availableInvestigatorsByExperience, assignedInvestigators);
                    break;
                }
            }
        }
        return assignedInvestigators;
    }

    //assign investigator to vehicle
    private int assignInvestigator(Vehicle vehicle, Investigator investigator, List<Investigator> availableInvestigatorsByExperience, int assignedInvestigators) {
        vehicle.addInvestigator(investigator);
        availableInvestigatorsByExperience.remove(investigator);
        assignedInvestigators++;
        investigator.isFree = false;
        return assignedInvestigators;
    }

    //assign detective driver to vehicle
    private int assignDetectiveDriver(int level, int requiredDetectives, List<Detective> availableDetectivesByExperience, int assignedDetectives, Vehicle vehicle) {
        if (assignedDetectives < requiredDetectives) {
            for (int i = 0; i < availableDetectivesByExperience.size(); i++) {
                Detective detective = availableDetectivesByExperience.get(i);
                if (detective.isCanDrive() && (level != 5 || detective.isWeaponLicense())) {
                    assignedDetectives = assignDetective(vehicle, detective, availableDetectivesByExperience, assignedDetectives);
                    detective.setSalary(level); //set detective salary by operation level
                    break;
                }
            }
        }
        return assignedDetectives;
    }

    // checks and assigns vehicles to operation if possible
    private boolean assignVehiclesToOperation(int level, Operation operation, int requiredVehicleSeats, List<Vehicle> availableVehiclesBySpeed) {
        int foundSeats = 0;
        for (int i = 0; i < availableVehiclesBySpeed.size() && (level != 3 && foundSeats < requiredVehicleSeats || operation.getVehicles().size() < 2); i++) {
            Vehicle vehicle = availableVehiclesBySpeed.get(i);
            operation.getVehicles().add(vehicle); //adds vehicle to operation
            vehicle.isFree = false; //turns vehicle occupied
            foundSeats += vehicle.getCapacity();
        }

        if (foundSeats < requiredVehicleSeats) { //checks if found enough seats for operation
            return false;
        }
        if (level == 3 && operation.getVehicles().size() < 2) { //checks 2 or more vehicles for level 3
            return false;
        }

        return true;
    }

    private List<Vehicle> getAvailableVehicles() { // lists available vehicles by max speed order from top to bottom
        List<Vehicle> availableVehiclesBySpeed = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.isFree) {
                availableVehiclesBySpeed.add(vehicle);
            }
        }
        availableVehiclesBySpeed.sort(Collections.reverseOrder());
        return availableVehiclesBySpeed;
    }

    private <T extends Agent> List<T> getAvailableAgents(Class<T> clazz) { // lists available detectives by max experience order from top to bottom
        List<T> availableAgentsByExperience = new ArrayList<>();
        for (Agent agent : agents) {
            if (clazz.isInstance(agent)) {
                if (agent.isFree) {
                    availableAgentsByExperience.add((T) agent);
                }
            }
        }
        availableAgentsByExperience.sort(Collections.reverseOrder());
        return availableAgentsByExperience;
    }

    //finds the average experience between two experienceable objects in a list
    public static double averageExperience(List<? extends Experienceable> experienceables) {
        double totalExperience = 0;
        int numOfExperienceables = experienceables.size();
        for (Experienceable experienceable : experienceables) {
            totalExperience += experienceable.getExperience();
        }
        return totalExperience / numOfExperienceables;
    }

    //returns total expenses of expensable objects in a list
    public static double totalExpenses(List<Expensable> expensables) {
        double total = 0;
        for (Expensable expensable : expensables) {
            total += expensable.getExpenses();
        }
        return total;
    }

    //finds operation and ends operation
    public void endOperation(String codeName) {
        Operation currentOperation = null;
        List<Vehicle> currentVehicles = null;
        List<Agent> currentAgents = new ArrayList<>();
        List<Expensable> expensables = new ArrayList<>();
        int numberOfAgents = 0;

        for (Operation openOperation : openOperations) {
            if (codeName.equals(openOperation.getCodeName())) {
                currentOperation = openOperation;
                currentVehicles = openOperation.getVehicles();
                for (Vehicle currentVehicle : currentVehicles) {
                    currentAgents.addAll(currentVehicle.getAgents());
                }
                numberOfAgents = currentOperation.getRequiredInvestigatorsCount() + currentOperation.getRequiredDetectivesCount();
                break;
            }
        }

        if (currentOperation == null) { // no operation found under codeName
            System.out.println("The code name you have entered has no open operation.");
            return;
        }

        printEndOfOperationMessage(currentOperation, currentVehicles, currentAgents, expensables, numberOfAgents);

        currentOperation.removeAllAgentsAndVehicles();
        openOperations.remove(currentOperation);
    }

    // prints end of operation message to user
    private void printEndOfOperationMessage(Operation currentOperation, List<Vehicle> currentVehicles, List<Agent> currentAgents, List<Expensable> expensables, int numberOfAgents) {
        expensables.addAll(currentVehicles);
        expensables.addAll(currentAgents);

        System.out.println("Operation Ended:");
        System.out.println("Code Name: '" + currentOperation.getCodeName() + "'");
        System.out.println("Operation: '" + currentOperation.getOperationType() + "'");
        System.out.println("Number of agents in the event: '" + numberOfAgents + "'");
        System.out.println("Agents average years of experience: '" + averageExperience(currentAgents) + "'");
        System.out.println("Operation cost: '" + totalExpenses(expensables) + "'");
        System.out.println("Most expensive vehicle: '" + mostExpensiveVehicle(currentVehicles) + "'");
    }

    // finds the most expensive vehicle id in a list of vehicles
    private int mostExpensiveVehicle(List<Vehicle> vehicles) {
        Vehicle max = vehicles.get(0);
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getExpenses() > max.getExpenses()) {
                max = vehicle;
            }
        }
        return max.getId();
    }
}





