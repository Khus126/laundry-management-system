package in.edu.tint.lms.model;

public class Customer extends User {

    private String phone;

    public Customer(String name, String password, String phone) {
        super(name, password);

        if (phone == null || !phone.matches("\\d{10}")) {
            throw new IllegalArgumentException(
                    "Phone number must be 10 digits");
        }

        this.phone = phone;
    }

    @Override
    public String getRole() {
        return "Customer";
    }

    public String getPhone() {
        return phone;
    }
}