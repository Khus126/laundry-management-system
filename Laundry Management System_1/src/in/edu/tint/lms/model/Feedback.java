package in.edu.tint.lms.model;

import java.time.LocalDate;

public class Feedback {
    private String customerName;
    private int orderId;
    private int rating;
    private String comment;
    private LocalDate date = LocalDate.now();

    public Feedback(String customerName, int orderId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        this.customerName = customerName;
        this.orderId = orderId;
        this.rating = rating;
        this.comment = comment.trim();
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDate getDate() {
        return date;
    }
}