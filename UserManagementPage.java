import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;

public class UserManagementPage {
    private JFrame frame;
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JSONArray users;

    public UserManagementPage() {
        frame = new JFrame("User Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("System Users", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        frame.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Username", "Full Name", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0);
        usersTable = new JTable(tableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frame.add(new JScrollPane(usersTable), BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createUserButton = new JButton("Create User");
        JButton deleteUserButton = new JButton("Delete Selected User");
        
        footerPanel.add(createUserButton);
        footerPanel.add(deleteUserButton);
        frame.add(footerPanel, BorderLayout.SOUTH);

        createUserButton.addActionListener(e -> showCreateUserDialog());
        deleteUserButton.addActionListener(e -> deleteSelectedUser());

        loadUsers();
        frame.setVisible(true);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        try {
            String content = new String(Files.readAllBytes(Paths.get("data/users.json")));
            users = new JSONArray(content);
            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                tableModel.addRow(new Object[]{
                        user.getString("username"),
                        user.getString("fullname"),
                        user.getString("role")
                });
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to load users.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showCreateUserDialog() {
        String username = JOptionPane.showInputDialog(frame, "Enter Username:");
        if (username == null || username.trim().isEmpty()) return;

        String fullname = JOptionPane.showInputDialog(frame, "Enter Full Name:");
        String password = JOptionPane.showInputDialog(frame, "Enter Password:");
        String[] roles = {"student", "management", "admin"};
        String role = (String) JOptionPane.showInputDialog(frame, "Select Role:", "Create User",
                JOptionPane.QUESTION_MESSAGE, null, roles, roles[0]);

        if (role == null) return;

        JSONObject newUser = new JSONObject();
        newUser.put("username", username);
        newUser.put("fullname", fullname);
        newUser.put("password", password);
        newUser.put("role", role);
        users.put(newUser);
        saveUsersToFile();
    }

    private void deleteSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(frame, "Please select a user to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this user?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            users.remove(selectedRow);
            saveUsersToFile();
        }
    }

    private void saveUsersToFile() {
        try (FileWriter writer = new FileWriter("data/users.json")) {
            writer.write(users.toString(4));
            JOptionPane.showMessageDialog(frame, "User data updated successfully.");
            loadUsers();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving user data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}