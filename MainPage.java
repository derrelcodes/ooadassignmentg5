import javax.swing.*;

public class MainPage {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainPage().createAndShowGUI());
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("MMU Event Management Portal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);

        JLabel title = new JLabel("MMU Event Management Portal", JLabel.CENTER);
        title.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));

        JLabel roleLabel = new JLabel("Who are you?", JLabel.CENTER);

        JButton studentBtn = new JButton("STUDENT / STAFF");
        JButton mgmtBtn = new JButton("MANAGEMENT");
        // New button for System Admin
        JButton adminBtn = new JButton("SYSTEM ADMIN");

        studentBtn.addActionListener(e -> new LoginForm("student"));
        mgmtBtn.addActionListener(e -> new LoginForm("management"));
        // Action listener for the new button
        adminBtn.addActionListener(e -> new LoginForm("admin"));

        JPanel btnPanel = new JPanel();
        btnPanel.add(studentBtn);
        btnPanel.add(mgmtBtn);
        btnPanel.add(adminBtn); // Add the button to the panel

        frame.setLayout(new java.awt.GridLayout(3, 1));
        frame.add(title);
        frame.add(roleLabel);
        frame.add(btnPanel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}