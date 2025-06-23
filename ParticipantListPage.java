import javax.swing.*;
import java.awt.*;
import java.util.List;
import org.json.*;

public class ParticipantListPage {
    public ParticipantListPage(JSONObject event) {
        JFrame frame = new JFrame("Participants for " + event.getString("name"));
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        List<JSONObject> bookings = BookingManager.getBookingsByEvent(event.getString("event_id"));

        if (bookings.isEmpty()) {
            panel.add(new JLabel("No participants registered yet."));
        } else {
            for (JSONObject booking : bookings) {
                JPanel card = new JPanel();
                card.setLayout(new GridLayout(0, 1));
                card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                card.add(new JLabel("Username: " + booking.getString("username")));
                card.add(new JLabel("Amount Paid: RM" + booking.getDouble("paid_amount")));
                card.add(new JLabel("Transport: " + (booking.getBoolean("transport") ? "Yes" : "No")));
                card.add(new JLabel("Dietary: " + (booking.getBoolean("dietary") ? "Yes" : "No")));
                card.add(new JLabel("Booking Date: " + booking.getString("booking_date")));
                panel.add(card);
                panel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
