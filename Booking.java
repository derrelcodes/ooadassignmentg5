import org.json.JSONObject;

public class Booking {
    private String username;
    private String eventId;
    private double paidAmount;
    private boolean transport;
    private boolean dietary;
    private String bookingDate;

    public Booking(String username, String eventId, double paidAmount, boolean transport, boolean dietary, String bookingDate) {
        this.username = username;
        this.eventId = eventId;
        this.paidAmount = paidAmount;
        this.transport = transport;
        this.dietary = dietary;
        this.bookingDate = bookingDate;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("username", username);
        obj.put("event_id", eventId);
        obj.put("paid_amount", paidAmount);
        obj.put("transport", transport);
        obj.put("dietary", dietary);
        obj.put("booking_date", bookingDate);
        return obj;
    }

    public static Booking fromJSON(JSONObject obj) {
        return new Booking(
                obj.getString("username"),
                obj.getString("event_id"),
                obj.getDouble("paid_amount"),
                obj.getBoolean("transport"),
                obj.getBoolean("dietary"),
                obj.getString("booking_date")
        );
    }

    public String getUsername() { return username; }
    public String getEventId() { return eventId; }
    public double getPaidAmount() { return paidAmount; }
    public boolean isTransport() { return transport; }
    public boolean isDietary() { return dietary; }
    public String getBookingDate() { return bookingDate; }
}
