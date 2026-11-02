package in.edu.tint.lms.controller;

import in.edu.tint.lms.model.Admin;
import in.edu.tint.lms.model.Customer;
import in.edu.tint.lms.model.Staff;
import in.edu.tint.lms.model.User;
import in.edu.tint.lms.view.AdminView;
import in.edu.tint.lms.view.CustomerView;
import in.edu.tint.lms.view.LoginView;
import in.edu.tint.lms.view.BaseView;
import in.edu.tint.lms.view.StaffView;
import javax.swing.SwingUtilities;

/**
 * Flow controller: starts the application, shows the login screen
 * and opens the correct dashboard for the logged-in user.
 */
public class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::showLogin); // build the GUI on the Swing thread
    }

    public static void showLogin() {
        new LoginView().showView();
    }

    // factory: the user's type decides which screen opens (polymorphism through View)
    public static void openDashboard(User user) {
        BaseView view;
        if (user instanceof Admin) {
            view = new AdminView((Admin) user);
        } else if (user instanceof Staff) {
            view = new StaffView((Staff) user);
        } else {
            view = new CustomerView((Customer) user);
        }
        view.showView();
    }
}