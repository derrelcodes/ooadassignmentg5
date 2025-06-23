import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;

public class RegistrationForm {
    private JFrame frame;
    private JSONObject event;
    private String username;

    public RegistrationForm(String username, JSONObject event) {
        this.username = username;
        this.event = event;

        frame = new JFrame("Register for Event");
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("Registering for: " + event.getString("name")));
        panel.add(new JLabel("Venue: " + event.getString("venue")));
        panel.add(new JLabel("Date: " + event.getString("date")));
        panel.add(new JLabel("Start Time: " + event.getString("start_time")));
        panel.add(new JLabel("End Time: " + event.getString("end_time")));

        JCheckBox transportOption = new JCheckBox("Require Transportation?");
        JCheckBox dietaryOption = new JCheckBox("Have Dietary Restrictions?");

        // Only show if management provided it
        if (!event.optBoolean("provides_transportation", false)) {
            transportOption.setVisible(false);
        }
        if (!event.optBoolean("provides_food", false)) {
            dietaryOption.setVisible(false);
        }

        panel.add(transportOption);
        panel.add(dietaryOption);

        JButton nextBtn = new JButton("Next");
        nextBtn.addActionListener(e -> {
            frame.dispose();
            new FeeCalculationPage(username, event, transportOption.isSelected(), dietaryOption.isSelected());
        });

        panel.add(Box.createVerticalStrut(20));
        panel.add(nextBtn);

        frame.add(panel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
