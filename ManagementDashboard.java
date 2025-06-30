import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;

public class ManagementDashboard {
    private JFrame frame;
    private JPanel eventsPanel;
    private String username;
    private final String role = "management";

    public ManagementDashboard(String username) {
        this.username = username;
        frame = new JFrame("Management Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        frame.add(createHeaderPanel(), BorderLayout.NORTH);

        eventsPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        eventsPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(eventsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        frame.add(scrollPane, BorderLayout.CENTER);

        loadOwnedEvents();
        frame.setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        headerPanel.setBackground(new Color(255, 240, 230));

        JLabel welcomeLabel = new JLabel("WELCOME " + username.toUpperCase() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton createEventButton = new JButton("CREATE EVENT");
        createEventButton.addActionListener(e -> {
            frame.dispose();
            new CreateEventForm(username, role);
        });

        JButton signOutButton = new JButton("SIGN OUT");
        signOutButton.addActionListener(e -> {
            frame.dispose();
            new MainPage().createAndShowGUI();
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setOpaque(false);

        buttonsPanel.add(createEventButton);
        buttonsPanel.add(signOutButton);

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private void loadOwnedEvents() {
        eventsPanel.removeAll();
        try {
            String content = new String(Files.readAllBytes(Paths.get("data/events.json")));
            JSONArray allEvents = new JSONArray(content);

            for (int i = 0; i < allEvents.length(); i++) {
                JSONObject event = allEvents.getJSONObject(i);
                if (this.username.equals(event.optString("createdBy"))) {
                    eventsPanel.add(createEventCard(event));
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to load events from file.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        eventsPanel.revalidate();
        eventsPanel.repaint();
    }

    private JPanel createEventCard(JSONObject event) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        JLabel eventNameLabel = new JLabel(event.optString("name", "Event Name"));
        eventNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        detailsPanel.add(eventNameLabel);
        detailsPanel.add(Box.createVerticalStrut(5));

        detailsPanel.add(new JLabel("Date: " + event.optString("date")));
        detailsPanel.add(new JLabel("Venue: " + event.optString("venue")));
        detailsPanel.add(new JLabel("Capacity: " + event.getJSONArray("participants").length() + " / " + event.getInt("capacity")));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setOpaque(false);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            frame.dispose();
            new UpdateEventForm(event, this.username, this.role);
        });

        boolean isCancelled = event.optBoolean("cancelled", false);
        if (isCancelled) {
            card.setBackground(new Color(255, 220, 220));
            eventNameLabel.setText("<html><s>" + event.optString("name", "Event Name") + "</s> (CANCELLED)</html>");
        }

        JButton toggleRegButton = new JButton(event.optBoolean("registration_open", false) ? "Close Registration" : "Open Registration");
        toggleRegButton.addActionListener(e -> toggleRegistration(event));

        JButton viewParticipantsButton = new JButton("View Participants");
        viewParticipantsButton.addActionListener(e -> new ParticipantListPage(event));

        JButton cancelButton = new JButton("Cancel Event");
        if (isCancelled) {
            cancelButton.setEnabled(false);
            updateButton.setEnabled(false);
            toggleRegButton.setEnabled(false);
        }
        cancelButton.addActionListener(e -> cancelEvent(event));

        buttonPanel.add(updateButton);
        buttonPanel.add(toggleRegButton);
        buttonPanel.add(viewParticipantsButton);
        buttonPanel.add(cancelButton);

        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private void updateEventInJson(JSONObject updatedEvent) {
        try {
            String content = new String(Files.readAllBytes(Paths.get("data/events.json")));
            JSONArray allEvents = new JSONArray(content);
            for (int i = 0; i < allEvents.length(); i++) {
                if (allEvents.getJSONObject(i).getString("event_id").equals(updatedEvent.getString("event_id"))) {
                    allEvents.put(i, updatedEvent);
                    break;
                }
            }
            try (FileWriter writer = new FileWriter("data/events.json")) {
                writer.write(allEvents.toString(4));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving event data.", "File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void toggleRegistration(JSONObject event) {
        boolean currentStatus = event.optBoolean("registration_open", false);
        event.put("registration_open", !currentStatus);
        updateEventInJson(event);
        loadOwnedEvents();
    }

    private void cancelEvent(JSONObject event) {
        int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to cancel this event? This action cannot be undone.", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            event.put("cancelled", true);
            event.put("registration_open", false);
            updateEventInJson(event);
            loadOwnedEvents();
        }
    }
}