import javax.swing.*;

public class ManagementDashboard {
    public ManagementDashboard(String username) {
        JFrame frame = new JFrame("Management Dashboard");
        frame.setSize(600, 400);

        JLabel welcome = new JLabel("Welcome, " + username + " (Management)");
        JButton signOut = new JButton("Sign Out");
        signOut.addActionListener(e -> {
            frame.dispose();
            new MainPage().createAndShowGUI();
        });

        JPanel panel = new JPanel(new java.awt.BorderLayout());
        panel.add(welcome, java.awt.BorderLayout.CENTER);
        panel.add(signOut, java.awt.BorderLayout.SOUTH);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
