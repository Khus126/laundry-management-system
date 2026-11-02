
package in.edu.tint.lms.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import in.edu.tint.lms.controller.Main;
import in.edu.tint.lms.exception.LMSException.OrderNotFoundException;
import in.edu.tint.lms.exception.LMSException.SlotUnavailableException;
import in.edu.tint.lms.model.Customer;
import in.edu.tint.lms.model.Order;
import in.edu.tint.lms.model.Service;
import in.edu.tint.lms.model.Slot;

public class CustomerView extends BaseView {

    private static final long serialVersionUID = 1L;

    private Customer customer;

    // Fixed laundry price list
    private static final String[] ITEMS = {
        "Blanket",
        "Shirts",
        "Comforter",
        "Pants",
        "Socks",
        "Bedsheets",
        "Curtains",
        "Blazers",
        "Jackets",
        "Tops"
    };

    private static final double[] PRICES = {
        100, 50, 200, 80, 20, 74, 50, 300, 200, 70
    };

    public CustomerView(Customer customer) {
        super("Customer - " + customer.getName(), 520, 300);

        this.customer = customer;

        layoutButtons(2,
            makeButton("Place Order", this::onPlaceOrder),
            makeButton("My Orders", this::onMyOrders),
            makeButton("Track Order", this::onTrackOrder),
            makeButton("Make Payment", this::onMakePayment),
            makeButton("Give Feedback", this::onFeedback),
            makeButton("Logout", this::onLogout)
        );
    }

    // ---------- Multi-item order placement ----------

