import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDate;

public class StudentDashboard {
    public StudentDashboard(String username) {
        JFrame frame = new JFrame("Student Dashboard");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("Welcome, " + username , JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel eventPanel = new JPanel();
        eventPanel.setLayout(new BoxLayout(eventPanel, BoxLayout.Y_AXIS));

        try {
            String content = new String(Files.readAllBytes(Paths.get("data/events.json")));
            JSONArray events = new JSONArray(content);
            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                String eventDate = event.getString("date");
                boolean isCancelled = event.optBoolean("cancelled", false);
                boolean isRegistrationOpen = event.optBoolean("registration_open", true);
                int capacity = event.getInt("capacity");
                JSONArray participants = event.optJSONArray("participants");
                int currentCount = participants != null ? participants.length() : 0;

                if (LocalDate.parse(eventDate).isBefore(LocalDate.now())) continue;

                JPanel card = new JPanel(new GridLayout(0, 1));
                card.setBorder(BorderFactory.createTitledBorder(event.getString("name")));
                card.add(new JLabel("Type: " + event.getString("type")));
                card.add(new JLabel("Venue: " + event.getString("venue")));
                card.add(new JLabel("Date: " + eventDate));

                String status = isCancelled ? "Event Cancelled" :
                                (!isRegistrationOpen ? "Registration Closed" :
                                (currentCount >= capacity ? "Full" : "Register"));

                JButton actionBtn = new JButton(status);
                actionBtn.setEnabled(status.equals("Register"));
                // Optional: actionBtn.addActionListener(...) for actual registration

                card.add(actionBtn);
                eventPanel.add(card);
                eventPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } catch (Exception e) {
            eventPanel.add(new JLabel("Failed to load events."));
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
}
