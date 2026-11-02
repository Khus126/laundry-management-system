package in.edu.tint.lms.model;

public class Service {
	    public static String NORMAL = "Normal";
	    public static String EXPRESS = "Express";
	    public static double EXPRESS_CHARGE = 50;

	    private String name;
	    private double extraCharge;

	    public Service(String name, double extraCharge) {
	        if (name == null || name.trim().isEmpty()) {
	            throw new IllegalArgumentException("Service name cannot be empty");
	        }
	        if (extraCharge < 0) {
	            throw new IllegalArgumentException("Extra charge cannot be negative");
	        }
	        this.name = name.trim();
	        this.extraCharge = extraCharge;
	    }

	    public String getName() {
	        return name;
	    }

	    public double getExtraCharge() {
	        return extraCharge;
	    }

	    @Override
	    public String toString() {
	        return name;
	    }
	}
