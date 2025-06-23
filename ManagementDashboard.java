// ManagementDashboard.java

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;

public class ManagementDashboard {
    public ManagementDashboard(String username) {
        JFrame frame = new JFrame("Management Dashboard");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("Welcome, " + username + " (Management)", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel eventPanel = new JPanel();
        eventPanel.setLayout(new BoxLayout(eventPanel, BoxLayout.Y_AXIS));

        try {
            String content = new String(Files.readAllBytes(Paths.get("data/events.json")));
            JSONArray events = new JSONArray(content);
            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                JPanel card = new JPanel(new GridLayout(0, 1));
                card.setBorder(BorderFactory.createTitledBorder(event.getString("name")));
                card.add(new JLabel("Type: " + event.getString("type")));
                card.add(new JLabel("Venue: " + event.getString("venue")));
                card.add(new JLabel("Date: " + event.getString("date")));

                JButton updateBtn = new JButton("Update");
                JButton cancelBtn = new JButton("Cancel Event");
                JButton toggleBtn = new JButton(event.optBoolean("registration_open", true) ? "Close Registration" : "Open Registration");
                JButton viewParticipantsBtn = new JButton("View Participants");

                updateBtn.addActionListener(e -> {
                    frame.dispose();
                    new UpdateEventForm(event);
                });

                cancelBtn.addActionListener(e -> {
                    event.put("cancelled", true);
                    saveAllEvents(events);
                    JOptionPane.showMessageDialog(frame, "Event marked as cancelled.");
                    frame.dispose();
                    new ManagementDashboard(username);
                });

                toggleBtn.addActionListener(e -> {
                    event.put("registration_open", !event.optBoolean("registration_open", true));
                    saveAllEvents(events);
                    frame.dispose();
                    new ManagementDashboard(username);
                });

                viewParticipantsBtn.addActionListener(e -> {
                    frame.dispose();
                    new ParticipantListPage(event);
                });

                JPanel buttonPanel = new JPanel();
                buttonPanel.add(updateBtn);
                buttonPanel.add(cancelBtn);
                buttonPanel.add(toggleBtn);
                buttonPanel.add(viewParticipantsBtn);

                card.add(buttonPanel);
                eventPanel.add(card);
                eventPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } catch (Exception e) {
            eventPanel.add(new JLabel("Failed to load events."));
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(eventPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton signOut = new JButton("Sign Out");
        signOut.addActionListener(e -> {
            frame.dispose();
            new MainPage().createAndShowGUI();
        });

        mainPanel.add(signOut, BorderLayout.SOUTH);
        frame.setContentPane(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void saveAllEvents(JSONArray events) {
        try (FileWriter writer = new FileWriter("data/events.json")) {
            writer.write(events.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
