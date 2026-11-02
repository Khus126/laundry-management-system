package in.edu.tint.lms.interfaces;

import in.edu.tint.lms.model.Customer;
import in.edu.tint.lms.model.Order;
import in.edu.tint.lms.model.Service;
import in.edu.tint.lms.model.Feedback;
import in.edu.tint.lms.exception.LMSException.OrderNotFoundException;

import java.util.List;

public interface CustomerManage {

    Customer registerCustomer(String name, String password, String phone);

    Order placeOrder(Customer customer, Service service,
                     String description, int quantity, double rate);

    List<Order> getOrdersOf(Customer customer);

    Order findOrderFor(Customer customer, int orderId)
            throws OrderNotFoundException;

    String payFor(Customer customer, int orderId,
                  String mode, String... details)
            throws OrderNotFoundException;

    Feedback addFeedback(Customer customer, int orderId,
                         int rating, String comment)
            throws OrderNotFoundException;
}