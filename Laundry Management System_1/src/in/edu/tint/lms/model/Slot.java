package in.edu.tint.lms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** One time slot on one machine. */
public class Slot implements Comparable<Slot> {
    public static String AVAILABLE = "AVAILABLE";
    public static String RESERVED = "RESERVED";
    public static String CANCELLED = "CANCELLED";

    private static int counter = 0;

    private int slotId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status = AVAILABLE;
    private Machine machine;
    private int orderId; // order that holds this slot (0 when nobody does)

    public Slot(Machine machine, LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (machine == null || date == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("Machine, date, start time and end time are required");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        this.slotId = ++counter;
        this.machine = machine;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // free, machine working, and the slot has not started yet
    public boolean checkAvailability() {
        return AVAILABLE.equals(status)
                && machine.isAvailable()
                && !LocalDateTime.of(date, startTime).isBefore(LocalDateTime.now());
    }

    public void reserveSlot(int orderId) {
        if (!checkAvailability()) {
            throw new IllegalStateException("Slot #" + slotId + " is not available");
        }
        this.status = RESERVED;
        this.orderId = orderId;
    }

    // withdraws the slot itself (for example when the machine breaks down)
    public void cancelSlot() {
        if (CANCELLED.equals(status)) {
            throw new IllegalStateException("Slot #" + slotId + " is already cancelled");
        }
        this.status = CANCELLED;
        this.orderId = 0;
    }

    // frees a reserved slot so someone else can use it
    public void releaseSlot() {
        if (RESERVED.equals(status)) {
            this.status = AVAILABLE;
            this.orderId = 0;
        }
    }

    public boolean overlaps(Slot other) {
        return machine.equals(other.machine)
                && date.equals(other.date)
                && !CANCELLED.equals(status)
                && !CANCELLED.equals(other.status)
                && startTime.isBefore(other.endTime)
                && other.startTime.isBefore(endTime);
    }

    public int getSlotId() {
        return slotId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public Machine getMachine() {
        return machine;
    }

    public int getOrderId() {
        return orderId;
    }

    @Override
    public int compareTo(Slot other) {
        int byDate = date.compareTo(other.date);
        if (byDate != 0) {
            return byDate;
        }
        int byStart = startTime.compareTo(other.startTime);
        if (byStart != 0) {
            return byStart;
        }
        return Integer.compare(slotId, other.slotId);
    }

    @Override
    public String toString() {
        return "Slot #" + slotId + " " + machine.getName() + " " + date + " "
                + startTime + "-" + endTime;
    }
}