package in.edu.tint.lms.controller;

import in.edu.tint.lms.model.Machine;
import in.edu.tint.lms.model.MaintenanceRequest;
import in.edu.tint.lms.model.Staff;
import in.edu.tint.lms.model.User;
import java.util.ArrayList;
import java.util.List;

/** Controls maintenance operations and the working status of machines. */
public class MaintenanceManager {
    private List<MaintenanceRequest> requests;
    private List<Machine> machines;
    private List<User> users;

    public MaintenanceManager(List<MaintenanceRequest> requests, List<Machine> machines,
                              List<User> users) {
        this.requests = requests;
        this.machines = machines;
        this.users = users;
    }

    public MaintenanceRequest createRequest(int machineId, String issueDescription,
                                            User reportedBy) {
        MaintenanceRequest request =
                new MaintenanceRequest(findMachine(machineId), issueDescription, reportedBy);
        requests.add(request);
        return request;
    }

    public List<MaintenanceRequest> viewRequests() {
        return new ArrayList<>(requests);
    }

    public List<MaintenanceRequest> viewRequestsOf(Staff staff) {
        List<MaintenanceRequest> result = new ArrayList<>();
        for (MaintenanceRequest request : requests) {
            if (staff.equals(request.getAssignedStaff())) {
                result.add(request);
            }
        }
        return result;
    }

    public void assignMaintenance(int requestId, int staffId) {
        findRequest(requestId).assignStaff(findStaff(staffId));
    }

    public void updateMaintenanceStatus(int requestId, String status) {
        findRequest(requestId).updateStatus(status);
    }

    // a staff member may only update requests assigned to him or her
    public void updateMaintenanceStatusFor(Staff staff, int requestId, String status) {
        MaintenanceRequest request = findRequest(requestId);
        if (!staff.equals(request.getAssignedStaff())) {
            throw new IllegalArgumentException("Request #" + requestId + " is not assigned to you");
        }
        request.updateStatus(status);
    }

    public List<Machine> viewMachines() {
        return new ArrayList<>(machines);
    }

    public void markMachineAvailable(int machineId) {
        Machine machine = findMachine(machineId);
        for (MaintenanceRequest request : requests) {
            if (request.getMachine().equals(machine) && request.isOpen()) {
                throw new IllegalStateException("Complete maintenance request #"
                        + request.getRequestId() + " before marking " + machine.getName()
                        + " available");
            }
        }
        machine.markAvailable();
    }

    public void markMachineOutOfService(int machineId) {
        findMachine(machineId).markOutOfService();
    }

    public int countMachinesOutOfService() {
        int count = 0;
        for (Machine machine : machines) {
            if (!machine.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    public int countOpenRequests() {
        int count = 0;
        for (MaintenanceRequest request : requests) {
            if (request.isOpen()) {
                count++;
            }
        }
        return count;
    }

    private Machine findMachine(int machineId) {
        for (Machine machine : machines) {
            if (machine.getMachineId() == machineId) {
                return machine;
            }
        }
        throw new IllegalArgumentException("No machine with ID " + machineId);
    }

    private MaintenanceRequest findRequest(int requestId) {
        for (MaintenanceRequest request : requests) {
            if (request.getRequestId() == requestId) {
                return request;
            }
        }
        throw new IllegalArgumentException("No maintenance request with ID " + requestId);
    }

    private Staff findStaff(int staffId) {
        for (User user : users) {
            if (user instanceof Staff && user.getId() == staffId) {
                return (Staff) user;
            }
        }
        throw new IllegalArgumentException("No staff member with ID " + staffId);
    }
}