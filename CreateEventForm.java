import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

public class CreateEventForm {
    private JFrame frame;
    private String username;
    private String role;
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    // Part 1 fields
    private JTextField nameField, venueField;
    private JComboBox<String> typeComboBox;
    private JSpinner dateSpinner, startTimeSpinner, endTimeSpinner;
    private JCheckBox outsideMmuCheckBox, hasFeeCheckBox;

    // Part 2 fields
    private JCheckBox provideFoodCheckBox, provideTransportCheckBox;
    private JTextField transportFeeField, earlyBirdPriceField, earlyBirdLimitField, normalPriceField, capacityField;

    public CreateEventForm(String username, String role) {
        this.username = username;
        this.role = role;
        frame = new JFrame("Create New Event");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 650);
        frame.setLocationRelativeTo(null);

        JPanel part1Panel = createPart1Panel();
        JPanel part2Panel = createPart2Panel();

        mainPanel.add(part1Panel, "Part1");
        mainPanel.add(part2Panel, "Part2");

        frame.add(mainPanel);
        cardLayout.show(mainPanel, "Part1");
        frame.setVisible(true);
    }

    private JPanel createPart1Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Create Event (Part 1 of 2)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Labels and Fields
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        String[] labels = {"Event Name:", "Type of Event:", "Venue:", "Date:", "Start Time:", "End Time:", "Capacity:"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i;
            panel.add(new JLabel(labels[i]), gbc);
        }

        gbc.gridx = 1;
        gbc.weightx = 0.7;

        nameField = new JTextField(20); gbc.gridy = 0; panel.add(nameField, gbc);
        typeComboBox = new JComboBox<>(new String[]{"Seminar", "Workshop", "Cultural", "Sports"}); gbc.gridy = 1; panel.add(typeComboBox, gbc);
        venueField = new JTextField(20); gbc.gridy = 2; panel.add(venueField, gbc);

        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        gbc.gridy = 3; panel.add(dateSpinner, gbc);

        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));
        gbc.gridy = 4; panel.add(startTimeSpinner, gbc);

        endTimeSpinner = new JSpinner(new SpinnerDateModel());
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));
        gbc.gridy = 5; panel.add(endTimeSpinner, gbc);

        capacityField = new JTextField(5); gbc.gridy = 6; panel.add(capacityField, gbc);

        // Checkboxes
        gbc.gridy = 7; gbc.gridwidth = 2;
        outsideMmuCheckBox = new JCheckBox("Is the venue located outside MMU?");
        panel.add(outsideMmuCheckBox, gbc);

        gbc.gridy = 8;
        hasFeeCheckBox = new JCheckBox("Does the event have registration fees?");
        panel.add(hasFeeCheckBox, gbc);

        // Navigation buttons
        gbc.gridy = 9; gbc.anchor = GridBagConstraints.EAST;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("BACK");
        backButton.addActionListener(e -> {
            frame.dispose();
            if ("admin".equals(role)) {
                new AdminDashboard(username);
            } else {
                new ManagementDashboard(username);
            }
        });
        JButton nextButton = new JButton("NEXT");
        nextButton.addActionListener(e -> cardLayout.show(mainPanel, "Part2"));
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createPart2Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Create Event (Part 2 of 2)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridwidth = 2; gbc.gridx = 0;

        provideFoodCheckBox = new JCheckBox("Is food provided for participants?"); gbc.gridy = 0; panel.add(provideFoodCheckBox, gbc);
        provideTransportCheckBox = new JCheckBox("Is transportation provided for participants?"); gbc.gridy = 1; panel.add(provideTransportCheckBox, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Transportation Fee:"), gbc);
        transportFeeField = new JTextField("0"); gbc.gridx = 1; panel.add(transportFeeField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Normal Price:"), gbc);
        normalPriceField = new JTextField("0"); gbc.gridx = 1; panel.add(normalPriceField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Early Bird Price:"), gbc);
        earlyBirdPriceField = new JTextField("0"); gbc.gridx = 1; panel.add(earlyBirdPriceField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; panel.add(new JLabel("Early Bird Pax Limit:"), gbc);
        earlyBirdLimitField = new JTextField("0"); gbc.gridx = 1; panel.add(earlyBirdLimitField, gbc);

        // Action listeners to enable/disable fields
        hasFeeCheckBox.addActionListener(e -> toggleFeeFields());
        provideTransportCheckBox.addActionListener(e -> toggleFeeFields());
        toggleFeeFields(); // Initial call

        // Navigation buttons
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("BACK");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Part1"));
        JButton continueButton = new JButton("CREATE EVENT");
        continueButton.addActionListener(e -> saveEvent());
        buttonPanel.add(backButton);
        buttonPanel.add(continueButton);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void toggleFeeFields() {
        boolean feesEnabled = hasFeeCheckBox.isSelected();
        normalPriceField.setEnabled(feesEnabled);
        earlyBirdPriceField.setEnabled(feesEnabled);
        earlyBirdLimitField.setEnabled(feesEnabled);

        boolean transportEnabled = provideTransportCheckBox.isSelected();
        transportFeeField.setEnabled(transportEnabled);
    }

    private void saveEvent() {
        try {
            JSONObject newEvent = new JSONObject();
            newEvent.put("event_id", "EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            newEvent.put("name", nameField.getText());
            newEvent.put("type", (String) typeComboBox.getSelectedItem());
            newEvent.put("venue", venueField.getText());
            newEvent.put("date", new SimpleDateFormat("yyyy-MM-dd").format((Date) dateSpinner.getValue()));
            newEvent.put("start_time", new SimpleDateFormat("HH:mm").format((Date) startTimeSpinner.getValue()));
            newEvent.put("end_time", new SimpleDateFormat("HH:mm").format((Date) endTimeSpinner.getValue()));
            newEvent.put("capacity", Integer.parseInt(capacityField.getText()));
            newEvent.put("has_fee", hasFeeCheckBox.isSelected());

            newEvent.put("provides_food", provideFoodCheckBox.isSelected());
            newEvent.put("provides_transportation", provideTransportCheckBox.isSelected());
            newEvent.put("transport_fee", Double.parseDouble(transportFeeField.getText()));
            newEvent.put("base_price", Double.parseDouble(normalPriceField.getText()));
            newEvent.put("early_bird_price", Double.parseDouble(earlyBirdPriceField.getText()));
            newEvent.put("early_bird_limit", Integer.parseInt(earlyBirdLimitField.getText()));

            newEvent.put("createdBy", this.username);

            newEvent.put("registration_open", true);
            newEvent.put("cancelled", false);
            newEvent.put("participants", new JSONArray());

            JSONArray events = new JSONArray(new String(Files.readAllBytes(Paths.get("data/events.json"))));
            events.put(newEvent);

            try (FileWriter writer = new FileWriter("data/events.json")) {
                writer.write(events.toString(4));
            }

            JOptionPane.showMessageDialog(frame, "Event created successfully!");
            frame.dispose();
            if ("admin".equals(role)) {
                new AdminDashboard(username);
            } else {
                new ManagementDashboard(username);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error creating event: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}