package in.edu.tint.lms.view;

import in.edu.tint.lms.controller.Main;
import in.edu.tint.lms.exception.LMSException.OrderNotFoundException;
import in.edu.tint.lms.exception.LMSException.SlotUnavailableException;
import in.edu.tint.lms.model.Customer;
import in.edu.tint.lms.model.Machine;
import in.edu.tint.lms.model.MaintenanceRequest;
import in.edu.tint.lms.model.Order;
import in.edu.tint.lms.model.OrderStatus;
import in.edu.tint.lms.model.Service;
import in.edu.tint.lms.model.Slot;
import in.edu.tint.lms.model.Staff;

public class StaffView extends BaseView {
    private Staff staff;

    public StaffView(Staff staff) {
        super("Staff - " + staff.getName(), 600, 440);
        this.staff = staff;

        layoutButtons(2,
                makeButton("Create Order", this::onCreateOrder),
                makeButton("Add Item to Order", this::onAddItem),
                makeButton("Update Order Status", this::onUpdateStatus),
                makeButton("Cancel Order", this::onCancelOrder),
                makeButton("Search Orders", this::onSearch),
                makeButton("Register Customer", this::onRegisterCustomer),
                makeButton("Take Payment", this::onTakePayment),
                makeButton("View Available Slots", this::onViewSlots),
                makeButton("Reserve Slot", this::onReserveSlot),
                makeButton("Reschedule Slot", this::onRescheduleSlot),
                makeButton("Release Slot", this::onReleaseSlot),
                makeButton("Report Machine Issue", this::onReportIssue),
                makeButton("My Maintenance Tasks", this::onMyTasks),
                makeButton("Update My Task", this::onUpdateTask),
                makeButton("Logout", this::onLogout));
    }
 // ---------- Customer Registration ----------

