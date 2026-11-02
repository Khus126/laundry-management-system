package in.edu.tint.lms.controller;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import in.edu.tint.lms.exception.LMSException.InvalidLoginException;
import in.edu.tint.lms.exception.LMSException.OrderNotFoundException;
import in.edu.tint.lms.exception.LMSException.SlotUnavailableException;
import in.edu.tint.lms.interfaces.AdminManage;
import in.edu.tint.lms.interfaces.CustomerManage;
import in.edu.tint.lms.interfaces.ReportManage;
import in.edu.tint.lms.interfaces.StaffManage;
import in.edu.tint.lms.model.Admin;
import in.edu.tint.lms.model.Customer;
import in.edu.tint.lms.model.Feedback;
import in.edu.tint.lms.model.Inventory;
import in.edu.tint.lms.model.Machine;
import in.edu.tint.lms.model.MaintenanceRequest;
import in.edu.tint.lms.model.Order;
import in.edu.tint.lms.model.OrderStatus;
import in.edu.tint.lms.model.Payment;
import in.edu.tint.lms.model.Service;
import in.edu.tint.lms.model.Slot;
import in.edu.tint.lms.model.Staff;
import in.edu.tint.lms.model.User;


/**
 * Logic controller (Singleton).
 * Handles the main business operations of the Laundry Management System.
 */
