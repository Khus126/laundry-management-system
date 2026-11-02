package in.edu.tint.lms.exception;

public class LMSException extends Exception {

	public LMSException(String message) {
        super(message);
    }

    public static class InvalidLoginException extends LMSException {
		public InvalidLoginException(String message) {
            super(message);
        }
    }

    public static class OrderNotFoundException extends LMSException {
		public OrderNotFoundException(String message) {
            super(message);
        }
    }

    public static class SlotUnavailableException extends LMSException {
		public SlotUnavailableException(String message) {
            super(message);
        }
    }
}