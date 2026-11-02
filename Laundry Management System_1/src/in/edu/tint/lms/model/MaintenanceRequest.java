package in.edu.tint.lms.model;

import java.time.LocalDate;

/** One maintenance / issue record for one machine. */
public class MaintenanceRequest {
    public static String REPORTED = "REPORTED";
    public static String ASSIGNED = "ASSIGNED";
    public static String IN_PROGRESS = "IN_PROGRESS";
    public static String COMPLETED = "COMPLETED";

    // statuses that can be set with updateStatus (REPORTED is the starting status)
    public static String[] UPDATABLE = {ASSIGNED, IN_PROGRESS, COMPLETED};

    private static int counter = 0;

    private int requestId;
    private Machine machine;
    private String issueDescription;
    private User reportedBy;
    private LocalDate reportedDate;
    private String status;
    private Staff assignedStaff;
    private LocalDate completionDate;

    public MaintenanceRequest(Machine machine, String issueDescription, User reportedBy) {
        if (machine == null || reportedBy == null) {
            throw new IllegalArgumentException("Machine and reporter are required");
        }
        if (issueDescription == null || issueDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Describe the issue");
        }
        this.requestId = ++counter;
        this.machine = machine;
        this.issueDescription = issueDescription.trim();
        this.reportedBy = reportedBy;
        reportIssue();
    }

    // records the issue: sets the date and the REPORTED status (done once, when created)
    public void reportIssue() {
        if (status != null) {
            throw new IllegalStateException("This issue has already been reported");
        }
        this.reportedDate = LocalDate.now();
        this.status = REPORTED;
    }

    public void assignStaff(Staff staff) {
        if (staff == null) {
            throw new IllegalArgumentException("Staff member is required");
        }
        if (COMPLETED.equals(status)) {
            throw new IllegalStateException("Request #" + requestId + " is already completed");
        }
        this.assignedStaff = staff;
        if (REPORTED.equals(status)) {
            this.status = ASSIGNED;
        }
    }

    public void updateStatus(String newStatus) {
        if (COMPLETED.equals(status)) {
            throw new IllegalStateException("Request #" + requestId + " is already completed");
        }
        if (!ASSIGNED.equals(newStatus) && !IN_PROGRESS.equals(newStatus)
                && !COMPLETED.equals(newStatus)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }
        if (assignedStaff == null) {
            throw new IllegalStateException("Assign a staff member first");
        }
        if (COMPLETED.equals(newStatus)) {
            completeMaintenance();
        } else {
            this.status = newStatus;
        }
    }

    public void completeMaintenance() {
        if (COMPLETED.equals(status)) {
            throw new IllegalStateException("Request #" + requestId + " is already completed");
        }
        if (assignedStaff == null) {
            throw new IllegalStateException("Assign a staff member first");
        }
        this.status = COMPLETED;
        this.completionDate = LocalDate.now();
    }

    public boolean isOpen() {
        return !COMPLETED.equals(status);
    }

    public int getRequestId() {
        return requestId;
    }

    public Machine getMachine() {
        return machine;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public User getReportedBy() {
        return reportedBy;
    }

    public LocalDate getReportedDate() {
        return reportedDate;
    }

    public String getStatus() {
        return status;
    }

    public Staff getAssignedStaff() {
        return assignedStaff;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public String getReport() {
        return "#" + requestId + " " + machine.getName() + ": " + issueDescription
                + " [" + status + "]";
    }
}