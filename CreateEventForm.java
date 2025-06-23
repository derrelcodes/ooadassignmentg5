import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDate;

public class CreateEventForm {
    private JFrame frame;
    private JTextField nameField, venueField, capacityField, startTimeField, endTimeField;
    private JComboBox<String> typeBox;
    private JCheckBox hasFeeBox, outsideMMUBox;
    private JTextField earlyBirdLimitField, earlyBirdPriceField, normalPriceField, transportFeeField;
    private JCheckBox provideFoodBox, provideTransportBox;
    private JSpinner dateSpinner;

    public CreateEventForm(String createdBy) {
        frame = new JFrame("Create Event");
        frame.setSize(500, 600);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        nameField = new JTextField();
        venueField = new JTextField();
        capacityField = new JTextField();
        startTimeField = new JTextField();
        endTimeField = new JTextField();
        typeBox = new JComboBox<>(new String[] {"Seminar", "Workshop", "Cultural", "Sports"});
        hasFeeBox = new JCheckBox("Has Registration Fee?");
        outsideMMUBox = new JCheckBox("Venue Outside MMU?");

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        panel.add(new JLabel("Event Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Type:"));
        panel.add(typeBox);
        panel.add(new JLabel("Venue:"));
        panel.add(venueField);
        panel.add(new JLabel("Event Date:"));
        panel.add(dateSpinner);
        panel.add(new JLabel("Start Time (HH:mm):"));
        panel.add(startTimeField);
        panel.add(new JLabel("End Time (HH:mm):"));
        panel.add(endTimeField);
        panel.add(new JLabel("Capacity:"));
        panel.add(capacityField);
        panel.add(outsideMMUBox);
        panel.add(hasFeeBox);

        // Conditional Panel
        JPanel part2Panel = new JPanel();
        part2Panel.setLayout(new BoxLayout(part2Panel, BoxLayout.Y_AXIS));
        earlyBirdLimitField = new JTextField();
        earlyBirdPriceField = new JTextField();
        normalPriceField = new JTextField();
        transportFeeField = new JTextField();
        provideFoodBox = new JCheckBox("Provide Food?");
        provideTransportBox = new JCheckBox("Provide Transport?");

        part2Panel.add(new JLabel("Early Bird Pax Limit:"));
        part2Panel.add(earlyBirdLimitField);
        part2Panel.add(new JLabel("Early Bird Price:"));
        part2Panel.add(earlyBirdPriceField);
        part2Panel.add(new JLabel("Normal Price:"));
        part2Panel.add(normalPriceField);
        part2Panel.add(new JLabel("Transport Fee:"));
        part2Panel.add(transportFeeField);
        part2Panel.add(provideFoodBox);
        part2Panel.add(provideTransportBox);

        part2Panel.setVisible(false);
        panel.add(part2Panel);

        hasFeeBox.addActionListener(e -> updatePart2Visibility(part2Panel));
        outsideMMUBox.addActionListener(e -> updatePart2Visibility(part2Panel));

        JButton createBtn = new JButton("Create Event");
        createBtn.addActionListener(e -> saveEvent());

        panel.add(createBtn);
        frame.add(panel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void updatePart2Visibility(JPanel part2Panel) {
        boolean show = hasFeeBox.isSelected() || outsideMMUBox.isSelected();
        part2Panel.setVisible(show);
        frame.revalidate();
        frame.repaint();
    }

    private void saveEvent() {
        try {
            JSONObject event = new JSONObject();
            event.put("event_id", UUID.randomUUID().toString());
            event.put("name", nameField.getText());
            event.put("type", typeBox.getSelectedItem());
            event.put("venue", venueField.getText());
            event.put("date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(dateSpinner.getValue()));
            event.put("start_time", startTimeField.getText());
            event.put("end_time", endTimeField.getText());
            event.put("capacity", Integer.parseInt(capacityField.getText()));
            event.put("registration_open", true);
            event.put("cancelled", false);
            event.put("participants", new JSONArray());
            event.put("outside_mmu", outsideMMUBox.isSelected());
            event.put("has_fee", hasFeeBox.isSelected());

            if (hasFeeBox.isSelected() || outsideMMUBox.isSelected()) {
                event.put("early_bird_limit", Integer.parseInt(earlyBirdLimitField.getText()));
                event.put("early_bird_price", Double.parseDouble(earlyBirdPriceField.getText()));
                event.put("normal_price", Double.parseDouble(normalPriceField.getText()));
                event.put("transport_fee", Double.parseDouble(transportFeeField.getText()));
                event.put("provides_food", provideFoodBox.isSelected());
                event.put("provides_transportation", provideTransportBox.isSelected());
            }

            File file = new File("data/events.json");
            JSONArray allEvents;
            if (file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath()));
                allEvents = new JSONArray(content);
            } else {
                allEvents = new JSONArray();
            }
            allEvents.put(event);

            FileWriter writer = new FileWriter(file);
            writer.write(allEvents.toString(4));
            writer.close();

            JOptionPane.showMessageDialog(frame, "Event Created Successfully!");
            frame.dispose();
            new ManagementDashboard("management");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error creating event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
