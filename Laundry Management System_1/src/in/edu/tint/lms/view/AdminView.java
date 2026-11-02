package in.edu.tint.lms.view;

import in.edu.tint.lms.controller.Main;
import in.edu.tint.lms.model.Admin;
import in.edu.tint.lms.model.Feedback;
import in.edu.tint.lms.model.Inventory;
import in.edu.tint.lms.model.Machine;
import in.edu.tint.lms.model.MaintenanceRequest;
import in.edu.tint.lms.model.Slot;
import in.edu.tint.lms.model.Staff;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AdminView extends BaseView {
	private static final long serialVersionUID = 1L;
    private Admin admin;

    public AdminView(Admin admin) {
        super("Admin - " + admin.getName(), 860, 400);
        this.admin = admin;

        layoutButtons(3,
                makeButton("Daily Report", () -> showMessage(manager.dailyReport())),
                makeButton("All Orders", this::onAllOrders),
                makeButton("Add Staff", this::onAddStaff),
                makeButton("View Inventory", this::onViewInventory),
                makeButton("Add Inventory Item", this::onAddInventory),
                makeButton("Update Stock", this::onUpdateStock),
                makeButton("View Machines", this::onViewMachines),
                makeButton("Mark Machine Available", this::onMarkAvailable),
                makeButton("Mark Machine Out of Service", this::onMarkOutOfService),
                makeButton("Create Maintenance Request", this::onCreateRequest),
                makeButton("View Maintenance Requests", this::onViewRequests),
                makeButton("Assign Maintenance", this::onAssign),
                makeButton("Update Request Status", this::onUpdateRequestStatus),
                makeButton("Create Slot", this::onCreateSlot),
                makeButton("View All Slots", this::onViewSlots),
                makeButton("Cancel Slot", this::onCancelSlot),
                makeButton("View Feedback", this::onViewFeedback),
                makeButton("Logout", this::onLogout));
    }

    private void onAllOrders() {
        showTable("All Orders (highest amount first)", ORDER_COLUMNS,
                orderRows(manager.getOrdersSortedByAmount()));
    }

    private void onAddStaff() {
        String[] f = askFields("Add Staff", "Name", "Password (min 4)");
        if (f == null) {
            return;
        }
        Staff staff = manager.addStaff(f[0], f[1]);
        showMessage("Staff added. User ID: " + staff.getId());
    }

    // ---------- inventory ----------

    private void onViewInventory() {
        List<Inventory> items = manager.getInventory();
        Object[][] rows = new Object[items.size()][];
        for (int i = 0; i < items.size(); i++) {
            Inventory item = items.get(i);
            rows[i] = new Object[]{item.getItemName(), item.getQuantity(),
                    item.getReorderLevel(), item.isLow() ? "LOW STOCK" : "OK"};
        }
        showTable("Inventory", new String[]{"Item", "Quantity", "Reorder Level", "Status"}, rows);
    }

    private void onAddInventory() {
        String[] f = askFields("Add Inventory Item", "Item name", "Quantity", "Reorder level");
        if (f == null) {
            return;
        }
        Inventory item = manager.addInventoryItem(f[0], Integer.parseInt(f[1]),
                Integer.parseInt(f[2]));
        showMessage("Added: " + item.getReport());
    }

    private void onUpdateStock() {
        String[] f = askFields("Update Stock", "Item name", "Change (+ add / - use)");
        if (f == null) {
            return;
        }
        manager.updateStock(f[0], Integer.parseInt(f[1]));
        showMessage("Stock updated.");
    }

    // ---------- machines ----------

    private void onViewMachines() {
        showTable("Machines", MACHINE_COLUMNS, machineRows(maintenanceManager.viewMachines()));
    }

    private void onMarkAvailable() {
        Machine machine = chooseMachine();
        if (machine == null) {
            return;
        }
        maintenanceManager.markMachineAvailable(machine.getMachineId());
        showMessage(machine.getName() + " is now available.");
    }

    private void onMarkOutOfService() {
        Machine machine = chooseMachine();
        if (machine == null) {
            return;
        }
        maintenanceManager.markMachineOutOfService(machine.getMachineId());
        showMessage(machine.getName() + " is now out of service.");
    }

    // ---------- maintenance ----------

    private void onCreateRequest() {
        Machine machine = chooseMachine();
        if (machine == null) {
            return;
        }
        String[] f = askFields("Maintenance Request - " + machine.getName(), "Issue description");
        if (f == null) {
            return;
        }
        MaintenanceRequest request = maintenanceManager.createRequest(machine.getMachineId(), f[0], admin);
        showMessage("Request created: " + request.getReport());
    }

    private void onViewRequests() {
        showTable("Maintenance Requests", REQUEST_COLUMNS,
                requestRows(maintenanceManager.viewRequests()));
    }

    private void onAssign() {
        String[] f = askFields("Assign Maintenance", "Request ID", "Staff ID");
        if (f == null) {
            return;
        }
        maintenanceManager.assignMaintenance(Integer.parseInt(f[0]), Integer.parseInt(f[1]));
        showMessage("Request " + f[0] + " assigned to staff " + f[1] + ".");
    }

    private void onUpdateRequestStatus() {
        String[] f = askFields("Update Request Status", "Request ID");
        if (f == null) {
            return;
        }
        String status = choose("New status for request " + f[0], MaintenanceRequest.UPDATABLE);
        if (status == null) {
            return;
        }
        maintenanceManager.updateMaintenanceStatus(Integer.parseInt(f[0]), status);
        showMessage("Request " + f[0] + " is now " + status + ".");
    }

    // ---------- slots ----------

    private void onCreateSlot() {
        Machine machine = chooseMachine();
        if (machine == null) {
            return;
        }
        String[] f = askFields("Create Slot - " + machine.getName(),
                "Date (yyyy-MM-dd)", "Start time (HH:mm)", "End time (HH:mm)");
        if (f == null) {
            return;
        }
        Slot slot = slotManager.createSlot(machine.getMachineId(), LocalDate.parse(f[0]),
                LocalTime.parse(f[1]), LocalTime.parse(f[2]));
        showMessage("Created: " + slot);
    }

    private void onViewSlots() {
        showTable("All Slots", SLOT_COLUMNS, slotRows(slotManager.viewAllSlots()));
    }

    private void onCancelSlot() {
        String[] f = askFields("Cancel Slot", "Slot ID");
        if (f == null) {
            return;
        }
        if (confirm("Cancel slot " + f[0] + "? An order holding it will lose the reservation.")) {
            slotManager.cancelSlot(Integer.parseInt(f[0]));
            showMessage("Slot " + f[0] + " cancelled.");
        }
    }

    // ---------- feedback and logout ----------

    private void onViewFeedback() {
        List<Feedback> list = manager.getFeedbacks();
        Object[][] rows = new Object[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            Feedback fb = list.get(i);
            rows[i] = new Object[]{fb.getOrderId(), fb.getCustomerName(), fb.getRating(),
                    fb.getComment(), fb.getDate()};
        }
        showTable("Customer Feedback",
                new String[]{"Order ID", "Customer", "Rating", "Comment", "Date"}, rows);
    }

    private void onLogout() {
        closeView();
        Main.showLogin();
    }
}