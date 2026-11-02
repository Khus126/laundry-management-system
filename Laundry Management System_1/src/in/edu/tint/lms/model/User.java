package in.edu.tint.lms.model;

public abstract class User {
	    private static int counter = 1000;

	    private int id;
	    private String name;
	    private String password;

	    protected User(String name, String password) {
	        setName(name);
	        setPassword(password);
	        this.id = ++counter;
	    }

	    public abstract String getRole();

	    public boolean login(String pwd) {
	        return password.equals(pwd);
	    }

	    public int getId() {
	        return id;
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        if (name == null || name.trim().isEmpty()) {
	            throw new IllegalArgumentException("Name cannot be empty");
	        }
	        this.name = name.trim();
	    }

	    public void setPassword(String password) {
	        if (password == null || password.length() < 4) {
	            throw new IllegalArgumentException("Password must be at least 4 characters");
	        }
	        this.password = password;
	    }

	    @Override
	    public boolean equals(Object obj) {
	        if (this == obj) {
	            return true;
	        }
	        if (!(obj instanceof User)) {
	            return false;
	        }
	        return id == ((User) obj).id;
	    }

	    @Override
	    public int hashCode() {
	        return Integer.hashCode(id);
	    }

	    @Override
	    public String toString() {
	        return getRole() + " #" + id + " (" + name + ")";
	    }
	}
