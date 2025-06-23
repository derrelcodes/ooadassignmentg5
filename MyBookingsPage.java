import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import org.json.*;

public class MyBookingsPage {
    public MyBookingsPage(String username) {
        JFrame frame = new JFrame("My Bookings");
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        List<JSONObject> bookings = BookingManager.getBookingsByUser(username);
        JSONArray events = EventManager.getAllEvents();

        for (JSONObject booking : bookings) {
            String eventId = booking.getString("event_id");
            JSONObject matchedEvent = null;
            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                if (event.getString("event_id").equals(eventId)) {
                    matchedEvent = event;
                    break;
                }
            }
            if (matchedEvent == null) continue;

            JPanel card = new JPanel();
            card.setLayout(new GridLayout(0, 1));
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            card.setBackground(Color.WHITE);

            card.add(new JLabel("Event: " + matchedEvent.getString("name")));
            card.add(new JLabel("Date: " + matchedEvent.getString("date")));
            card.add(new JLabel("Venue: " + matchedEvent.getString("venue")));
            card.add(new JLabel("Amount Paid: RM" + booking.getDouble("paid_amount")));

            boolean isCancelled = matchedEvent.optBoolean("cancelled", false);
            LocalDate eventDate = LocalDate.parse(matchedEvent.getString("date"));
            LocalDate today = LocalDate.now();

            if (isCancelled) {
                card.add(new JLabel("Status: CANCELLED - Refund is being processed."));
            } else if (eventDate.isBefore(today)) {
                card.add(new JLabel("Status: EVENT ENDED"));
            } else {
                card.add(new JLabel("Status: UPCOMING"));
            }

            mainPanel.add(card);
            mainPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
