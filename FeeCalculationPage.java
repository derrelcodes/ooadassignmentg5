import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class FeeCalculationPage {
    private JFrame frame;
    private JSONObject event;
    private String username;
    private Map<String, String> userDetails;
    private double finalPrice;

    public FeeCalculationPage(String username, JSONObject event, Map<String, String> userDetails) {
        this.username = username;
        this.event = event;
        this.userDetails = userDetails;

        frame = new JFrame("Payment Breakdown");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        double basePrice = event.optDouble("base_price", 0);
        double earlyBirdDiscount = 0;
        int currentParticipants = event.getJSONArray("participants").length();
        int earlyBirdLimit = event.optInt("early_bird_limit", 0);
        double earlyBirdPrice = event.optDouble("early_bird_price", 0);

        if (currentParticipants < earlyBirdLimit && earlyBirdPrice > 0) {
            earlyBirdDiscount = basePrice - earlyBirdPrice;
        }

        boolean needsTransport = userDetails.get("needsTransport").equalsIgnoreCase("Yes");
        double transportFee = needsTransport ? event.optDouble("transport_fee", 0) : 0;
        double totalBeforeDiscount = basePrice + transportFee;
        this.finalPrice = totalBeforeDiscount - earlyBirdDiscount;

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainPanel.add(createBreakdownPanel(basePrice, transportFee, earlyBirdDiscount, totalBeforeDiscount, this.finalPrice));
        mainPanel.add(createActionPanel(this.finalPrice));

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(createFooterPanel(), BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JPanel createBreakdownPanel(double base, double transport, double discount, double totalBefore, double totalAfter) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("PAYMENT BREAKDOWN"));

        panel.add(new JLabel("Base Price: RM " + String.format("%.2f", base)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Transportation: RM " + String.format("%.2f", transport)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Early Bird Discount: - RM " + String.format("%.2f", discount)));
        panel.add(Box.createVerticalStrut(20));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Total Before Discount: RM " + String.format("%.2f", totalBefore)));
        panel.add(Box.createVerticalStrut(10));

        JLabel totalAfterLabel = new JLabel("Total After Discount: RM " + String.format("%.2f", totalAfter));
        totalAfterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(totalAfterLabel);

        return panel;
    }

    private JPanel createActionPanel(double finalPrice) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("PAYMENT DETAILS"));

        JLabel amountLabel = new JLabel("AMOUNT TO BE PAID");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        // UPDATED: Align text to the left
        amountLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel priceLabel = new JLabel("RM " + String.format("%.2f", finalPrice));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 24));
        // UPDATED: Align text to the left
        priceLabel.setHorizontalAlignment(SwingConstants.LEFT);
        priceLabel.setForeground(Color.BLUE);

        JTextArea instructions = new JTextArea();
        instructions.setText("Bank: Maybank\nAccount No: 512345678901\n\nPlease Whatsapp the payment receipt to 018-8214217 for verification.");
        instructions.setFont(new Font("Arial", Font.PLAIN, 12));
        instructions.setWrapStyleWord(true);
        instructions.setLineWrap(true);
        instructions.setOpaque(false);
        instructions.setEditable(false);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(amountLabel);
        centerPanel.add(priceLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(instructions);

        // UPDATED: Align the components themselves to the left within the layout
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructions.setAlignmentX(Component.LEFT_ALIGNMENT);


        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(new EmptyBorder(0, 15, 10, 15));

        JButton backButton = new JButton("BACK");
        backButton.addActionListener(e -> {
            frame.dispose();
            new RegistrationForm(username, event);
        });

        JButton registerButton = new JButton("REGISTER");
        registerButton.addActionListener(e -> saveRegistration());

        footerPanel.add(backButton);
        footerPanel.add(registerButton);

        return footerPanel;
    }

    private void saveRegistration() {
        try {
            JSONArray events = new JSONArray(new String(Files.readAllBytes(Paths.get("data/events.json"))));
            for (int i = 0; i < events.length(); i++) {
                JSONObject ev = events.getJSONObject(i);
                if (ev.getString("event_id").equals(event.getString("event_id"))) {
                    ev.getJSONArray("participants").put(new JSONObject().put("username", username));
                    this.event = ev;
                    break;
                }
            }
            try (FileWriter writer = new FileWriter("data/events.json")) {
                writer.write(events.toString(4));
            }

            JSONArray registrations = new JSONArray();
            try {
                registrations = new JSONArray(new String(Files.readAllBytes(Paths.get("data/registrations.json"))));
            } catch (Exception ignored) {
            }

            JSONObject newRegistration = new JSONObject();
            newRegistration.put("username", username);
            newRegistration.put("event_id", event.getString("event_id"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            newRegistration.put("booking_date", java.time.LocalDate.now().format(formatter));

            newRegistration.put("fullName", userDetails.get("fullName"));
            newRegistration.put("studentId", userDetails.get("studentId"));
            newRegistration.put("contact", userDetails.get("contact"));
            newRegistration.put("email", userDetails.get("email"));
            newRegistration.put("dietary", userDetails.get("dietary"));
            newRegistration.put("transport", userDetails.get("needsTransport"));
            newRegistration.put("paid_amount", this.finalPrice);

            registrations.put(newRegistration);

            try (FileWriter writer = new FileWriter("data/registrations.json")) {
                writer.write(registrations.toString(4));
            }

            JOptionPane.showMessageDialog(frame, "Registration Successful! Please complete the payment.");
            frame.dispose();
            new StudentDashboard(username);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error during registration: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}