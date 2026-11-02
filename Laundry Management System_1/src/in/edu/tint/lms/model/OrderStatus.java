package in.edu.tint.lms.model;

public class OrderStatus {
	public static String PLACED = "PLACED";
    public static String PROCESSING = "PROCESSING";
    public static String READY = "READY";
    public static String PAID = "PAID";
    public static String CANCELLED = "CANCELLED";

    // statuses staff may set manually (PAID and CANCELLED have their own actions)
    public static String[] PROGRESS = {PLACED, PROCESSING, READY};

    private OrderStatus() {
    }

    public static boolean isProgress(String status) {
        for (String s : PROGRESS) {
            if (s.equals(status)) {
                return true;
            }
        }
        return false;
    }
}
