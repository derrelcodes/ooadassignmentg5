import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class RegistrationForm {
    private JFrame frame;
    private JSONObject event;
    private String username;

    private JTextField nameField, studentIdField, contactField, emailField;
    private JComboBox<String> dietaryComboBox, transportComboBox;

    public RegistrationForm(String username, JSONObject event) {
        this.username = username;
        this.event = event;

        frame = new JFrame("Let's Register");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 600);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // Header Panel with Event Details
        frame.add(createHeaderPanel(), BorderLayout.NORTH);

        // Center Panel with Registration Form
        frame.add(createFormPanel(), BorderLayout.CENTER);

        // Footer Panel with Navigation Buttons
        frame.add(createFooterPanel(), BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        headerPanel.setBackground(new Color(230, 240, 255));

        JLabel eventTypeLabel = new JLabel(event.optString("type", "EVENT").toUpperCase());
        eventTypeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel eventNameLabel = new JLabel(event.optString("name", "Event Name"));
        eventNameLabel.setFont(new Font("Arial", Font.BOLD, 20));

        String priceText = event.optBoolean("has_fee", false) ? "Price: RM " + String.format("%.2f", event.optDouble("base_price")) : "Price: Free";
        JLabel priceLabel = new JLabel(priceText);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        headerPanel.add(eventTypeLabel);
        headerPanel.add(eventNameLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(new JLabel("Date: " + event.optString("date")));
        headerPanel.add(new JLabel("Time: " + event.optString("start_time", "N/A") + " - " + event.optString("end_time", "N/A")));
        headerPanel.add(new JLabel("Venue: " + event.optString("venue")));
        headerPanel.add(priceLabel);

        if (event.optInt("early_bird_limit", 0) > 0) {
            JLabel earlyBirdLabel = new JLabel("Early Bird Discount: Available");
            earlyBirdLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            earlyBirdLabel.setForeground(Color.BLUE);
            headerPanel.add(earlyBirdLabel);
        }

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Labels
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        String[] labels = {"Name:", "Student ID:", "Contact Number:", "Email:", "Dietary Preferences:", "Need Transportation?"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);
        }

        // Fields
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        
        nameField = new JTextField(15);
        gbc.gridy = 0;
        formPanel.add(nameField, gbc);

        studentIdField = new JTextField(15);
        gbc.gridy = 1;
        formPanel.add(studentIdField, gbc);

        contactField = new JTextField(15);
        gbc.gridy = 2;
        formPanel.add(contactField, gbc);

        emailField = new JTextField(15);
        gbc.gridy = 3;
        formPanel.add(emailField, gbc);

        dietaryComboBox = new JComboBox<>(new String[]{"None", "Vegetarian", "Non-Vegetarian"});
        gbc.gridy = 4;
        formPanel.add(dietaryComboBox, gbc);

        transportComboBox = new JComboBox<>(new String[]{"No", "Yes"});
        gbc.gridy = 5;
        // Hide option if transport is not provided by management
        transportComboBox.setVisible(event.optBoolean("provides_transportation", false));
        formPanel.add(transportComboBox, gbc);

        return formPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(new EmptyBorder(0, 15, 10, 15));

        JButton backButton = new JButton("BACK");
        backButton.addActionListener(e -> {
            frame.dispose();
            new StudentDashboard(username);
        });

        JButton nextButton = new JButton("NEXT");
        nextButton.addActionListener(e -> {
            // Simple validation
            if (nameField.getText().isEmpty() || studentIdField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Name and Student ID are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Collect data into a map
            Map<String, String> userDetails = new HashMap<>();
            userDetails.put("fullName", nameField.getText());
            userDetails.put("studentId", studentIdField.getText());
            userDetails.put("contact", contactField.getText());
            userDetails.put("email", emailField.getText());
            userDetails.put("dietary", (String) dietaryComboBox.getSelectedItem());
            userDetails.put("needsTransport", (String) transportComboBox.getSelectedItem());
            
            frame.dispose();
            // We now pass all the collected details to the next page
            new FeeCalculationPage(username, event, userDetails);
        });
        
        footerPanel.add(backButton);
        footerPanel.add(nextButton);

        return footerPanel;
    }
}