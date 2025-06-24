import javax.swing.*;
import java.nio.file.*;
import org.json.*;

public class LoginForm {
    public LoginForm(String role) {
        JFrame frame = new JFrame("Login - " + role.toUpperCase());
        frame.setSize(500, 300);
        JPanel panel = new JPanel(new java.awt.GridLayout(4, 2));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        JButton signUpBtn = new JButton("Create Account");

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginBtn);
        panel.add(signUpBtn);

        loginBtn.addActionListener(e -> {
            if (validateUser(usernameField.getText(), new String(passwordField.getPassword()), role)) {
                frame.dispose();
                if (role.equals("student")) {
                    new StudentDashboard(usernameField.getText());
                } else {
                    // Pass both username and role to the dashboard
                    new ManagementDashboard(usernameField.getText(), role);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials or role");
            }
        });

        signUpBtn.addActionListener(e -> {
            frame.dispose();
            new SignUpForm(role);
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
                if (user.getString("username").equals(username) &&
                    user.getString("password").equals(password) &&
                    user.getString("role").equals(role)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}