package in.edu.tint.lms.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order implements Comparable<Order> {
    public static double TAX_RATE = 0.05;
    private static int counter = 500;

    private int orderId;
    private Customer customer;
    private Service service;
    private List<OrderItem> items = new ArrayList<>();
    private double discount;
    private LocalDate date = LocalDate.now();
    private String status = OrderStatus.PLACED;
    private Slot slot;
    private Payment payment;

    public Order(Customer customer, Service service) {
        this(customer, service, 0);
    }

    public Order(Customer customer, Service service, double discount) {
        if (discount < 0) {
            throw new IllegalArgumentException("Discount cannot be negative");
        }
        this.orderId = ++counter;
        this.customer = customer;
        this.service = service;
        this.discount = discount;
    }

    public void addItem(String description, int quantity, double rate) {
        if (!OrderStatus.isProgress(status)) {
            throw new IllegalStateException("Items cannot be added to a " + status + " order");
        }
        items.add(new OrderItem(description, quantity, rate));
    }

    public double getSubtotal() {
        double sum = 0;
        for (OrderItem item : items) {
            sum += item.getAmount();
        }
        return sum;
    }

    // overloaded: with a given discount, or the order's own discount
    public double calculateTotal(double discount) {
        double beforeTax = Math.max(0, getSubtotal() + service.getExtraCharge() - discount);
        return beforeTax * (1 + TAX_RATE);
    }

    public double calculateTotal() {
        return calculateTotal(discount);
    }

    public void cancel() {
        if (!OrderStatus.isProgress(status)) {
            throw new IllegalStateException("A " + status + " order cannot be cancelled");
        }
        status = OrderStatus.CANCELLED;
        if (slot != null) {
            slot.releaseSlot();
            slot = null;
        }
    }

    public boolean isCancelled() {
        return OrderStatus.CANCELLED.equals(status);
    }

    public void setStatus(String newStatus) {
        if (!OrderStatus.isProgress(status)) {
            throw new IllegalStateException("A " + status + " order cannot be changed");
        }
        if (!OrderStatus.isProgress(newStatus)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }
        this.status = newStatus;
    }

    public void setPayment(Payment payment) {
        if (!OrderStatus.isProgress(status)) {
            throw new IllegalStateException("A " + status + " order cannot be paid");
        }
        this.payment = payment;
        this.status = OrderStatus.PAID;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public int getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Service getService() {
        return service;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public double getDiscount() {
        return discount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public Slot getSlot() {
        return slot;
    }

    public Payment getPayment() {
        return payment;
    }

    @Override
    public int compareTo(Order other) {
        return Double.compare(calculateTotal(), other.calculateTotal());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Order)) {
            return false;
        }
        return orderId == ((Order) obj).orderId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(orderId);
    }

    @Override
    public String toString() {
        return "Order #" + orderId + " [" + customer.getName() + ", " + service
                + ", " + status + ", Rs. " + String.format("%.2f", calculateTotal()) + "]";
    }

    // nested class: one line of the order
    public static class OrderItem {
        private String description;
        private int quantity;
        private double rate;

        public OrderItem(String description, int quantity, double rate) {
            if (description == null || description.trim().isEmpty()) {
                throw new IllegalArgumentException("Item description is required");
            }
            if (quantity <= 0 || rate <= 0) {
                throw new IllegalArgumentException("Quantity and rate must be greater than zero");
            }
            this.description = description.trim();
            this.quantity = quantity;
            this.rate = rate;
        }

        public double getAmount() {
            return quantity * rate;
        }

        public String getDescription() {
            return description;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getRate() {
            return rate;
        }

        @Override
        public String toString() {
            return quantity + " x " + description;
        }
    }
}