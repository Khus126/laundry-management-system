package in.edu.tint.lms.view;

import in.edu.tint.lms.controller.LaundryManager;
import in.edu.tint.lms.controller.MaintenanceManager;
import in.edu.tint.lms.controller.SlotManager;
import in.edu.tint.lms.exception.LMSException;
import in.edu.tint.lms.interfaces.Action;
import in.edu.tint.lms.interfaces.View;
import in.edu.tint.lms.model.Machine;
import in.edu.tint.lms.model.MaintenanceRequest;
import in.edu.tint.lms.model.Order;
import in.edu.tint.lms.model.Payment;
import in.edu.tint.lms.model.Service;
import in.edu.tint.lms.model.Slot;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public abstract class BaseView extends JFrame implements View {

    protected static String[] ORDER_COLUMNS = {
        "Order ID", "Customer", "Service", "Items",
        "Status", "Total (Rs.)", "Date", "Slot"
    };

    protected static String[] SLOT_COLUMNS = {
        "Slot ID", "Machine", "Date", "Start",
        "End", "Status", "Order ID"
    };

    protected static String[] MACHINE_COLUMNS = {
        "Machine ID", "Name", "Type", "Status"
    };

    protected static String[] REQUEST_COLUMNS = {
        "Request ID", "Machine", "Issue", "Reported By",
        "Reported On", "Status", "Assigned To", "Completed On"
    };

    protected LaundryManager manager = LaundryManager.getInstance();

    protected SlotManager slotManager = manager.getSlotManager();

    protected MaintenanceManager maintenanceManager =
            manager.getMaintenanceManager();

    // ---------- Constructor ----------

    protected BaseView(String title, int width, int height) {
        super(title);

        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // ---------- View interface methods ----------

    @Override
    public void showView() {
        setVisible(true);
    }

    @Override
    public void closeView() {
        dispose();
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    protected String choose(String message, String[] options) {
        return (String) JOptionPane.showInputDialog(
            this,
            message,
            "Select Option",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options.length > 0 ? options[0] : null
        );
    }
 // Confirmation Dialog
    protected boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(
            this,
            message,
            "Confirmation",
            JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }
    protected Machine chooseMachine() {
        return selectMachine(maintenanceManager.viewMachines());
    }
    // ---------- Exception handling ----------

    protected void guard(Action action) {
        try {
            action.run();
        } catch (Exception e) {
            showError(e.getMessage() == null
                    ? "An unexpected error occurred."
                    : e.getMessage());
        }
    }

    // ---------- Button creation ----------

    protected JButton makeButton(String text, Action action) {
        JButton button = new JButton(text);

        button.addActionListener(e -> guard(action));

        return button;
    }

    // ---------- Layout helpers ----------

    protected void layoutButtons(int columns, JButton... buttons) {
        JPanel panel = new JPanel(new GridLayout(0, columns, 10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        for (JButton button : buttons) {
            panel.add(button);
        }

        add(panel);
    }

    protected JPanel createPanel(int rows, int columns) {
        JPanel panel = new JPanel(
                new GridLayout(rows, columns, 8, 8)
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        return panel;
    }

    // ---------- Input helpers ----------

    protected String askInput(String message) {
        return JOptionPane.showInputDialog(this, message);
    }

    protected String[] askFields(String title, String... labels) {
        JPanel panel = new JPanel(
                new GridLayout(labels.length, 2, 8, 8)
        );

        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            panel.add(new JLabel(labels[i]));

            fields[i] = new JTextField(15);
            panel.add(fields[i]);
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        String[] values = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            values[i] = fields[i].getText().trim();

            if (values[i].isEmpty()) {
                showError(labels[i] + " cannot be empty.");
                return null;
            }
        }

        return values;
    }

    protected int askInt(String message) {
        try {
            return Integer.parseInt(askInput(message).trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Enter a valid number.");
        }
    }

    protected double askDouble(String message) {
        try {
            return Double.parseDouble(askInput(message).trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Enter a valid amount.");
        }
    }

    // ---------- Service selection ----------

    protected Service selectService() {
        String[] options = {
            "Normal",
            "Express"
        };

        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Select laundry service:",
                "Service Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == null) {
            return null;
        }

        if (Service.EXPRESS.equals(choice)) {
            return new Service(
                    Service.EXPRESS,
                    Service.EXPRESS_CHARGE
            );
        }

        return new Service(Service.NORMAL, 0);
    }

    // ---------- Payment selection ----------

    protected String selectPaymentMode() {
        String[] modes = Payment.MODES;

        return (String) JOptionPane.showInputDialog(
                this,
                "Select payment method:",
                "Payment",
                JOptionPane.QUESTION_MESSAGE,
                null,
                modes,
                modes[0]
        );
    }

    protected String[] askPaymentDetails(String mode) {

        if (Payment.CARD.equals(mode)) {
            return askFields(
                    "Card Payment",
                    "Card holder name",
                    "Card number"
            );
        }

        if (Payment.UPI.equals(mode)) {
            return askFields(
                    "UPI Payment",
                    "UPI ID"
            );
        }

        return new String[0];
    }

    // ---------- Machine selection ----------

    protected Machine selectMachine(List<Machine> machines) {
        if (machines == null || machines.isEmpty()) {
            showMessage("No machines available.");
            return null;
        }

        Machine[] options = machines.toArray(new Machine[0]);

        return (Machine) JOptionPane.showInputDialog(
                this,
                "Select machine:",
                "Machine Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
    }

    // ---------- Table display ----------

    protected void showTable(
            String title,
            String[] columns,
            Object[][] rows) {

        if (rows.length == 0) {
            showMessage("No records found.");
            return;
        }

        DefaultTableModel model = new DefaultTableModel(
                rows,
                columns
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int totalWidth = 0;

        for (int c = 0; c < columns.length; c++) {

            int width = table.getTableHeader()
                    .getDefaultRenderer()
                    .getTableCellRendererComponent(
                            table,
                            columns[c],
                            false,
                            false,
                            0,
                            c
                    )
                    .getPreferredSize()
                    .width;

            for (int r = 0; r < rows.length; r++) {
                width = Math.max(
                        width,
                        table.getCellRenderer(r, c)
                                .getTableCellRendererComponent(
                                        table,
                                        table.getValueAt(r, c),
                                        false,
                                        false,
                                        r,
                                        c
                                )
                                .getPreferredSize()
                                .width
                );
            }

            table.getColumnModel()
                    .getColumn(c)
                    .setPreferredWidth(width + 16);

            totalWidth += width + 16;
        }

        JScrollPane scroll = new JScrollPane(table);

        scroll.setPreferredSize(
                new Dimension(
                        Math.min(totalWidth + 20, 900),
                        Math.min(rows.length * 20 + 45, 300)
                )
        );

        JOptionPane.showMessageDialog(
                this,
                scroll,
                title,
                JOptionPane.PLAIN_MESSAGE
        );
    }

    // ---------- Order table data ----------

    protected Object[][] orderRows(List<Order> orders) {

        Object[][] rows = new Object[orders.size()][];

        for (int i = 0; i < orders.size(); i++) {

            Order o = orders.get(i);

            rows[i] = new Object[]{
                o.getOrderId(),
                o.getCustomer().getName(),
                o.getService(),
                o.getItems().size() + " item(s)",
                o.getStatus(),
                String.format("%.2f", o.calculateTotal()),
                o.getDate(),
                o.getSlot() == null ? "-" : o.getSlot().toString()
            };
        }

        return rows;
    }

    // ---------- Machine table data ----------

    protected Object[][] machineRows(List<Machine> machines) {

        Object[][] rows = new Object[machines.size()][];

        for (int i = 0; i < machines.size(); i++) {

            Machine m = machines.get(i);

            rows[i] = new Object[]{
                m.getMachineId(),
                m.getName(),
                m.getType(),
                m.getStatus()
            };
        }

        return rows;
    }

    // ---------- Slot table data ----------

    protected Object[][] slotRows(List<Slot> slots) {

        Object[][] rows = new Object[slots.size()][];

        for (int i = 0; i < slots.size(); i++) {

            Slot s = slots.get(i);

            rows[i] = new Object[]{
                s.getSlotId(),
                s.getMachine().getName(),
                s.getDate(),
                s.getStartTime(),
                s.getEndTime(),
                s.getStatus(),
                s.getOrderId() == 0 ? "-" : s.getOrderId()
            };
        }

        return rows;
    }

    // ---------- Maintenance request table data ----------

    protected Object[][] requestRows(
            List<MaintenanceRequest> requests) {

        Object[][] rows = new Object[requests.size()][];

        for (int i = 0; i < requests.size(); i++) {

            MaintenanceRequest r = requests.get(i);

            rows[i] = new Object[]{
                r.getRequestId(),
                r.getMachine().getName(),
                r.getIssueDescription(),
                r.getReportedBy().getName(),
                r.getReportedDate(),
                r.getStatus(),
                r.getAssignedStaff() == null
                        ? "-"
                        : r.getAssignedStaff().getName(),
                r.getCompletionDate() == null
                        ? "-"
                        : r.getCompletionDate()
            };
        }

        return rows;
    }

    // ---------- Order description ----------

    protected String describe(Order o) {

        StringBuilder sb = new StringBuilder();

        sb.append("Order #")
                .append(o.getOrderId())
                .append(" - ")
                .append(o.getStatus());

        sb.append("\nCustomer : ")
                .append(o.getCustomer().getName());

        sb.append("\nService  : ")
                .append(o.getService());

        sb.append("\nDate     : ")
                .append(o.getDate());

        sb.append("\nItems:");

        for (Order.OrderItem item : o.getItems()) {

            sb.append("\n   ")
                    .append(item)
                    .append(" @ Rs. ")
                    .append(String.format("%.2f", item.getRate()));
        }

        sb.append("\nDiscount : Rs. ")
                .append(String.format("%.2f", o.getDiscount()));

        sb.append("\nTotal (incl. ")
                .append((int) (Order.TAX_RATE * 100))
                .append("% tax): Rs. ")
                .append(String.format("%.2f", o.calculateTotal()));

        sb.append("\nSlot     : ")
                .append(o.getSlot() == null
                        ? "not booked"
                        : o.getSlot());

        return sb.toString();
    }
}