package de.kabuecher.storage.v4.sendcloud;

import de.kabuecher.storage.v4.Main;
import de.kabuecher.storage.v4.sevdesk.impl.offer.Offer;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SendCloud {

    public File getParcelLabel(Offer offer, double weight, int height) {
        String fullAddress = offer.getAddress();

        String[] lines = fullAddress.split("\n");

        String address2 = lines.length > 2 ? lines[1] : "";
        String streetAndHouseNumber = lines[lines.length - 2];
        String postalCodeAndCity = lines[lines.length - 1];

        String[] streetParts = streetAndHouseNumber.split(" ");
        String address = streetParts[0];
        String houseNumber = streetParts[1];

        String[] cityParts = postalCodeAndCity.split(" ");
        String postalCode = cityParts[0];
        String city = cityParts[1];

        JSONObject object = new JSONObject();
        object.put("parcel", new JSONObject()
                .put("name", offer.getAddress().split("\n")[0])
                .put("company_name", (offer.getContact().getCategory().getObjectName().equalsIgnoreCase("Organisations") ? offer.getContact().getName() : ""))
                .put("address", address)
                .put("house_number", houseNumber)
                .put("address_2", address2)
                .put("city", city)
                .put("country", "DE")
                .put("postal_code", postalCode)
                .put("weight", weight)
                .put("total_order_value", String.valueOf(offer.getSumGross()))
                .put("total_order_value_currency", "EUR")
                .put("quantity", 1)
                .put("is_return", false)
                .put("request_label", true)
                .put("apply_shipping_rules", false)
                .put("request_label_async", false)
                .put("shipment", (weight > 1001 ? new JSONObject().put("id", 3805).put("name", "DPD Shop2Home XS 0-20kg") : (height > 5 ? new JSONObject().put("id", 3805).put("name", "DPD Shop2Home XS 0-20kg") : new JSONObject().put("id", 2831).put("name", "DHL Warenpost GoGreen 0-1kg"))))
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://panel.sendcloud.sc/api/v2/parcels"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + Main.getJsonFile().get("sendcloud").getString("api_key"))
                .POST(HttpRequest.BodyPublishers.ofString(object.toString()))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            //return new JSONObject(response.body()).toString();
            return new File("");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
