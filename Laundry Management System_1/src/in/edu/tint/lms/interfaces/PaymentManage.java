package in.edu.tint.lms.interfaces;

public interface PaymentManage {

    String payByCash(int orderId, double amount);

    String payByCard(int orderId, double amount,
                     String cardHolder, String cardNumber);

    String payByUpi(int orderId, double amount, String upiId);
}