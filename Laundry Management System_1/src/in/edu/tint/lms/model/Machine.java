package in.edu.tint.lms.model;

public class Machine {
    public static String AVAILABLE = "AVAILABLE";
    public static String OUT_OF_SERVICE = "OUT_OF_SERVICE";

    private static int counter = 0;

    private int machineId;
    private String name;
    private String type;
    private String status = AVAILABLE;

    public Machine(String name, String type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Machine name cannot be empty");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Machine type cannot be empty");
        }
        this.machineId = ++counter;
        this.name = name.trim();
        this.type = type.trim();
    }

    public boolean isAvailable() {
        return AVAILABLE.equals(status);
    }

    public void markAvailable() {
        if (isAvailable()) {
            throw new IllegalStateException(name + " is already available");
        }
        status = AVAILABLE;
    }

    public void markOutOfService() {
        if (!isAvailable()) {
            throw new IllegalStateException(name + " is already out of service");
        }
        status = OUT_OF_SERVICE;
    }

    public int getMachineId() {
        return machineId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Machine)) {
            return false;
        }
        return machineId == ((Machine) obj).machineId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(machineId);
    }

    @Override
    public String toString() {
        return "#" + machineId + " " + name + " (" + type + ") [" + status + "]";
    }
}