
package in.edu.tint.lms.model;

import in.edu.tint.lms.interfaces.PaymentManage;
import java.time.LocalDate;

public class Payment implements PaymentManage {

    public static String CASH = "Cash";
    public static String CARD = "Card";
    public static String UPI = "UPI";

    public static String[] MODES = {CASH, CARD, UPI};

    private int orderId;
    private double amount;
    private String method;
    private LocalDate paidOn;

    // ---------- Cash payment ----------

    @Override
    public String payByCash(int orderId, double amount) {

        checkAmount(amount);

        record(orderId, amount, CASH);

        return receipt("Cash received at counter");
    }

    // ---------- Card payment ----------

    @Override
    public String payByCard(
            int orderId,
            double amount,
            String holder,
            String cardNumber) {

        checkAmount(amount);

        String digits = cardNumber == null
                ? ""
                : cardNumber.replace(" ", "");

        if (holder == null || holder.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Card holder name is required"
            );
        }

        if (!digits.matches("\\d{16}")) {

            throw new IllegalArgumentException(
                    "Card number must be 16 digits"
            );
        }

        record(orderId, amount, CARD);

        return receipt(
                "Card ending " + digits.substring(12)
                + " (" + holder.trim() + ") approved"
        );
    }

    // ---------- UPI payment ----------

    @Override
    public String payByUpi(
            int orderId,
            double amount,
            String upiId) {

        checkAmount(amount);

        if (upiId == null
                || !upiId.matches("[\\w.\\-]+@[\\w]+")) {

            throw new IllegalArgumentException(
                    "Enter a valid UPI ID (example: name@bank)"
            );
        }

        record(orderId, amount, UPI);

        return receipt(
                "UPI payment from " + upiId + " successful"
        );
    }

    // ---------- Validation ----------

    private void checkAmount(double amount) {

        if (amount <= 0) {

            throw new IllegalArgumentException(
                    "Amount must be greater than zero"
            );
        }
    }

    // ---------- Store payment details ----------

    private void record(
            int orderId,
            double amount,
            String method) {

        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.paidOn = LocalDate.now();
    }

    // ---------- Generate receipt ----------

    private String receipt(String detail) {

        return "Receipt | Order " + orderId
                + " | " + method
                + " | Rs. " + String.format("%.2f", amount)
                + " | " + detail;
    }

    // ---------- Getters ----------

    public int getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }

    public LocalDate getPaidOn() {
        return paidOn;
    }
}