public class LaundryManager implements AdminManage, StaffManage,
        CustomerManage, ReportManage {

    private static LaundryManager instance;

    // ArrayList: data that is mostly read
    private List<User> users = new ArrayList<>();
    private List<Feedback> feedbacks = new ArrayList<>();
    private List<Inventory> inventory = new ArrayList<>();
    private List<Machine> machines = new ArrayList<>();
    private List<MaintenanceRequest> maintenanceRequests = new ArrayList<>();

    // Vector: shared data that several counters may update
    private List<Order> orders = new Vector<>();
    private List<Slot> slots = new Vector<>();
    private List<Payment> payments = new Vector<>();

    // Managers for slots and maintenance
    private SlotManager slotManager = new SlotManager(slots, machines, orders);
    private MaintenanceManager maintenanceManager =
            new MaintenanceManager(maintenanceRequests, machines, users);

    private LaundryManager() {
        seedDemoData();
    }

    public static LaundryManager getInstance() {
        if (instance == null) {
            instance = new LaundryManager();
        }
        return instance;
    }

    private void seedDemoData() {
        users.add(new Admin("Admin", "admin123"));
        users.add(new Staff("Staff One", "staff123"));
        users.add(new Customer("Demo Customer", "cust123", "9999999999"));

        inventory.add(new Inventory("Detergent", 50, 10));
        inventory.add(new Inventory("Fabric Softener", 30, 10));
        inventory.add(new Inventory("Hangers", 100, 20));

        Machine washer1 = new Machine("Washer-1", "Washer");
        Machine washer2 = new Machine("Washer-2", "Washer");
        Machine dryer1 = new Machine("Dryer-1", "Dryer");

        machines.add(washer1);
        machines.add(washer2);
        machines.add(dryer1);

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        for (Machine machine : machines) {
            slotManager.createSlot(machine.getMachineId(), tomorrow,
                    LocalTime.of(9, 0), LocalTime.of(10, 0));

            slotManager.createSlot(machine.getMachineId(), tomorrow,
                    LocalTime.of(10, 0), LocalTime.of(11, 0));
        }
    }

    // ---------- Login and accounts ----------

    public User authenticate(int id, String password)
            throws InvalidLoginException {

        User user = findUser(id);

        if (user == null || !user.login(password)) {
            throw new InvalidLoginException("Wrong ID or password");
        }

        return user;
    }

    @Override
    public Customer registerCustomer(String name, String password, String phone) {
        Customer customer = new Customer(name, password, phone);
        users.add(customer);
        return customer;
    }

    @Override
    public Staff addStaff(String name, String password) {
        Staff staff = new Staff(name, password);
        users.add(staff);
        return staff;
    }

    private User findUser(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    // ---------- Orders ----------

    @Override
    public Order createOrder(int customerId, Service service, double discount) {
        User user = findUser(customerId);

        if (!(user instanceof Customer)) {
            throw new IllegalArgumentException(
                    "No customer with ID " + customerId);
        }

        Customer customer = (Customer) user;
        Order order = new Order(customer, service, discount);
        orders.add(order);

        return order;
    }

    @Override
    public Order placeOrder(Customer customer, Service service,
                            String description, int quantity, double rate) {

        Order order = new Order(customer, service);
        order.addItem(description, quantity, rate);
        orders.add(order);

        return order;
    }
    public Order placeOrderWithSlot(
            Customer customer,
            Service service,
            List<Order.OrderItem> items,
            int slotId) throws SlotUnavailableException {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Please add at least one item.");
        }

        // Create order, but do not save it yet
        Order order = new Order(customer, service);

        // Add every item to the same order
        for (Order.OrderItem item : items) {
            order.addItem(
                item.getDescription(),
                item.getQuantity(),
                item.getRate()
            );
        }

        // Reserve slot before saving the order
        slotManager.reserveSlot(order, slotId);

        // Save only after successful reservation
        orders.add(order);

        return order;
    }
    public Order findOrder(int orderId) throws OrderNotFoundException {
        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                return order;
            }
        }

        throw new OrderNotFoundException(
                "No order found with ID " + orderId);
    }

    @Override
    public Order findOrderFor(Customer customer, int orderId)
            throws OrderNotFoundException {

        Order order = findOrder(orderId);

        if (!order.getCustomer().equals(customer)) {
            throw new OrderNotFoundException(
                    "No order found with ID " + orderId);
        }

        return order;
    }

    @Override
    public void addItem(int orderId, String description,
                        int quantity, double rate)
            throws OrderNotFoundException {

        findOrder(orderId).addItem(description, quantity, rate);
    }

    @Override
    public void updateStatus(int orderId, String status)
            throws OrderNotFoundException {

        findOrder(orderId).setStatus(status);
    }

    @Override
    public void cancelOrder(int orderId)
            throws OrderNotFoundException {

        findOrder(orderId).cancel();
    }

    public List<Order> searchOrders(String customerName) {
        List<Order> result = new ArrayList<>();

        for (Order order : orders) {
            if (order.getCustomer().getName().toLowerCase()
                    .contains(customerName.trim().toLowerCase())) {
                result.add(order);
            }
        }

        return result;
    }

    @Override
    public List<Order> getOrdersOf(Customer customer) {
        List<Order> result = new ArrayList<>();

        for (Order order : orders) {
            if (order.getCustomer().equals(customer)) {
                result.add(order);
            }
        }

        return result;
    }

    // Highest amount first
    @Override
    public List<Order> getOrdersSortedByAmount() {
        List<Order> list = new ArrayList<>(orders);
        Collections.sort(list, Collections.reverseOrder());

        return list;
    }

    // ---------- Slot and maintenance managers ----------

    public SlotManager getSlotManager() {
        return slotManager;
    }

    public MaintenanceManager getMaintenanceManager() {
        return maintenanceManager;
    }

    // ---------- Payment ----------

    @Override
    public String pay(int orderId, String mode, String... details)
            throws OrderNotFoundException {

        return processPayment(findOrder(orderId), mode, details);
    }

    @Override
    public String payFor(Customer customer, int orderId,
                         String mode, String... details)
            throws OrderNotFoundException {

        return processPayment(findOrderFor(customer, orderId), mode, details);
    }

    private String processPayment(Order order, String mode, String[] details) {

        if (order.getItems().isEmpty()) {
            throw new IllegalStateException(
                    "Add items to the order before paying");
        }

        if (!OrderStatus.isProgress(order.getStatus())) {
            throw new IllegalStateException(
                    "A " + order.getStatus() + " order cannot be paid");
        }

        int id = order.getOrderId();
        double total = order.calculateTotal();

        Payment payment = new Payment();
        String receipt;

        if (Payment.CASH.equals(mode)) {
            receipt = payment.payByCash(id, total);

        } else if (Payment.CARD.equals(mode)) {
            receipt = payment.payByCard(
                    id, total, details[0], details[1]);

        } else if (Payment.UPI.equals(mode)) {
            receipt = payment.payByUpi(
                    id, total, details[0]);

        } else {
            throw new IllegalArgumentException(
                    "Unknown payment mode: " + mode);
        }

        order.setPayment(payment);
        payments.add(payment);

        return receipt;
    }

    // ---------- Reports ----------

    @Override
    public double dailyRevenue() {
        LocalDate today = LocalDate.now();
        double total = 0;

        for (Payment payment : payments) {
            if (today.equals(payment.getPaidOn())) {
                total += payment.getAmount();
            }
        }

        return total;
    }

    @Override
    public String dailyReport() {
        LocalDate today = LocalDate.now();

        int placed = 0;
        int paid = 0;
        int cancelled = 0;

        for (Order order : orders) {
            if (today.equals(order.getDate())) {
                placed++;
            }

            if (OrderStatus.PAID.equals(order.getStatus())) {
                paid++;
            } else if (order.isCancelled()) {
                cancelled++;
            }
        }

        int lowStock = 0;

        for (Inventory item : inventory) {
            if (item.isLow()) {
                lowStock++;
            }
        }

        return "DAILY REPORT - " + today
                + "\nOrders placed today : " + placed
                + "\nOrders paid (total) : " + paid
                + "\nOrders cancelled    : " + cancelled
                + "\nRevenue today       : Rs. "
                + String.format("%.2f", dailyRevenue())
                + "\nLow stock items     : " + lowStock
                + "\nMachines out of service : "
                + maintenanceManager.countMachinesOutOfService()
                + "\nOpen maintenance requests: "
                + maintenanceManager.countOpenRequests();
    }

    // ---------- Inventory ----------

    @Override
    public Inventory addInventoryItem(String name, int quantity,
                                      int reorderLevel) {

        if (findInventory(name) != null) {
            throw new IllegalArgumentException(
                    name + " already exists. Use stock update.");
        }

        Inventory item = new Inventory(name, quantity, reorderLevel);
        inventory.add(item);

        return item;
    }

    @Override
    public void updateStock(String name, int change) {
        Inventory item = findInventory(name);

        if (item == null) {
            throw new IllegalArgumentException(
                    "No inventory item named " + name);
        }

        item.adjust(change);
    }

    private Inventory findInventory(String name) {
        for (Inventory item : inventory) {
            if (item.getItemName().equalsIgnoreCase(name.trim())) {
                return item;
            }
        }

        return null;
    }

    public List<Inventory> getInventory() {
        return new ArrayList<>(inventory);
    }

    // ---------- Feedback ----------

    @Override
    public Feedback addFeedback(Customer customer, int orderId,
                                int rating, String comment)
            throws OrderNotFoundException {

        findOrderFor(customer, orderId);

        Feedback feedback = new Feedback(
                customer.getName(), orderId, rating, comment);

        feedbacks.add(feedback);

        return feedback;
    }

    public List<Feedback> getFeedbacks() {
        return new ArrayList<>(feedbacks);
    }
}