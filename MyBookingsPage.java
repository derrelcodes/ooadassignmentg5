import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        frame.add(createHeaderPanel(), BorderLayout.NORTH);

        bookingsPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        bookingsPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(bookingsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

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
        bookingsPanel.removeAll();
        try {
            String eventsContent = new String(Files.readAllBytes(Paths.get("data/events.json")));
            allEvents = new JSONArray(eventsContent);

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
        bookingsPanel.revalidate();
        bookingsPanel.repaint();
    }

    private JSONObject findEventById(String eventId) {
        for (int i = 0; i < allEvents.length(); i++) {
            JSONObject event = allEvents.getJSONObject(i);
            if (event.getString("event_id").equals(eventId)) {
                return event;
            }
        }
        return null;
    }

    // --- THIS METHOD HAS BEEN UPDATED ---
    private JPanel createBookingCard(JSONObject booking, JSONObject event) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // Details Panel (Top)
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);

        JLabel eventTypeLabel = new JLabel(event.optString("type", "EVENT").toUpperCase());
        eventTypeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        eventTypeLabel.setForeground(new Color(100, 100, 100));
        detailsPanel.add(eventTypeLabel);

        JLabel eventNameLabel = new JLabel(event.optString("name", "Event Name"));
        eventNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        detailsPanel.add(eventNameLabel);
        detailsPanel.add(Box.createVerticalStrut(5));

        detailsPanel.add(new JLabel("Date: " + event.optString("date")));
        detailsPanel.add(new JLabel("Time: " + event.optString("start_time", "N/A") + " - " + event.optString("end_time", "N/A")));
        detailsPanel.add(new JLabel("Venue: " + event.optString("venue")));
        
        // Poster Panel (Center)
        JLabel posterLabel = new JLabel();
        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        posterLabel.setVerticalAlignment(SwingConstants.CENTER);
        String posterPath = event.optString("poster_path");
        if (posterPath != null && !posterPath.isEmpty() && new File(posterPath).exists()) {
            ImageIcon imageIcon = new ImageIcon(posterPath);
            Image image = imageIcon.getImage().getScaledInstance(200, 120, Image.SCALE_SMOOTH);
            posterLabel.setIcon(new ImageIcon(image));
        } else {
            posterLabel.setText("No Poster Available");
        }

        // Button Panel (Bottom)
        JButton viewDetailsButton = new JButton("VIEW DETAILS");
        viewDetailsButton.setFont(new Font("Arial", Font.BOLD, 12));

        // Logic for cancelled events
        boolean isCancelled = event.optBoolean("cancelled", false);
        if (isCancelled) {
            card.setBackground(new Color(255, 220, 220));
            eventNameLabel.setText("<html><s>" + event.optString("name") + "</s> (CANCELLED)</html>");
            viewDetailsButton.setEnabled(false);

            double paidAmount = booking.optDouble("paid_amount", 0.0);
            if (paidAmount > 0) {
                JLabel refundLabel = new JLabel("Refund will be processed.");
                refundLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                refundLabel.setForeground(Color.RED);
                detailsPanel.add(Box.createVerticalStrut(10));
                detailsPanel.add(refundLabel);
            }
        } else {
            card.setBackground(Color.WHITE);
            viewDetailsButton.addActionListener(e -> {
                new BookingDetailsPage(booking, event, username);
                frame.dispose();
            });
        }

        card.add(detailsPanel, BorderLayout.NORTH);
        card.add(posterLabel, BorderLayout.CENTER);
        card.add(viewDetailsButton, BorderLayout.SOUTH);

        return card;
    }
}