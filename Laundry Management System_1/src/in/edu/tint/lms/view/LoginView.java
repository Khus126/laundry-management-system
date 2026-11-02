
package in.edu.tint.lms.view;

import in.edu.tint.lms.controller.Main;
import in.edu.tint.lms.exception.LMSException.InvalidLoginException;
import in.edu.tint.lms.model.Admin;
import in.edu.tint.lms.model.Customer;
import in.edu.tint.lms.model.Staff;
import in.edu.tint.lms.model.User;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginView extends BaseView {

    private JComboBox<String> roleBox;

    private JTextField idField = new JTextField(15);
    private JPasswordField passwordField = new JPasswordField(15);

    public LoginView() {
        super("Laundry Management System - Login", 480, 280);

        setLayout(new BorderLayout());

        // Role selection
        roleBox = new JComboBox<>(
            new String[]{"Select Role", "Admin", "Staff", "Customer"}
        );

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));

        form.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 15, 20)
        );

        form.add(new JLabel("Login As:"));
        form.add(roleBox);

        form.add(new JLabel("User ID:"));
        form.add(idField);

        form.add(new JLabel("Password:"));
        form.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> onLogin());

        JButton signUpButton =
            makeButton("Customer Sign Up", this::onSignUp);

        form.add(loginButton);
        form.add(signUpButton);

        add(form, BorderLayout.CENTER);
    }

    private void onLogin() {

        String role = (String) roleBox.getSelectedItem();

        if (role == null || role.equals("Select Role")) {
            showError("Please select your login role.");
            return;
        }

        try {

            int id = Integer.parseInt(idField.getText().trim());

            String password =
                new String(passwordField.getPassword());

            User user = manager.authenticate(id, password);

            // Verify selected role against actual account type
            boolean validRole =
                (role.equals("Admin") && user instanceof Admin)
                || (role.equals("Staff") && user instanceof Staff)
                || (role.equals("Customer") && user instanceof Customer);

            if (!validRole) {
                showError(
                    "This account does not belong to the selected role."
                );
                return;
            }

            closeView();
            Main.openDashboard(user);

        } catch (NumberFormatException ex) {

            showError("User ID must be a valid number.");

        } catch (InvalidLoginException ex) {

            showError(ex.getMessage());
        }
    }

    private void onSignUp() {

        String role = (String) roleBox.getSelectedItem();

        if (!"Customer".equals(role)) {
            showError(
                "Only customers can register using Customer Sign Up."
            );
            return;
        }

        String[] f = askFields(
            "Customer Sign Up",
            "Name",
            "Password (minimum 4 characters)",
            "Phone (10 digits)"
        );

        if (f == null) {
            return;
        }

        try {

            Customer customer =
                manager.registerCustomer(f[0], f[1], f[2]);

            showMessage(
                "Account created successfully!\n"
                + "Your User ID is: " + customer.getId()
                + "\nPlease log in using your new credentials."
            );

        } catch (IllegalArgumentException ex) {

            showError(ex.getMessage());
        }
    }
}
