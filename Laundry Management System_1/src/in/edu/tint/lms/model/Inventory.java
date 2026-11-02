package in.edu.tint.lms.model;

public class Inventory {
    private String itemName;
    private int quantity;
    private int reorderLevel;

    public Inventory(String itemName, int quantity, int reorderLevel) {
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }
        if (quantity < 0 || reorderLevel < 0) {
            throw new IllegalArgumentException("Quantity and reorder level cannot be negative");
        }
        this.itemName = itemName.trim();
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
    }

    public void adjust(int change) {
        if (quantity + change < 0) {
            throw new IllegalArgumentException("Not enough stock of " + itemName);
        }
        quantity += change;
    }

    public boolean isLow() {
        return quantity <= reorderLevel;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public String getReport() {
        return itemName + ": " + quantity + (isLow() ? " (LOW STOCK)" : " (OK)");
    }
}