    private void onRegisterCustomer() {

        String[] f = askFields(
                "Register New Customer",
                "Customer Name",
                "Password (minimum 4 characters)",
                "Phone Number (10 digits)"
        );

        if (f == null) {
            return;
        }

        String name = f[0].trim();
        String password = f[1];
        String phone = f[2].trim();

        // Validate customer name
        if (name.isEmpty()) {
            showError("Customer name cannot be empty.");
            return;
        }

        // Validate password
        if (password.length() < 4) {
            showError("Password must contain at least 4 characters.");
            return;
        }

        // Validate phone number
        if (!phone.matches("[0-9]{10}")) {
            showError("Phone number must contain exactly 10 digits.");
            return;
        }

        try {
            Customer customer = manager.registerCustomer(
                    name,
                    password,
                    phone
            );

            showMessage(
                    "Customer registered successfully!\n\n"
                    + "Customer Name: " + customer.getName()
                    + "\nCustomer ID: " + customer.getId()
                    + "\nPhone: " + phone
                    + "\n\nShare the Customer ID and password with the customer."
            );

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    // ---------- orders ----------

    private void onCreateOrder() throws OrderNotFoundException {
        String[] f = askFields("Create Order", "Customer ID", "Discount (Rs., 0 for none)",
                "First item", "Quantity", "Rate (Rs.)");
        if (f == null) {
            return;
        }
        int customerId = Integer.parseInt(f[0]);
        double discount = Double.parseDouble(f[1]);
        String description = f[2];
        int quantity = Integer.parseInt(f[3]);
        double rate = Double.parseDouble(f[4]);
        new Order.OrderItem(description, quantity, rate); // validate before creating the order

        Service service = selectService();
        if (service == null) {
            return;
        }
        Order order = manager.createOrder(customerId, service, discount);
        manager.addItem(order.getOrderId(), description, quantity, rate);
        showMessage("Order created.\n\n" + describe(order));
    }

    private void onAddItem() throws OrderNotFoundException {
        String[] f = askFields("Add Item", "Order ID", "Item", "Quantity", "Rate (Rs.)");
        if (f == null) {
            return;
        }
        manager.addItem(Integer.parseInt(f[0]), f[1], Integer.parseInt(f[2]),
                Double.parseDouble(f[3]));
        showMessage("Item added.\n\n" + describe(manager.findOrder(Integer.parseInt(f[0]))));
    }

    private void onUpdateStatus() throws OrderNotFoundException {
        String[] f = askFields("Update Status", "Order ID");
        if (f == null) {
            return;
        }
        int orderId = Integer.parseInt(f[0]);
        manager.findOrder(orderId); // fails early if the order does not exist
        String status = choose("New status for order " + orderId, OrderStatus.PROGRESS);
        if (status == null) {
            return;
        }
        manager.updateStatus(orderId, status);
        showMessage("Order " + orderId + " is now " + status + ".");
    }

    private void onCancelOrder() throws OrderNotFoundException {
        String[] f = askFields("Cancel Order", "Order ID");
        if (f == null) {
            return;
        }
        int orderId = Integer.parseInt(f[0]);
        Order order = manager.findOrder(orderId);
        if (confirm("Cancel " + order + "?")) {
            manager.cancelOrder(orderId);
            showMessage("Order " + orderId + " cancelled.");
        }
    }

    private void onSearch() {
        String[] f = askFields("Search Orders", "Customer name");
        if (f == null) {
            return;
        }
        showTable("Search results", ORDER_COLUMNS, orderRows(manager.searchOrders(f[0])));
    }

    private void onTakePayment() throws OrderNotFoundException {
        String[] f = askFields("Take Payment", "Order ID");
        if (f == null) {
            return;
        }
        int orderId = Integer.parseInt(f[0]);
        Order order = manager.findOrder(orderId);
        if (!confirm("Total to collect: Rs. " + String.format("%.2f", order.calculateTotal())
                + "\nProceed with payment?")) {
            return;
        }
        String mode = selectPaymentMode();
        if (mode == null) {
            return;
        }
        String[] details = askPaymentDetails(mode);
        if (details == null) {
            return;
        }
        showMessage(manager.pay(orderId, mode, details));
    }

    // ---------- slots ----------

    private void onViewSlots() {
        showTable("Available Slots", SLOT_COLUMNS, slotRows(slotManager.viewAvailableSlots()));
    }

    private void onReserveSlot() throws OrderNotFoundException, SlotUnavailableException {
        showTable("Available Slots", SLOT_COLUMNS, slotRows(slotManager.viewAvailableSlots()));
        String[] f = askFields("Reserve Slot", "Order ID", "Slot ID");
        if (f == null) {
            return;
        }
        Slot slot = slotManager.reserveSlot(manager.findOrder(Integer.parseInt(f[0])),
                Integer.parseInt(f[1]));
        showMessage("Reserved: " + slot);
    }

    private void onRescheduleSlot() throws OrderNotFoundException, SlotUnavailableException {
        showTable("Available Slots", SLOT_COLUMNS, slotRows(slotManager.viewAvailableSlots()));
        String[] f = askFields("Reschedule Slot", "Order ID", "New slot ID");
        if (f == null) {
            return;
        }
        Slot slot = slotManager.rescheduleSlot(manager.findOrder(Integer.parseInt(f[0])),
                Integer.parseInt(f[1]));
        showMessage("Rescheduled to: " + slot);
    }

    private void onReleaseSlot() throws OrderNotFoundException {
        String[] f = askFields("Release Slot", "Order ID");
        if (f == null) {
            return;
        }
        if (confirm("Release the slot held by order " + f[0] + "?")) {
            slotManager.releaseSlot(manager.findOrder(Integer.parseInt(f[0])));
            showMessage("Slot released.");
        }
    }

    // ---------- machines and maintenance ----------

    private void onReportIssue() {
        Machine machine = chooseMachine();
        if (machine == null) {
            return;
        }
        String[] f = askFields("Report Issue - " + machine.getName(), "Issue description");
        if (f == null) {
            return;
        }
        MaintenanceRequest request = maintenanceManager.createRequest(machine.getMachineId(), f[0], staff);
        showMessage("Issue reported: " + request.getReport());
    }

    private void onMyTasks() {
        showTable("My Maintenance Tasks", REQUEST_COLUMNS, requestRows(maintenanceManager.viewRequestsOf(staff)));
    }

    private void onUpdateTask() {
        String[] f = askFields("Update My Task", "Request ID");
        if (f == null) {
            return;
        }
        String status = choose("New status for request " + f[0], MaintenanceRequest.UPDATABLE);
        if (status == null) {
            return;
        }
        maintenanceManager.updateMaintenanceStatusFor(staff, Integer.parseInt(f[0]), status);
        showMessage("Request " + f[0] + " is now " + status + ".");
    }

    private void onLogout() {
        closeView();
        Main.showLogin();
    }
}