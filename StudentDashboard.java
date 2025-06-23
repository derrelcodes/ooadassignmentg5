import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class StudentDashboard {
    private JFrame frame;
    private JPanel eventsPanel;
    private String username;

    public StudentDashboard(String username) {
        this.username = username;
        frame = new JFrame("Student Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        frame.add(headerPanel, BorderLayout.NORTH);

        // Main content panel with scroll pane
        eventsPanel = new JPanel();
        eventsPanel.setLayout(new GridLayout(0, 3, 15, 15)); // Grid layout for cards
        eventsPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(eventsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        frame.add(scrollPane, BorderLayout.CENTER);

        loadActiveEvents();
        frame.setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        headerPanel.setBackground(new Color(240, 240, 240));

        JLabel welcomeLabel = new JLabel("WELCOME " + username.toUpperCase() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton myBookingsButton = new JButton("MY BOOKINGS");
        myBookingsButton.addActionListener(e -> {
            frame.dispose();
            new MyBookingsPage(username);
        });

        JButton signOutButton = new JButton("SIGN OUT");
        signOutButton.addActionListener(e -> {
            frame.dispose();
            new MainPage().createAndShowGUI();
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(myBookingsButton);
        buttonsPanel.add(signOutButton);
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private void loadActiveEvents() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("data/events.json")));
            JSONArray allEvents = new JSONArray(content);
            List<JSONObject> activeEvents = new ArrayList<>();

            // Filter for active events
            for (int i = 0; i < allEvents.length(); i++) {
                JSONObject event = allEvents.getJSONObject(i);
                LocalDate eventDate = LocalDate.parse(event.getString("date"), DateTimeFormatter.ISO_LOCAL_DATE);
                if (!event.optBoolean("cancelled", false) && eventDate.isAfter(LocalDate.now().minusDays(1))) {
                    activeEvents.add(event);
                }
            }
            
            // Create a card for each active event
            for (JSONObject event : activeEvents) {
                eventsPanel.add(createEventCard(event));
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to load events from file.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JPanel createEventCard(JSONObject event) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        // Top section with event type
        JLabel eventTypeLabel = new JLabel(event.optString("type", "EVENT").toUpperCase());
        eventTypeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        eventTypeLabel.setForeground(new Color(100, 100, 100));
        
        // Center section with event details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        JLabel eventNameLabel = new JLabel(event.optString("name", "Event Name"));
        eventNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        detailsPanel.add(eventNameLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        
        detailsPanel.add(new JLabel("Date: " + event.optString("date")));
        detailsPanel.add(new JLabel("Time: " + event.optString("start_time", "N/A") + " - " + event.optString("end_time", "N/A")));
        detailsPanel.add(new JLabel("Venue: " + event.optString("venue")));
        
        String priceText = event.optBoolean("has_fee", false) ? "Price: RM " + String.format("%.2f", event.optDouble("base_price")) : "Price: Free";
        detailsPanel.add(new JLabel(priceText));

        if (event.optInt("early_bird_limit", 0) > 0) {
            JLabel earlyBirdLabel = new JLabel("Early Bird Discount: Available");
            earlyBirdLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            earlyBirdLabel.setForeground(new Color(0, 128, 0));
            detailsPanel.add(earlyBirdLabel);
        }

        // Bottom section with register button
        JButton registerButton = new JButton("REGISTER");
        registerButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Determine button state based on event status
        boolean registrationOpen = event.optBoolean("registration_open", false);
        int capacity = event.getInt("capacity");
        int participantCount = event.getJSONArray("participants").length();
        boolean isFull = participantCount >= capacity;
        
        if (!registrationOpen) {
            registerButton.setText("Registration Closed");
            registerButton.setEnabled(false);
        } else if (isFull) {
            registerButton.setText("Full");
            registerButton.setEnabled(false);
        } else {
            registerButton.addActionListener(e -> {
                frame.dispose();
                new RegistrationForm(username, event);
            });
        }
        
        card.add(eventTypeLabel, BorderLayout.NORTH);
        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(registerButton, BorderLayout.SOUTH);
        
        return card;
    }
}