import org.json.JSONArray;
import org.json.JSONObject;

public class Event {
    private String eventId;
    private String name;
    private String type;
    private String venue;
    private String date;
    private String startTime;
    private String endTime;
    private int capacity;
    private boolean hasFee;
    private boolean outsideMMU;
    private double earlyBirdPrice;
    private double normalPrice;
    private int earlyBirdLimit;
    private double transportFee;
    private boolean providesFood;
    private boolean providesTransport;
    private boolean cancelled;
    private boolean registrationOpen;
    private JSONArray participants;

    public Event(String eventId, String name, String type, String venue, String date, String startTime, String endTime,
                 int capacity, boolean hasFee, boolean outsideMMU, double earlyBirdPrice, double normalPrice,
                 int earlyBirdLimit, double transportFee, boolean providesFood, boolean providesTransport,
                 boolean cancelled, boolean registrationOpen, JSONArray participants) {

        this.eventId = eventId;
        this.name = name;
        this.type = type;
        this.venue = venue;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.hasFee = hasFee;
        this.outsideMMU = outsideMMU;
        this.earlyBirdPrice = earlyBirdPrice;
        this.normalPrice = normalPrice;
        this.earlyBirdLimit = earlyBirdLimit;
        this.transportFee = transportFee;
        this.providesFood = providesFood;
        this.providesTransport = providesTransport;
        this.cancelled = cancelled;
        this.registrationOpen = registrationOpen;
        this.participants = participants;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("event_id", eventId);
        obj.put("name", name);
        obj.put("type", type);
        obj.put("venue", venue);
        obj.put("date", date);
        obj.put("start_time", startTime);
        obj.put("end_time", endTime);
        obj.put("capacity", capacity);
        obj.put("has_fee", hasFee);
        obj.put("outside_mmu", outsideMMU);
        obj.put("early_bird_price", earlyBirdPrice);
        obj.put("normal_price", normalPrice);
        obj.put("early_bird_limit", earlyBirdLimit);
        obj.put("transport_fee", transportFee);
        obj.put("provides_food", providesFood);
        obj.put("provides_transportation", providesTransport);
        obj.put("cancelled", cancelled);
        obj.put("registration_open", registrationOpen);
        obj.put("participants", participants);
        return obj;
    }

    public static Event fromJSON(JSONObject obj) {
        return new Event(
            obj.getString("event_id"),
            obj.getString("name"),
            obj.getString("type"),
            obj.getString("venue"),
            obj.getString("date"),
            obj.getString("start_time"),
            obj.getString("end_time"),
            obj.getInt("capacity"),
            obj.optBoolean("has_fee", false),
            obj.optBoolean("outside_mmu", false),
            obj.optDouble("early_bird_price", 0.0),
            obj.optDouble("normal_price", 0.0),
            obj.optInt("early_bird_limit", 0),
            obj.optDouble("transport_fee", 0.0),
            obj.optBoolean("provides_food", false),
            obj.optBoolean("provides_transportation", false),
            obj.optBoolean("cancelled", false),
            obj.optBoolean("registration_open", true),
            obj.optJSONArray("participants") != null ? obj.getJSONArray("participants") : new JSONArray()
        );
    }

    // Getters for necessary fields (add more if needed)
    public String getEventId() { return eventId; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public boolean isCancelled() { return cancelled; }
    public boolean isRegistrationOpen() { return registrationOpen; }
    public int getCapacity() { return capacity; }
    public JSONArray getParticipants() { return participants; }
}
