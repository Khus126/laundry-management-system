package in.edu.tint.lms.interfaces;

import in.edu.tint.lms.model.Order;
import in.edu.tint.lms.model.Service;
import in.edu.tint.lms.exception.LMSException.OrderNotFoundException;

public interface StaffManage {

    Order createOrder(int customerId, Service service, double discount);

    void addItem(int orderId, String description, int quantity, double rate)
            throws OrderNotFoundException;

    void updateStatus(int orderId, String status)
            throws OrderNotFoundException;

    void cancelOrder(int orderId)
            throws OrderNotFoundException;

    String pay(int orderId, String mode, String... details)
            throws OrderNotFoundException;
}