public class Detective extends Agent {
    private boolean weaponLicense;

    // detective constructor
    public Detective(String name, int ID, int experience, boolean canDrive, boolean weaponLicense) throws IDTooLongException {
        super(name, ID, experience, canDrive);
        this.weaponLicense = weaponLicense;
    }

    public void setSalary(int level) { // salary setter
        this.salary = level*experience;
    }

    public boolean isWeaponLicense() { // weaponLicense getter
        return weaponLicense;
    }
}