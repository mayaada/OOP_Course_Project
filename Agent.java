public abstract class Agent implements Comparable<Agent>, Expensable, Experienceable{
    private String name;
    private int ID;
    protected int experience;
    private boolean canDrive;
    protected double salary;
    protected boolean isFree;


    public Agent(String name, int ID, int experience, boolean canDrive) throws IDTooLongException{ //agent constructor
        int idLength = String.valueOf(ID).length();
        if (idLength != 5) {
            throw new IDTooLongException();
        }
        this.name = name;
        this.ID = ID;
        this.experience = experience;
        this.canDrive = canDrive;
        this.isFree = true;
    }

    public boolean isCanDrive() { // canDrive getter
        return canDrive;
    }

    public boolean isFree() { //isFree getter
        return isFree;
    }

    @Override
    public double getExpenses(){ // expenses getter
        return salary;
    }

    @Override
    public int compareTo(Agent other) { //compares between two agents by experience level
        return this.experience - other.experience;
    }

    @Override
    public int getExperience() { // experience getter
        return experience;
    }
}

