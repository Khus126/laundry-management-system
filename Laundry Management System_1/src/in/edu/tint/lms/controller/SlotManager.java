package in.edu.tint.lms.controller;

import in.edu.tint.lms.exception.LMSException.SlotUnavailableException;
import in.edu.tint.lms.model.Machine;
import in.edu.tint.lms.model.Order;
import in.edu.tint.lms.model.Slot;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Handles all slots: creating, viewing, reserving, rescheduling and cancelling. */
public class SlotManager {
    private List<Slot> slots;
    private List<Machine> machines;
    private List<Order> orders;

    public SlotManager(List<Slot> slots, List<Machine> machines, List<Order> orders) {
        this.slots = slots;
        this.machines = machines;
        this.orders = orders;
    }

    public Slot createSlot(int machineId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        Machine machine = findMachine(machineId);
        if (date != null && date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Slot date cannot be in the past");
        }
        Slot slot = new Slot(machine, date, startTime, endTime); // validates the times
        for (Slot existing : slots) {
            if (existing.overlaps(slot)) {
                throw new IllegalArgumentException("Overlaps with " + existing);
            }
        }
        slots.add(slot);
        return slot;
    }

    // only slots that can be reserved right now, earliest first
    public List<Slot> viewAvailableSlots() {
        List<Slot> result = new ArrayList<>();
        for (Slot slot : slots) {
            if (slot.checkAvailability()) {
                result.add(slot);
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<Slot> viewAllSlots() {
        List<Slot> result = new ArrayList<>(slots);
        Collections.sort(result);
        return result;
    }

    public boolean checkAvailability(int slotId) {
        return findSlot(slotId).checkAvailability();
    }

    public Slot reserveSlot(Order order, int slotId) throws SlotUnavailableException {
        if (order.isCancelled()) {
            throw new IllegalStateException("Cannot book a slot for a cancelled order");
        }
        if (order.getSlot() != null) {
            throw new IllegalStateException("Order already has a slot. Use reschedule instead.");
        }
        Slot slot = findSlot(slotId);
        if (!slot.checkAvailability()) {
            throw new SlotUnavailableException(reasonUnavailable(slot));
        }
        slot.reserveSlot(order.getOrderId());
        order.setSlot(slot);
        return slot;
    }

    // moves an order to another slot; the old slot is freed only after the new one is reserved
    public Slot rescheduleSlot(Order order, int newSlotId) throws SlotUnavailableException {
        if (order.isCancelled()) {
            throw new IllegalStateException("Cannot reschedule a cancelled order");
        }
        Slot old = order.getSlot();
        if (old == null) {
            throw new IllegalStateException("Order has no slot to reschedule");
        }
        Slot target = findSlot(newSlotId);
        if (target.getSlotId() == old.getSlotId()) {
            throw new IllegalArgumentException("Order already holds this slot");
        }
        if (!target.checkAvailability()) {
            throw new SlotUnavailableException(reasonUnavailable(target));
        }
        target.reserveSlot(order.getOrderId());
        old.releaseSlot();
        order.setSlot(target);
        return target;
    }

    // removes an order's reservation; the slot becomes available again
    public void releaseSlot(Order order) {
        Slot slot = order.getSlot();
        if (slot == null) {
            throw new IllegalStateException("Order has no slot");
        }
        slot.releaseSlot();
        order.setSlot(null);
    }

    // cancels the slot itself; an order that held it loses the reservation
    public void cancelSlot(int slotId) {
        Slot slot = findSlot(slotId);
        int reservedFor = slot.getOrderId();
        slot.cancelSlot();
        if (reservedFor != 0) {
            for (Order order : orders) {
                if (order.getOrderId() == reservedFor) {
                    order.setSlot(null);
                }
            }
        }
    }

    private Machine findMachine(int machineId) {
        for (Machine machine : machines) {
            if (machine.getMachineId() == machineId) {
                return machine;
            }
        }
        throw new IllegalArgumentException("No machine with ID " + machineId);
    }

    private Slot findSlot(int slotId) {
        for (Slot slot : slots) {
            if (slot.getSlotId() == slotId) {
                return slot;
            }
        }
        throw new IllegalArgumentException("No slot with ID " + slotId);
    }

    private String reasonUnavailable(Slot slot) {
        if (Slot.CANCELLED.equals(slot.getStatus())) {
            return "Slot #" + slot.getSlotId() + " has been cancelled";
        }
        if (Slot.RESERVED.equals(slot.getStatus())) {
            return "Slot #" + slot.getSlotId() + " is already reserved";
        }
        if (!slot.getMachine().isAvailable()) {
            return slot.getMachine().getName() + " is out of service";
        }
        return "Slot #" + slot.getSlotId() + " has already started";
    }
}