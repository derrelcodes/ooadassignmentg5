import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;

public class UpdateEventForm {
    private JFrame frame;
    private String username;
    private String role;
    private JSONObject eventToUpdate;
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    // --- Part 1 fields ---
    private JTextField nameField, venueField, capacityField;
    private JComboBox<String> typeComboBox;
    private JSpinner dateSpinner, startTimeSpinner, endTimeSpinner;
    // --- ADDED: Fields for poster replacement ---
    private JLabel posterFileNameLabel;
    private File selectedPosterFile;


    // --- Part 2 fields ---
    private JCheckBox provideFoodCheckBox, provideTransportCheckBox, hasFeeCheckBox;
    private JTextField transportFeeField, earlyBirdPriceField, earlyBirdLimitField, normalPriceField;

    public UpdateEventForm(JSONObject event, String username, String role) {
        this.eventToUpdate = event;
        this.username = username;
        this.role = role;

        frame = new JFrame("Update Event: " + event.getString("name"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 650);
        frame.setLocationRelativeTo(null);

        JPanel part1Panel = createPart1Panel();
        JPanel part2Panel = createPart2Panel();

        mainPanel.add(part1Panel, "Part1");
        mainPanel.add(part2Panel, "Part2");

        frame.add(mainPanel);
        populateFields();
        cardLayout.show(mainPanel, "Part1");
        frame.setVisible(true);
    }

    private void populateFields() {
        nameField.setText(eventToUpdate.optString("name"));
        venueField.setText(eventToUpdate.optString("venue"));
        typeComboBox.setSelectedItem(eventToUpdate.optString("type"));
        capacityField.setText(String.valueOf(eventToUpdate.optInt("capacity")));

        try {
            dateSpinner.setValue(new SimpleDateFormat("dd/MM/yyyy").parse(eventToUpdate.optString("date")));
            startTimeSpinner.setValue(new SimpleDateFormat("HH:mm").parse(eventToUpdate.optString("start_time")));
            endTimeSpinner.setValue(new SimpleDateFormat("HH:mm").parse(eventToUpdate.optString("end_time")));
        } catch (Exception e) { /* ... */ }

        // --- ADDED: Display current poster file name ---
        String currentPosterPath = eventToUpdate.optString("poster_path", "");
        if (!currentPosterPath.isEmpty()) {
            posterFileNameLabel.setText(new File(currentPosterPath).getName());
        } else {
            posterFileNameLabel.setText("No poster uploaded.");
        }


        provideFoodCheckBox.setSelected(eventToUpdate.optBoolean("provides_food"));
        provideTransportCheckBox.setSelected(eventToUpdate.optBoolean("provides_transportation"));
        hasFeeCheckBox.setSelected(eventToUpdate.optBoolean("has_fee"));

        transportFeeField.setText(String.valueOf(eventToUpdate.optDouble("transport_fee", 0.0)));
        normalPriceField.setText(String.valueOf(eventToUpdate.optDouble("base_price", 0.0)));
        earlyBirdPriceField.setText(String.valueOf(eventToUpdate.optDouble("early_bird_price", 0.0)));
        earlyBirdLimitField.setText(String.valueOf(eventToUpdate.optInt("early_bird_limit", 0)));

        toggleFeeFields();
    }

    private JPanel createPart1Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Update Event (Part 1 of 2)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.weightx = 0.3;
        String[] labels = {"Event Name:", "Type of Event:", "Venue:", "Date:", "Start Time:", "End Time:", "Capacity:", "Event Poster:"};
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
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        gbc.gridy = 3; panel.add(dateSpinner, gbc);

        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));
        gbc.gridy = 4; panel.add(startTimeSpinner, gbc);

        endTimeSpinner = new JSpinner(new SpinnerDateModel());
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));
        gbc.gridy = 5; panel.add(endTimeSpinner, gbc);

        capacityField = new JTextField(5); gbc.gridy = 6; panel.add(capacityField, gbc);
        
        // --- UPDATED: Changed button text to "Replace..." ---
        JPanel posterPanel = new JPanel(new BorderLayout(5, 0));
        JButton replaceButton = new JButton("Replace...");
        posterFileNameLabel = new JLabel();
        posterPanel.add(replaceButton, BorderLayout.WEST);
        posterPanel.add(posterFileNameLabel, BorderLayout.CENTER);
        gbc.gridy = 7; panel.add(posterPanel, gbc);

        replaceButton.addActionListener(e -> selectPosterFile());

        gbc.gridy = 8; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("CANCEL");
        backButton.addActionListener(e -> {
            frame.dispose();
            if ("admin".equals(this.role)) {
                new AdminDashboard(this.username);
            } else {
                new ManagementDashboard(this.username);
            }
        });
        JButton nextButton = new JButton("NEXT");
        nextButton.addActionListener(e -> cardLayout.show(mainPanel, "Part2"));
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);
        panel.add(buttonPanel, gbc);

        return panel;
    }
    
    // --- This method is now used for replacing the poster ---
    private void selectPosterFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a New Event Poster");
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files (JPG, PNG, GIF)", "jpg", "png", "gif");
        fileChooser.addChoosableFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedPosterFile = fileChooser.getSelectedFile();
            posterFileNameLabel.setText(selectedPosterFile.getName());
        }
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
        hasFeeCheckBox = new JCheckBox("Does the event have registration fees?"); gbc.gridy = 2; panel.add(hasFeeCheckBox, gbc);
        panel.add(new JSeparator(), gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 3; gbc.gridx = 0; panel.add(new JLabel("Transportation Fee:"), gbc);
        transportFeeField = new JTextField(); gbc.gridx = 1; panel.add(transportFeeField, gbc);

        gbc.gridy = 4; gbc.gridx = 0; panel.add(new JLabel("Normal Price:"), gbc);
        normalPriceField = new JTextField(); gbc.gridx = 1; panel.add(normalPriceField, gbc);

        gbc.gridy = 5; gbc.gridx = 0; panel.add(new JLabel("Early Bird Price:"), gbc);
        earlyBirdPriceField = new JTextField(); gbc.gridx = 1; panel.add(earlyBirdPriceField, gbc);

        gbc.gridy = 6; gbc.gridx = 0; panel.add(new JLabel("Early Bird Pax Limit:"), gbc);
        earlyBirdLimitField = new JTextField(); gbc.gridx = 1; panel.add(earlyBirdLimitField, gbc);

        hasFeeCheckBox.addActionListener(e -> toggleFeeFields());
        provideTransportCheckBox.addActionListener(e -> toggleFeeFields());

        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("BACK");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Part1"));
        JButton updateButton = new JButton("UPDATE EVENT");
        updateButton.addActionListener(e -> saveUpdatedEvent());
        buttonPanel.add(backButton);
        buttonPanel.add(updateButton);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void toggleFeeFields() {
        if (provideTransportCheckBox.isSelected()) {
            transportFeeField.setEditable(true);
            if (transportFeeField.getText().equals("Not Applicable")) {
                transportFeeField.setText("0");
            }
        } else {
            transportFeeField.setText("Not Applicable");
            transportFeeField.setEditable(false);
        }

        boolean feesEnabled = hasFeeCheckBox.isSelected();
        normalPriceField.setEditable(feesEnabled);
        earlyBirdPriceField.setEditable(feesEnabled);
        earlyBirdLimitField.setEditable(feesEnabled);

        if (!feesEnabled) {
            normalPriceField.setText("Not Applicable");
            earlyBirdPriceField.setText("Not Applicable");
            earlyBirdLimitField.setText("Not Applicable");
        } else {
            if (normalPriceField.getText().equals("Not Applicable")) {
                normalPriceField.setText("0");
                earlyBirdPriceField.setText("0");
                earlyBirdLimitField.setText("0");
            }
        }
    }

    private void saveUpdatedEvent() {
        try {
            eventToUpdate.put("name", nameField.getText());
            eventToUpdate.put("type", typeComboBox.getSelectedItem());
            eventToUpdate.put("venue", venueField.getText());
            eventToUpdate.put("date", new SimpleDateFormat("dd/MM/yyyy").format((Date) dateSpinner.getValue()));
            eventToUpdate.put("start_time", new SimpleDateFormat("HH:mm").format((Date) startTimeSpinner.getValue()));
            eventToUpdate.put("end_time", new SimpleDateFormat("HH:mm").format((Date) endTimeSpinner.getValue()));
            eventToUpdate.put("capacity", Integer.parseInt(capacityField.getText()));

            // --- UPDATED: Handle poster file replacement ---
            if (selectedPosterFile != null) {
                // Delete the old poster if it exists
                String oldPosterPath = eventToUpdate.optString("poster_path");
                if (oldPosterPath != null && !oldPosterPath.isEmpty()) {
                    new File(oldPosterPath).delete();
                }
                
                // Save the new poster
                File postersDir = new File("data/posters");
                if (!postersDir.exists()) postersDir.mkdirs();
                
                String eventId = eventToUpdate.getString("event_id");
                String extension = selectedPosterFile.getName().substring(selectedPosterFile.getName().lastIndexOf("."));
                File destFile = new File(postersDir, eventId + extension);
                
                Files.copy(selectedPosterFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                eventToUpdate.put("poster_path", "data/posters/" + destFile.getName());
            }

            eventToUpdate.put("has_fee", hasFeeCheckBox.isSelected());
            eventToUpdate.put("provides_food", provideFoodCheckBox.isSelected());
            eventToUpdate.put("provides_transportation", provideTransportCheckBox.isSelected());
            eventToUpdate.put("transport_fee", transportFeeField.getText().equals("Not Applicable") ? 0 : Double.parseDouble(transportFeeField.getText()));
            eventToUpdate.put("base_price", normalPriceField.getText().equals("Not Applicable") ? 0 : Double.parseDouble(normalPriceField.getText()));
            eventToUpdate.put("early_bird_price", earlyBirdPriceField.getText().equals("Not Applicable") ? 0 : Double.parseDouble(earlyBirdPriceField.getText()));
            eventToUpdate.put("early_bird_limit", earlyBirdLimitField.getText().equals("Not Applicable") ? 0 : Integer.parseInt(earlyBirdLimitField.getText()));

            JSONArray events = new JSONArray(new String(Files.readAllBytes(Paths.get("data/events.json"))));
            for (int i = 0; i < events.length(); i++) {
                if (events.getJSONObject(i).getString("event_id").equals(eventToUpdate.getString("event_id"))) {
                    events.put(i, eventToUpdate);
                    break;
                }
            }

            try (FileWriter writer = new FileWriter("data/events.json")) {
                writer.write(events.toString(4));
            }

            JOptionPane.showMessageDialog(frame, "Event updated successfully!");
            frame.dispose();
            if ("admin".equals(this.role)) {
                new AdminDashboard(this.username);
            } else {
                new ManagementDashboard(this.username);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error updating event: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}