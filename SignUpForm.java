import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import org.json.*;

public class SignUpForm {
    // Make the role ComboBox a class member so the ActionListener can access it
    private JComboBox<String> roleComboBox;

    public SignUpForm(String userType) {
        JFrame frame = new JFrame("Create Account");
        frame.setSize(500, 300);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton createBtn = new JButton("Create Account");
        JButton backBtn = new JButton("Back");

        // Full Name Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        panel.add(new JLabel("Full Name:"), gbc);

        // Full Name Field
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        panel.add(nameField, gbc);

        // Username Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);

        // Username Field
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(usernameField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        // Password Field
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passwordField, gbc);

        // --- UPDATED: Conditionally add the Role field ---
        // Only show the role selection for Student/Staff sign-ups
        if ("student".equalsIgnoreCase(userType)) {
            roleComboBox = new JComboBox<>(new String[]{"Student", "Staff"});
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(new JLabel("Role:"), gbc);
            gbc.gridx = 1;
            gbc.gridy = 3;
            panel.add(roleComboBox, gbc);
        }

        // Buttons
        gbc.gridy = 4; // Move buttons to the next row
        gbc.gridx = 0;
        panel.add(backBtn, gbc);

        gbc.gridx = 1;
        panel.add(createBtn, gbc);


        createBtn.addActionListener(e -> {
            String selectedRole;
            // Determine the role based on which sign-up form was used
            if (roleComboBox != null) {
                selectedRole = ((String) roleComboBox.getSelectedItem()).toLowerCase();
            } else {
                selectedRole = "management"; // Default for management sign-up
            }

            if (addUser(nameField.getText(), usernameField.getText(), new String(passwordField.getPassword()), selectedRole)) {
                JOptionPane.showMessageDialog(frame, "Account Created Successfully!");
                frame.dispose();
                // UPDATED: Return to the correct login form
                new LoginForm(userType);
            } else {
                JOptionPane.showMessageDialog(frame, "Username already exists.");
            }
        });

        backBtn.addActionListener(e -> {
            frame.dispose();
            // UPDATED: Return to the correct login form
            new LoginForm(userType);
        });

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private boolean addUser(String name, String username, String password, String role) {
        try {
            File file = new File("data/users.json");
            JSONArray users;
            if (file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath()));
                users = new JSONArray(content);
            } else {
                users = new JSONArray();
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            for (int i = 0; i < users.length(); i++) {
                if (users.getJSONObject(i).getString("username").equals(username)) return false;
            }

            JSONObject newUser = new JSONObject();
            newUser.put("fullname", name);
            newUser.put("username", username);
            newUser.put("password", password);
            newUser.put("role", role);
            users.put(newUser);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(users.toString(4));
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}