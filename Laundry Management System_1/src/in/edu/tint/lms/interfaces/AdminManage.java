package in.edu.tint.lms.interfaces;

import in.edu.tint.lms.model.Staff;
import in.edu.tint.lms.model.Order;
import in.edu.tint.lms.model.Inventory;

import java.util.List;

public interface AdminManage {

    Staff addStaff(String name, String password);

    List<Order> getOrdersSortedByAmount();

    Inventory addInventoryItem(String name, int quantity, int reorderLevel);

    void updateStock(String name, int change);
}
