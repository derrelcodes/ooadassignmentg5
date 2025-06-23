import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.*;
import java.time.LocalDate;

public class FeeCalculationPage {
    public FeeCalculationPage(String username, JSONObject event, boolean needsTransport, boolean hasDietary) {
        JFrame frame = new JFrame("Payment Breakdown");
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("Event: " + event.getString("name")));

        final double[] price = {0.0};
        boolean earlyBird = false;

        int earlyLimit = event.optInt("early_bird_limit", 0);
        JSONArray participants = event.optJSONArray("participants");
        int currentPax = participants != null ? participants.length() : 0;

        if (event.optBoolean("has_fee", false)) {
            if (currentPax < earlyLimit) {
                price[0] += event.optDouble("early_bird_price", 0);
                earlyBird = true;
            } else {
                price[0] += event.optDouble("normal_price", 0);
            }
        }

        if (needsTransport) {
            price[0] += event.optDouble("transport_fee", 0);
        }

        panel.add(new JLabel("Base Price: RM" + (earlyBird ? event.optDouble("early_bird_price", 0) : event.optDouble("normal_price", 0))));
        if (needsTransport) panel.add(new JLabel("Transport Fee: RM" + event.optDouble("transport_fee", 0)));
        panel.add(new JLabel("Total: RM" + price[0]));

        JButton confirmBtn = new JButton("Confirm Payment");
        confirmBtn.addActionListener(e -> {
            try {
                JSONObject booking = new JSONObject();
                booking.put("username", username);
                booking.put("event_id", event.getString("event_id"));
                booking.put("paid_amount", price[0]);
                booking.put("transport", needsTransport);
                booking.put("dietary", hasDietary);
                booking.put("booking_date", LocalDate.now().toString());

                File regFile = new File("data/registrations.json");
                JSONArray allBookings;
                if (regFile.exists()) {
                    String content = new String(Files.readAllBytes(regFile.toPath()));
                    allBookings = new JSONArray(content);
                } else {
                    allBookings = new JSONArray();
                }
                allBookings.put(booking);
                FileWriter writer = new FileWriter(regFile);
                writer.write(allBookings.toString(4));
                writer.close();

                JSONArray events = new JSONArray(new String(Files.readAllBytes(Paths.get("data/events.json"))));
                for (int i = 0; i < events.length(); i++) {
                    JSONObject ev = events.getJSONObject(i);
                    if (ev.getString("event_id").equals(event.getString("event_id"))) {
                        ev.getJSONArray("participants").put(username);
                        break;
                    }
                }
                FileWriter ew = new FileWriter("data/events.json");
                ew.write(events.toString(4));
                ew.close();

                JOptionPane.showMessageDialog(frame, "Registration successful! Amount Paid: RM" + price[0]);
                frame.dispose();
                new StudentDashboard(username);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        panel.add(Box.createVerticalStrut(20));
        panel.add(confirmBtn);

        frame.add(panel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
