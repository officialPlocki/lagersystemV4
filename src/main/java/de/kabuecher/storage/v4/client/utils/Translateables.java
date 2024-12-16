package de.kabuecher.storage.v4.client.utils;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class Translateables {

    private JSONObject cachedPartFile;
    private Instant lastFetchTime;
    private static final URI PARTS_URI = URI.create("https://storage.kabuecher.de/parts.json");

    public String getNameByEAN(String ean) {
        JSONObject partFile = getPartFile();
        JSONObject parts = partFile.getJSONObject("parts");
        for (String key : parts.keySet()) {
            JSONObject part = parts.getJSONObject(key);
            if (part.getString("ean").equals(ean)) {
                return part.getString("name");
            }
        }
        return "unknown";
    }

    public String getPartIDByEAN(String ean) {
        JSONObject partFile = getPartFile();
        JSONObject parts = partFile.getJSONObject("parts");
        for (String key : parts.keySet()) {
            JSONObject part = parts.getJSONObject(key);
            if (part.getString("ean").equals(ean)) {
                return key;
            }
        }
        return "unknown";
    }

    public JSONObject getPartByName(String name) {
        JSONObject partFile = getPartFile();
        JSONObject parts = partFile.getJSONObject("parts");
        for (String key : parts.keySet()) {
            JSONObject part = parts.getJSONObject(key);
            if (part.getString("name").equals(name)) {
                return part;
            }
        }
        return null;
    }

    public JSONObject getPartByEAN(String ean) {
        JSONObject partFile = getPartFile();
        JSONObject parts = partFile.getJSONObject("parts");
        for (String key : parts.keySet()) {
            JSONObject part = parts.getJSONObject(key);
            if (part.getString("ean").equals(ean)) {
                return part;
            }
        }
        return null;
    }

    public JSONObject getPartByPartID(String partID) {
        JSONObject partFile = getPartFile();
        JSONObject parts = partFile.getJSONObject("parts");
        return parts.optJSONObject(partID, null);
    }

    public String getEANByPartID(String partID) {
        JSONObject partFile = getPartFile();
        JSONObject parts = partFile.getJSONObject("parts");
        return parts.getJSONObject(partID).optString("ean", "unknown");
    }

    public String getNameByPartID(String partID) {
        JSONObject partFile = getPartFile();
        JSONObject parts = partFile.getJSONObject("parts");
        return parts.getJSONObject(partID).optString("name", "unknown");
    }

    public boolean isIgnored(String partID) {
        JSONObject partFile = getPartFile();
        JSONObject ignoringParts = partFile.getJSONObject("ignoringParts");
        return ignoringParts.has(partID);
    }

    public synchronized JSONObject getPartFile() {
        // Check if the cache is valid
        /*if (cachedPartFile != null && lastFetchTime != null) {
            Instant now = Instant.now();
            if (now.isBefore(lastFetchTime.plusSeconds(15 * 60))) { // 15 minutes
                return cachedPartFile;
            }
        }

        // Fetch the JSON data from the server
        try {
            HttpURLConnection connection = (HttpURLConnection) PARTS_URI.toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == 200) {
                try (InputStream inputStream = connection.getInputStream()) {
                    String jsonText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    cachedPartFile = new JSONObject(jsonText);
                    lastFetchTime = Instant.now(); // Update fetch time
                    return cachedPartFile;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return the cached version if available, even if an error occurred
        return cachedPartFile;*/

        //get from resources
        try {
            InputStream inputStream = Translateables.class.getClassLoader().getResourceAsStream("parts.json");
            String jsonText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return new JSONObject(jsonText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
