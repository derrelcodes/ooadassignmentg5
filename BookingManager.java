import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BookingManager {

    public static List<JSONObject> getBookingsByUser(String username) {
        List<JSONObject> userBookings = new ArrayList<>();
        try {
            File file = new File("data/registrations.json");
            if (!file.exists()) return userBookings;

            String content = new String(Files.readAllBytes(Paths.get("data/registrations.json")));
            JSONArray bookings = new JSONArray(content);
            for (int i = 0; i < bookings.length(); i++) {
                JSONObject booking = bookings.getJSONObject(i);
                if (booking.getString("username").equals(username)) {
                    userBookings.add(booking);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userBookings;
    }

    public static List<JSONObject> getBookingsByEvent(String eventId) {
        List<JSONObject> eventBookings = new ArrayList<>();
        try {
            File file = new File("data/registrations.json");
            if (!file.exists()) return eventBookings;

            String content = new String(Files.readAllBytes(Paths.get("data/registrations.json")));
            JSONArray bookings = new JSONArray(content);
            for (int i = 0; i < bookings.length(); i++) {
                JSONObject booking = bookings.getJSONObject(i);
                if (booking.getString("event_id").equals(eventId)) {
                    eventBookings.add(booking);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventBookings;
    }
}
