import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileWriter;

public class UpdateEventForm {
    private JFrame frame;
    private String username;
    private JSONObject eventToUpdate;
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    // Part 1 fields
    private JTextField nameField, venueField, capacityField;
    private JComboBox<String> typeComboBox;
    private JSpinner dateSpinner, startTimeSpinner, endTimeSpinner;
    private JCheckBox outsideMmuCheckBox, hasFeeCheckBox;

    // Part 2 fields
    private JCheckBox provideFoodCheckBox, provideTransportCheckBox;
    private JTextField transportFeeField, earlyBirdPriceField, earlyBirdLimitField, normalPriceField;

    public UpdateEventForm(JSONObject event) {
        this.eventToUpdate = event;
        // Assuming the manager who created it is updating, or any manager can.
        // If you need to track the creator, the username should be stored with the event.
        this.username = "management"; // Placeholder username

        frame = new JFrame("Update Event: " + event.getString("name"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 650);
        frame.setLocationRelativeTo(null);

        JPanel part1Panel = createPart1Panel();
        JPanel part2Panel = createPart2Panel();

        mainPanel.add(part1Panel, "Part1");
        mainPanel.add(part2Panel, "Part2");
        
        populateFields(); // Populate form with existing event data

        frame.add(mainPanel);
        cardLayout.show(mainPanel, "Part1");
        frame.setVisible(true);
    }

    private void populateFields() {
        nameField.setText(eventToUpdate.optString("name"));
        venueField.setText(eventToUpdate.optString("venue"));
        typeComboBox.setSelectedItem(eventToUpdate.optString("type"));
        capacityField.setText(String.valueOf(eventToUpdate.optInt("capacity")));
        
        try {
            dateSpinner.setValue(new SimpleDateFormat("yyyy-MM-dd").parse(eventToUpdate.optString("date")));
            startTimeSpinner.setValue(new SimpleDateFormat("HH:mm").parse(eventToUpdate.optString("start_time")));
            endTimeSpinner.setValue(new SimpleDateFormat("HH:mm").parse(eventToUpdate.optString("end_time")));
        } catch (Exception e) {
            // Set to default if parsing fails
            dateSpinner.setValue(new Date());
            startTimeSpinner.setValue(new Date());
            endTimeSpinner.setValue(new Date());
        }

        outsideMmuCheckBox.setSelected(eventToUpdate.optBoolean("outside_mmu"));
        hasFeeCheckBox.setSelected(eventToUpdate.optBoolean("has_fee"));
        provideFoodCheckBox.setSelected(eventToUpdate.optBoolean("provides_food"));
        provideTransportCheckBox.setSelected(eventToUpdate.optBoolean("provides_transportation"));
        
        transportFeeField.setText(String.valueOf(eventToUpdate.optDouble("transport_fee")));
        normalPriceField.setText(String.valueOf(eventToUpdate.optDouble("base_price")));
        earlyBirdPriceField.setText(String.valueOf(eventToUpdate.optDouble("early_bird_price")));
        earlyBirdLimitField.setText(String.valueOf(eventToUpdate.optInt("early_bird_limit")));
        
        toggleFeeFields(); // Ensure fields are enabled/disabled correctly on load
    }
    
    // createPart1Panel() and createPart2Panel() are nearly identical to CreateEventForm
    // but with different button actions
    private JPanel createPart1Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Update Event (Part 1 of 2)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.weightx = 0.3;
        String[] labels = {"Event Name:", "Type of Event:", "Venue:", "Date:", "Start Time:", "End Time:", "Capacity:"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i; panel.add(new JLabel(labels[i]), gbc);
        }

        gbc.gridx = 1; gbc.weightx = 0.7;
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
        
        gbc.gridy = 7; gbc.gridwidth = 2;
        outsideMmuCheckBox = new JCheckBox("Is the venue located outside MMU?"); panel.add(outsideMmuCheckBox, gbc);
        
        gbc.gridy = 8;
        hasFeeCheckBox = new JCheckBox("Does the event have registration fees?"); panel.add(hasFeeCheckBox, gbc);
        
        gbc.gridy = 9; gbc.anchor = GridBagConstraints.EAST;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("CANCEL");
        backButton.addActionListener(e -> { frame.dispose(); new ManagementDashboard(username); });
        JButton nextButton = new JButton("NEXT");
        nextButton.addActionListener(e -> cardLayout.show(mainPanel, "Part2"));
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createPart2Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Update Event (Part 2 of 2)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridwidth = 2; gbc.gridx = 0;
        provideFoodCheckBox = new JCheckBox("Is food provided for participants?"); gbc.gridy = 0; panel.add(provideFoodCheckBox, gbc);
        provideTransportCheckBox = new JCheckBox("Is transportation provided for participants?"); gbc.gridy = 1; panel.add(provideTransportCheckBox, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Transportation Fee:"), gbc);
        transportFeeField = new JTextField(); gbc.gridx = 1; panel.add(transportFeeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Normal Price:"), gbc);
        normalPriceField = new JTextField(); gbc.gridx = 1; panel.add(normalPriceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Early Bird Price:"), gbc);
        earlyBirdPriceField = new JTextField(); gbc.gridx = 1; panel.add(earlyBirdPriceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; panel.add(new JLabel("Early Bird Pax Limit:"), gbc);
        earlyBirdLimitField = new JTextField(); gbc.gridx = 1; panel.add(earlyBirdLimitField, gbc);
        
        hasFeeCheckBox.addActionListener(e -> toggleFeeFields());
        provideTransportCheckBox.addActionListener(e -> toggleFeeFields());
        
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("BACK");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Part1"));
        JButton continueButton = new JButton("UPDATE EVENT");
        continueButton.addActionListener(e -> saveUpdatedEvent());
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
    
    private void saveUpdatedEvent() {
        try {
            // Update the fields of the existing JSONObject
            eventToUpdate.put("name", nameField.getText());
            eventToUpdate.put("type", typeComboBox.getSelectedItem());
            eventToUpdate.put("venue", venueField.getText());
            eventToUpdate.put("date", new SimpleDateFormat("yyyy-MM-dd").format((Date) dateSpinner.getValue()));
            eventToUpdate.put("start_time", new SimpleDateFormat("HH:mm").format((Date) startTimeSpinner.getValue()));
            eventToUpdate.put("end_time", new SimpleDateFormat("HH:mm").format((Date) endTimeSpinner.getValue()));
            eventToUpdate.put("capacity", Integer.parseInt(capacityField.getText()));
            eventToUpdate.put("has_fee", hasFeeCheckBox.isSelected());
            eventToUpdate.put("provides_food", provideFoodCheckBox.isSelected());
            eventToUpdate.put("provides_transportation", provideTransportCheckBox.isSelected());
            eventToUpdate.put("transport_fee", Double.parseDouble(transportFeeField.getText()));
            eventToUpdate.put("base_price", Double.parseDouble(normalPriceField.getText()));
            eventToUpdate.put("early_bird_price", Double.parseDouble(earlyBirdPriceField.getText()));
            eventToUpdate.put("early_bird_limit", Integer.parseInt(earlyBirdLimitField.getText()));

            // Read all events, find the one with the matching ID, and replace it
            JSONArray events = new JSONArray(new String(Files.readAllBytes(Paths.get("data/events.json"))));
            for (int i = 0; i < events.length(); i++) {
                if (events.getJSONObject(i).getString("event_id").equals(eventToUpdate.getString("event_id"))) {
                    events.put(i, eventToUpdate); // Replace the old object with the updated one
                    break;
                }
            }

            try (FileWriter writer = new FileWriter("data/events.json")) {
                writer.write(events.toString(4));
            }

            JOptionPane.showMessageDialog(frame, "Event updated successfully!");
            frame.dispose();
            new ManagementDashboard(username);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error updating event: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}