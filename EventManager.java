import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class EventManager {

    public static JSONArray getAllEvents() {
        try {
            File file = new File("data/events.json");
            if (!file.exists()) return new JSONArray();
            String content = new String(Files.readAllBytes(Paths.get("data/events.json")));
            return new JSONArray(content);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static JSONObject getEventById(String id) {
        JSONArray events = getAllEvents();
        for (int i = 0; i < events.length(); i++) {
            JSONObject event = events.getJSONObject(i);
            if (event.getString("event_id").equals(id)) {
                return event;
            }
        }
        return null;
    }

    public static void saveEvent(JSONObject newEvent) {
        try {
            JSONArray events = getAllEvents();
            newEvent.put("event_id", UUID.randomUUID().toString());
            newEvent.put("participants", new JSONArray());
            newEvent.put("cancelled", false);
            newEvent.put("registration_open", true);
            events.put(newEvent);
            FileWriter writer = new FileWriter("data/events.json");
            writer.write(events.toString(4));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateEvent(JSONObject updatedEvent) {
        try {
            JSONArray events = getAllEvents();
            for (int i = 0; i < events.length(); i++) {
                if (events.getJSONObject(i).getString("event_id").equals(updatedEvent.getString("event_id"))) {
                    events.put(i, updatedEvent);
                    break;
                }
            }
            FileWriter writer = new FileWriter("data/events.json");
            writer.write(events.toString(4));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteEvent(String eventId) {
        try {
            JSONArray events = getAllEvents();
            for (int i = 0; i < events.length(); i++) {
                if (events.getJSONObject(i).getString("event_id").equals(eventId)) {
                    events.remove(i);
                    break;
                }
            }
            FileWriter writer = new FileWriter("data/events.json");
            writer.write(events.toString(4));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
