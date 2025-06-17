import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import org.json.*;

public class SignUpForm {
    public SignUpForm(String role) {
        JFrame frame = new JFrame("Create Account - " + role.toUpperCase());
        frame.setSize(500, 300);
        JPanel panel = new JPanel(new java.awt.GridLayout(5, 2));

        JTextField nameField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton createBtn = new JButton("Create Account");
        JButton backBtn = new JButton("Back");

        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(createBtn);
        panel.add(backBtn);

        createBtn.addActionListener(e -> {
            if (addUser(nameField.getText(), usernameField.getText(), new String(passwordField.getPassword()), role)) {
                JOptionPane.showMessageDialog(frame, "Account Created Successfully!");
                frame.dispose();
                new LoginForm(role);
            } else {
                JOptionPane.showMessageDialog(frame, "Username already exists.");
            }
        });

        backBtn.addActionListener(e -> {
            frame.dispose();
            new LoginForm(role);
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

            FileWriter writer = new FileWriter(file);
            writer.write(users.toString(4));
            writer.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
