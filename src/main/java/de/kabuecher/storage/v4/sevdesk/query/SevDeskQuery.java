package de.kabuecher.storage.v4.sevdesk.query;

import de.kabuecher.storage.v4.Main;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SevDeskQuery {

    private final static String sevdesk_api_url = "https://my.sevdesk.de/api/v1";

    public static JSONObject query(String queryParam, JSONObject body, QueryMethod method) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest.Builder build = HttpRequest.newBuilder()
                    .uri(URI.create(sevdesk_api_url + queryParam))
                    .header("Authorization", Main.sevdesk_api_token)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json");

            if(method == QueryMethod.GET) {
                build.GET();
            } else if(method == QueryMethod.POST) {
                build.POST(HttpRequest.BodyPublishers.ofString(body.toString()));
            } else if(method == QueryMethod.PUT) {
                build.PUT(HttpRequest.BodyPublishers.ofString(body.toString()));
            }

            HttpRequest request = build.build();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            return new JSONObject((new String(response.body()).startsWith("{") ? new String(response.body()) : "{}"));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
