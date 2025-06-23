import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;

public class ParticipantListPage {
    private JFrame frame;
    private JSONObject event;
    private JTable participantsTable;
    private DefaultTableModel tableModel;

    public ParticipantListPage(JSONObject event) {
        this.event = event;
        frame = new JFrame("Participants for: " + event.optString("name"));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close, don't exit app
        frame.setSize(800, 500);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        // Header
        JLabel titleLabel = new JLabel(event.optString("name").toUpperCase(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Name", "Student ID", "Food Preference", "Transportation", "Amount Paid (RM)"};
        tableModel = new DefaultTableModel(columnNames, 0);
        participantsTable = new JTable(tableModel);
        participantsTable.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(participantsTable);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton downloadButton = new JButton("DOWNLOAD");
        downloadButton.addActionListener(e -> downloadParticipantList());
        JButton backButton = new JButton("BACK");
        backButton.addActionListener(e -> frame.dispose());
        
        footerPanel.add(downloadButton);
        footerPanel.add(backButton);
        frame.add(footerPanel, BorderLayout.SOUTH);

        loadParticipants();
        frame.setVisible(true);
    }

    private void loadParticipants() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("data/registrations.json")));
            JSONArray allRegistrations = new JSONArray(content);
            String eventId = event.getString("event_id");

            for (int i = 0; i < allRegistrations.length(); i++) {
                JSONObject reg = allRegistrations.getJSONObject(i);
                if (reg.getString("event_id").equals(eventId)) {
                    String name = reg.optString("fullName", "N/A");
                    String studentId = reg.optString("studentId", "N/A");
                    String foodPref = reg.optString("dietary", "N/A");
                    String transport = reg.optString("transport", "N/A");
                    double amountPaid = reg.optDouble("paid_amount", 0.0);

                    tableModel.addRow(new Object[]{name, studentId, foodPref, transport, String.format("%.2f", amountPaid)});
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to load participant list.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void downloadParticipantList() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Participant List");
        fileChooser.setSelectedFile(new File(event.optString("name") + "_participants.csv"));

        int userSelection = fileChooser.showSaveDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                // Write headers
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.append(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");

                // Write data
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        writer.append(tableModel.getValueAt(row, col).toString());
                        if (col < tableModel.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
                JOptionPane.showMessageDialog(frame, "Participant list saved successfully to:\n" + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error saving file: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}