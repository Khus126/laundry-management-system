package in.edu.tint.lms.model;

public class Staff extends User {

    public Staff(String name, String password) {
        super(name, password);
    }

    @Override
    public String getRole() {
        return "Staff";
    }
}