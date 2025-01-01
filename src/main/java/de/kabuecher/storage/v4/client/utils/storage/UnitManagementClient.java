package de.kabuecher.storage.v4.client.utils.storage;

import de.kabuecher.storage.v4.Main;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class UnitManagementClient {

    public boolean removeItem(String storeName, String stackName, String boxId, String itemId, int amount) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .header("Content-Type", "application/json")
                .header("username", Main.username)
                .header("password", Main.passwordHash)
                .POST(HttpRequest.BodyPublishers.ofString(
                        new JSONObject()
                                .put("data", new JSONObject()
                                        .put("storeName", storeName)
                                        .put("stackName", stackName)
                                        .put("boxId", boxId)
                                        .put("itemId", itemId)
                                        .put("amount", amount)
                                )
                                .put("method", "removeItemFromStore")
                                .toString()
                ))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public JSONObject getStoredItems(String username) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .header("Content-Type", "application/json")
                .header("username", Main.username)
                .header("password", Main.passwordHash)
                .POST(HttpRequest.BodyPublishers.ofString(
                        new JSONObject()
                                .put("method", "getStoredItems")
                                .toString()
                ))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject resp = new JSONObject(response.body());

            List<String> keysToRemove = new ArrayList<>();
            for (String key : resp.keySet()) {
                JSONObject item = resp.getJSONObject(key);
                if (!item.has("stacks")) {
                    keysToRemove.add(key);
                }
            }
            for (String key : keysToRemove) {
                resp.remove(key);
            }

            return resp;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }

    }

    public JSONObject searchForItemInStore(String storeName, String itemId, int quantity) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .header("Content-Type", "application/json")
                .header("username", Main.username)
                .header("password", Main.passwordHash)
                .POST(HttpRequest.BodyPublishers.ofString(
                        new JSONObject()
                                .put("data", new JSONObject()
                                        .put("storeName", storeName)
                                        .put("itemId", itemId)
                                        .put("quantity", quantity)
                                )
                                .put("method", "searchForItemInStore")
                                .toString()
                ))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new JSONObject(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addItemToBox(String storeName, String stackName, String boxId, String itemId, int quantity) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .header("Content-Type", "application/json")
                .header("username", Main.username)
                .header("password", Main.passwordHash)
                .POST(HttpRequest.BodyPublishers.ofString(
                        new JSONObject()
                                .put("data", new JSONObject()
                                        .put("storeName", storeName)
                                        .put("stackName", stackName)
                                        .put("boxId", boxId)
                                        .put("itemId", itemId)
                                        .put("quantity", quantity)
                                )
                                .put("method", "addItemToStore")
                                .toString()
                ))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