    private void onPlaceOrder() {

        // Select service
        Service service = selectService();

        if (service == null) {
            return;
        }

        // Create cart table
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Item", "Quantity", "Rate (Rs.)", "Amount (Rs.)"},
            0
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable cartTable = new JTable(model);

        // Item selection
        String[] itemOptions = new String[ITEMS.length];

        for (int i = 0; i < ITEMS.length; i++) {
            itemOptions[i] = ITEMS[i] + " - Rs. " + PRICES[i] + " / piece";
        }

        JComboBox<String> itemBox = new JComboBox<>(itemOptions);

        // Quantity selection
        JSpinner quantitySpinner = new JSpinner(
            new SpinnerNumberModel(1, 1, 1000, 1)
        );

        JButton addButton = new JButton("Add Item");
        JButton removeButton = new JButton("Remove Selected");

        JLabel totalLabel = new JLabel();

        // Calculate subtotal and final total
        Runnable updateTotal = () -> {

            double subtotal = 0;

            for (int i = 0; i < model.getRowCount(); i++) {
                subtotal += (double) model.getValueAt(i, 3);
            }

            double serviceCharge = service.getExtraCharge();
            double taxableAmount = subtotal + serviceCharge;
            double tax = taxableAmount * Order.TAX_RATE;
            double finalTotal = taxableAmount + tax;

            totalLabel.setText(
                "<html>"
                + "Subtotal: Rs. " + String.format("%.2f", subtotal)
                + "<br>Service charge: Rs. "
                + String.format("%.2f", serviceCharge)
                + "<br>GST/Tax (5%): Rs. "
                + String.format("%.2f", tax)
                + "<br><b>Final Total: Rs. "
                + String.format("%.2f", finalTotal)
                + "</b></html>"
            );
        };

        // Add item to cart
        addButton.addActionListener(e -> {

        	int itemIndex = itemBox.getSelectedIndex();


            int quantity = (Integer) quantitySpinner.getValue();

            String selectedItem = ITEMS[itemIndex];


            double rate = PRICES[itemIndex];

            boolean alreadyExists = false;

            // If same item is added again, increase its quantity
            for (int i = 0; i < model.getRowCount(); i++) {

                if (model.getValueAt(i, 0).equals(selectedItem)) {

                    int oldQuantity =
                        (Integer) model.getValueAt(i, 1);

                    int newQuantity = oldQuantity + quantity;

                    model.setValueAt(newQuantity, i, 1);

                    model.setValueAt(
                        newQuantity * rate, i, 3
                    );

                    alreadyExists = true;
                    break;
                }
            }

            // Otherwise add a new row
            if (!alreadyExists) {

                model.addRow(new Object[]{
                    selectedItem,
                    quantity,
                    rate,
                    quantity * rate
                });
            }

            updateTotal.run();

            quantitySpinner.setValue(1);
        });

        // Remove selected item
        removeButton.addActionListener(e -> {

            int selectedRow = cartTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please select an item to remove."
                );
                return;
            }

            model.removeRow(selectedRow);

            updateTotal.run();
        });

        // Layout
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        selectionPanel.add(new JLabel("Laundry Item:"));
        selectionPanel.add(itemBox);

        selectionPanel.add(new JLabel("Quantity:"));
        selectionPanel.add(quantitySpinner);

        selectionPanel.add(addButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(removeButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(totalLabel, BorderLayout.EAST);

        JPanel cartPanel = new JPanel(new BorderLayout(8, 8));

        cartPanel.add(selectionPanel, BorderLayout.NORTH);
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        cartPanel.add(bottomPanel, BorderLayout.SOUTH);

        cartPanel.setPreferredSize(new java.awt.Dimension(700, 350));

        updateTotal.run();

        // Show cart dialog
        int result = JOptionPane.showConfirmDialog(
            this,
            cartPanel,
            "Create Your Laundry Order",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        if (model.getRowCount() == 0) {
            showError("Please add at least one laundry item.");
            return;
        }

        // Convert cart rows into OrderItem objects
        List<Order.OrderItem> orderItems = new ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {

            String description = (String) model.getValueAt(i, 0);

            int quantity = (Integer) model.getValueAt(i, 1);

            double rate = (Double) model.getValueAt(i, 2);

            orderItems.add(
                new Order.OrderItem(description, quantity, rate)
            );
        }

        // Show available slots
        List<Slot> availableSlots = slotManager.viewAvailableSlots();

        if (availableSlots.isEmpty()) {
            showError("No available slots. Please try again later.");
            return;
        }

        String[] slotOptions = new String[availableSlots.size()];

        for (int i = 0; i < availableSlots.size(); i++) {

            Slot slot = availableSlots.get(i);

            slotOptions[i] =
                "Slot ID: " + slot.getSlotId()
                + " | Machine: " + slot.getMachine().getName()
                + " | Date: " + slot.getDate()
                + " | Time: " + slot.getStartTime()
                + " - " + slot.getEndTime();
        }

        String selectedSlot = (String) JOptionPane.showInputDialog(
            this,
            "Select your laundry booking slot:",
            "Available Slots",
            JOptionPane.QUESTION_MESSAGE,
            null,
            slotOptions,
            slotOptions[0]
        );

        if (selectedSlot == null) {
            return;
        }

        int selectedIndex = -1;

        for (int i = 0; i < slotOptions.length; i++) {

            if (slotOptions[i].equals(selectedSlot)) {
                selectedIndex = i;
                break;
            }
        }

        if (selectedIndex == -1) {
            return;
        }

        int slotId = availableSlots.get(selectedIndex).getSlotId();

        // Create order and reserve slot
        try {

            Order order = manager.placeOrderWithSlot(
                customer,
                service,
                orderItems,
                slotId
            );

            showMessage(
                "Order placed successfully!\n\n"
                + describe(order)
            );

        } catch (SlotUnavailableException e) {

            showError(
                "Slot booking failed: " + e.getMessage()
                + "\nPlease try placing your order again."
            );

        } catch (IllegalArgumentException | IllegalStateException e) {

            showError(e.getMessage());
        }
    }

    // ---------- View customer orders ----------

    private void onMyOrders() {
        showTable(
            "My Orders",
            ORDER_COLUMNS,
            orderRows(manager.getOrdersOf(customer))
        );
    }

    // ---------- Track order ----------

    private void onTrackOrder() throws OrderNotFoundException {

        String[] f = askFields("Track Order", "Order ID");

        if (f == null) {
            return;
        }

        showMessage(
            describe(manager.findOrderFor(
                customer,
                Integer.parseInt(f[0])
            ))
        );
    }

    // ---------- Payment ----------

    private void onMakePayment() throws OrderNotFoundException {

        String[] f = askFields("Make Payment", "Order ID");

        if (f == null) {
            return;
        }

        int orderId = Integer.parseInt(f[0]);

        Order order = manager.findOrderFor(customer, orderId);

        if (!confirm(
            "Amount payable: Rs. "
            + String.format("%.2f", order.calculateTotal())
            + "\nProceed?"
        )) {
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

        showMessage(
            manager.payFor(customer, orderId, mode, details)
        );
    }

    // ---------- Feedback ----------

    private void onFeedback() throws OrderNotFoundException {

        String[] f = askFields(
            "Give Feedback",
            "Order ID",
            "Rating (1-5)",
            "Comment"
        );

        if (f == null) {
            return;
        }

        manager.addFeedback(
            customer,
            Integer.parseInt(f[0]),
            Integer.parseInt(f[1]),
            f[2]
        );

        showMessage("Thank you for your feedback!");
    }

    // ---------- Logout ----------

    private void onLogout() {
        closeView();
        Main.showLogin();
    }
}
