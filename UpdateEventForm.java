import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;

public class UpdateEventForm {
    private JFrame frame;
    private JSONObject eventToUpdate;
    private JTextField nameField, venueField, capacityField, startTimeField, endTimeField;
    private JComboBox<String> typeBox;
    private JCheckBox hasFeeBox, outsideMMUBox;
    private JTextField earlyBirdLimitField, earlyBirdPriceField, normalPriceField, transportFeeField;
    private JCheckBox provideFoodBox, provideTransportBox;
    private JSpinner dateSpinner;

    public UpdateEventForm(JSONObject event) {
        this.eventToUpdate = event;

        frame = new JFrame("Update Event");
        frame.setSize(500, 600);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        nameField = new JTextField(event.getString("name"));
        venueField = new JTextField(event.getString("venue"));
        capacityField = new JTextField(String.valueOf(event.getInt("capacity")));
        startTimeField = new JTextField(event.getString("start_time"));
        endTimeField = new JTextField(event.getString("end_time"));
        typeBox = new JComboBox<>(new String[] {"Seminar", "Workshop", "Cultural", "Sports"});
        typeBox.setSelectedItem(event.getString("type"));
        hasFeeBox = new JCheckBox("Has Registration Fee?", event.optBoolean("has_fee", false));
        outsideMMUBox = new JCheckBox("Venue Outside MMU?", event.optBoolean("outside_mmu", false));

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        try {
            java.util.Date date = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(event.getString("date"));
            dateSpinner.setValue(date);
        } catch (Exception ignored) {}

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

        JPanel part2Panel = new JPanel();
        part2Panel.setLayout(new BoxLayout(part2Panel, BoxLayout.Y_AXIS));
        earlyBirdLimitField = new JTextField(String.valueOf(event.optInt("early_bird_limit", 0)));
        earlyBirdPriceField = new JTextField(String.valueOf(event.optDouble("early_bird_price", 0)));
        normalPriceField = new JTextField(String.valueOf(event.optDouble("normal_price", 0)));
        transportFeeField = new JTextField(String.valueOf(event.optDouble("transport_fee", 0)));
        provideFoodBox = new JCheckBox("Provide Food?", event.optBoolean("provides_food", false));
        provideTransportBox = new JCheckBox("Provide Transport?", event.optBoolean("provides_transportation", false));

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

        boolean showPart2 = hasFeeBox.isSelected() || outsideMMUBox.isSelected();
        part2Panel.setVisible(showPart2);
        panel.add(part2Panel);

        hasFeeBox.addActionListener(e -> togglePart2(part2Panel));
        outsideMMUBox.addActionListener(e -> togglePart2(part2Panel));

        JButton updateBtn = new JButton("Update Event");
        updateBtn.addActionListener(e -> saveUpdatedEvent());

        panel.add(updateBtn);
        frame.add(panel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void togglePart2(JPanel part2Panel) {
        boolean show = hasFeeBox.isSelected() || outsideMMUBox.isSelected();
        part2Panel.setVisible(show);
        frame.revalidate();
        frame.repaint();
    }

    private void saveUpdatedEvent() {
        try {
            eventToUpdate.put("name", nameField.getText());
            eventToUpdate.put("type", typeBox.getSelectedItem());
            eventToUpdate.put("venue", venueField.getText());
            eventToUpdate.put("date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(dateSpinner.getValue()));
            eventToUpdate.put("start_time", startTimeField.getText());
            eventToUpdate.put("end_time", endTimeField.getText());
            eventToUpdate.put("capacity", Integer.parseInt(capacityField.getText()));
            eventToUpdate.put("outside_mmu", outsideMMUBox.isSelected());
            eventToUpdate.put("has_fee", hasFeeBox.isSelected());

            if (hasFeeBox.isSelected() || outsideMMUBox.isSelected()) {
                eventToUpdate.put("early_bird_limit", Integer.parseInt(earlyBirdLimitField.getText()));
                eventToUpdate.put("early_bird_price", Double.parseDouble(earlyBirdPriceField.getText()));
                eventToUpdate.put("normal_price", Double.parseDouble(normalPriceField.getText()));
                eventToUpdate.put("transport_fee", Double.parseDouble(transportFeeField.getText()));
                eventToUpdate.put("provides_food", provideFoodBox.isSelected());
                eventToUpdate.put("provides_transportation", provideTransportBox.isSelected());
            }

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

            JOptionPane.showMessageDialog(frame, "Event Updated Successfully!");
            frame.dispose();
            new ManagementDashboard("management");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error updating event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
