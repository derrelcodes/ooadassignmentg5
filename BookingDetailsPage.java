import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import org.json.JSONObject;

public class BookingDetailsPage {
    private JFrame frame;
    private String username;

    public BookingDetailsPage(JSONObject booking, JSONObject event, String username) {
        this.username = username;

        frame = new JFrame("Registered Details");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 550);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);
        frame.getRootPane().setBorder(new EmptyBorder(15, 15, 15, 15));

        // Main panel to hold both sections
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Top section: User's submitted details
        JPanel userDetailsPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        userDetailsPanel.setBorder(BorderFactory.createTitledBorder("Your Information"));
        
        userDetailsPanel.add(new JLabel("Name:"));
        userDetailsPanel.add(new JLabel(booking.optString("fullName", "N/A")));
        
        // --- UPDATED: Label text changed ---
        userDetailsPanel.add(new JLabel("Student / Staff ID:"));
        userDetailsPanel.add(new JLabel(booking.optString("studentId", "N/A")));
        
        userDetailsPanel.add(new JLabel("Contact Number:"));
        userDetailsPanel.add(new JLabel(booking.optString("contact", "N/A")));
        userDetailsPanel.add(new JLabel("Dietary Preference:"));
        userDetailsPanel.add(new JLabel(booking.optString("dietary", "N/A")));
        userDetailsPanel.add(new JLabel("Transportation:"));
        userDetailsPanel.add(new JLabel(booking.optString("transport", "N/A")));

        // Bottom section: Payment Breakdown
        JPanel paymentPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("PAYMENT BREAKDOWN"));

        double paidAmount = booking.getDouble("paid_amount");
        double transportFee = booking.getString("transport").equalsIgnoreCase("Yes") ? event.getDouble("transport_fee") : 0.0;
        double basePrice = paidAmount - transportFee;
        double originalBasePrice = event.getDouble("base_price");
        double discount = originalBasePrice - basePrice;


        paymentPanel.add(new JLabel("Base Price:"));
        paymentPanel.add(new JLabel("RM " + String.format("%.2f", originalBasePrice)));
        paymentPanel.add(new JLabel("Transportation:"));
        paymentPanel.add(new JLabel("RM " + String.format("%.2f", transportFee)));
        paymentPanel.add(new JLabel("Early Bird Discount:"));
        paymentPanel.add(new JLabel("- RM " + String.format("%.2f", discount)));

        // Total Paid Label
        JLabel totalPaidLabel = new JLabel("TOTAL PAID:");
        totalPaidLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel totalAmountLabel = new JLabel("RM " + String.format("%.2f", paidAmount));
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        paymentPanel.add(totalPaidLabel);
        paymentPanel.add(totalAmountLabel);

        mainPanel.add(userDetailsPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(paymentPanel);
        
        frame.add(mainPanel, BorderLayout.CENTER);

        // Footer with Back button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("BACK");
        backButton.addActionListener(e -> {
            frame.dispose();
            new MyBookingsPage(username);
        });
        footerPanel.add(backButton);
        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}