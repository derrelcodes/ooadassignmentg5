import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import org.json.*;

public class LoginForm {
    public LoginForm(String role) {
        JFrame frame = new JFrame("Login - " + role.toUpperCase());
        frame.setSize(500, 300);
        // --- UPDATED: Switched to GridBagLayout for more flexibility ---
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginBtn = new JButton("Login");

        // Username Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2; // Give some weight to the label column
        panel.add(new JLabel("Username:"), gbc);

        // Username Field
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.8; // Give more weight to the field column
        panel.add(usernameField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        // Password Field
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);


        // --- UPDATED: Conditional button layout ---
        if (!"admin".equalsIgnoreCase(role)) {
            // For Student/Staff/Management, show both buttons side-by-side
            JButton signUpBtn = new JButton("Create Account");
            
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(loginBtn, gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 2;
            panel.add(signUpBtn, gbc);

            signUpBtn.addActionListener(e -> {
                frame.dispose();
                new SignUpForm(role);
            });
        } else {
            // For Admin, show only the login button, centered
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2; // Make the button span two columns
            gbc.anchor = GridBagConstraints.CENTER; // Center it within the spanned columns
            gbc.fill = GridBagConstraints.NONE; // Don't stretch the button
            panel.add(loginBtn, gbc);
        }


        loginBtn.addActionListener(e -> {
            String enteredUsername = usernameField.getText();
            String enteredPassword = new String(passwordField.getPassword());
            
            if (validateUser(enteredUsername, enteredPassword, role)) {
                frame.dispose();
                // For login, the "student" role is used for both students and staff
                String dashboardRole = (role.equals("staff")) ? "student" : role;

                switch (dashboardRole) {
                    case "student":
                        new StudentDashboard(enteredUsername);
                        break;
                    case "management":
                        new ManagementDashboard(enteredUsername);
                        break;
                    case "admin":
                        new AdminDashboard(enteredUsername);
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials or role");
            }
        });

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private boolean validateUser(String username, String password, String role) {
        try {
            String content = new String(Files.readAllBytes(Paths.get("data/users.json")));
            JSONArray users = new JSONArray(content);
            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                
                // If logging in as "student", check for both "student" and "staff" roles in the JSON
                if (role.equals("student")) {
                    String userRole = user.getString("role");
                    if ((userRole.equals("student") || userRole.equals("staff")) &&
                        user.getString("username").equals(username) &&
                        user.getString("password").equals(password)) {
                        return true;
                    }
                } 
                // For other roles (management, admin), match exactly
                else {
                    if (user.getString("username").equals(username) &&
                        user.getString("password").equals(password) &&
                        user.getString("role").equals(role)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}