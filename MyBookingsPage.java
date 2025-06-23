import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class MyBookingsPage {
    private JFrame frame;
    private JPanel bookingsPanel;
    private String username;
    private JSONArray allEvents;

    public MyBookingsPage(String username) {
        this.username = username;
        frame = new JFrame("My Bookings");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        frame.add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        bookingsPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        bookingsPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(bookingsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        frame.add(scrollPane, BorderLayout.CENTER);

        loadUserBookings();
        frame.setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        headerPanel.setBackground(new Color(240, 240, 240));

        JLabel titleLabel = new JLabel("MY BOOKINGS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton backButton = new JButton("BACK TO DASHBOARD");
        backButton.addActionListener(e -> {
            frame.dispose();
            new StudentDashboard(username);
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);
        
        return headerPanel;
    }

    private void loadUserBookings() {
        try {
            // Load all events first for easy lookup
            String eventsContent = new String(Files.readAllBytes(Paths.get("data/events.json")));
            allEvents = new JSONArray(eventsContent);

            // Load registrations and filter for the current user
            String registrationsContent = new String(Files.readAllBytes(Paths.get("data/registrations.json")));
            JSONArray allRegistrations = new JSONArray(registrationsContent);
            
            for (int i = 0; i < allRegistrations.length(); i++) {
                JSONObject booking = allRegistrations.getJSONObject(i);
                if (booking.getString("username").equals(this.username)) {
                    JSONObject event = findEventById(booking.getString("event_id"));
                    if (event != null) {
                        bookingsPanel.add(createBookingCard(booking, event));
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to load bookings from file.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JSONObject findEventById(String eventId) {
        for (int i = 0; i < allEvents.length(); i++) {
            JSONObject event = allEvents.getJSONObject(i);
            if (event.getString("event_id").equals(eventId)) {
                return event;
            }
        }
        return null; // Should not happen in consistent data
    }

    private JPanel createBookingCard(JSONObject booking, JSONObject event) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel eventTypeLabel = new JLabel(event.optString("type", "EVENT").toUpperCase());
        eventTypeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        eventTypeLabel.setForeground(new Color(100, 100, 100));
        
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

        JButton viewDetailsButton = new JButton("VIEW DETAILS");
        viewDetailsButton.setFont(new Font("Arial", Font.BOLD, 12));
        viewDetailsButton.addActionListener(e -> {
            // Open the new details page
            new BookingDetailsPage(booking, event, username);
            frame.dispose();
        });
        
        card.add(eventTypeLabel, BorderLayout.NORTH);
        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(viewDetailsButton, BorderLayout.SOUTH);
        
        return card;
    }
}