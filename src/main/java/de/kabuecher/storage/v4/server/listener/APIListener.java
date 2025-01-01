package de.kabuecher.storage.v4.server.listener;

import de.kabuecher.storage.v4.Main;
import de.kabuecher.storage.v4.server.user.*;
import de.kabuecher.storage.v4.server.query.QueryMethod;
import de.kabuecher.storage.v4.server.query.SevDeskQuery;
import de.kabuecher.storage.v4.server.storage.UnitManagement;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Base64;

public class APIListener implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) {
        if(httpServerExchange.getRequestURI().equalsIgnoreCase("/api/v1")) {

            if(!httpServerExchange.getRequestHeaders().contains("username")) {
                httpServerExchange.setStatusCode(401);
                httpServerExchange.getResponseSender().send("Unauthorized");
                return;
            }

            String username = httpServerExchange.getRequestHeaders().get("username").getFirst();
            String password = httpServerExchange.getRequestHeaders().get("password").getFirst();

            if(username == null || password == null) {
                httpServerExchange.setStatusCode(401);
                httpServerExchange.getResponseSender().send("Unauthorized");
                return;
            }

            UserManager userManager = new UserManager();
            User user = userManager.getUser(username);

            if(user == null || !userManager.passwordCorrect(user, password)) {
                httpServerExchange.setStatusCode(401);
                httpServerExchange.getResponseSender().send("Unauthorized");
                return;
            }

            if(user.invalidationDate() < ZonedDateTime.now().toInstant().toEpochMilli()) {
                httpServerExchange.setStatusCode(401);
                httpServerExchange.getResponseSender().send("Unauthorized");
                return;
            }

            Role role = user.getRole();

            httpServerExchange.getRequestReceiver().receiveFullString((exchange, message) -> {

                Main.addToLog("User " + user.getUsername() + " (" + exchange.getRequestHeaders().getFirst("X-Real-IP") + ") is executing API call with message " + message);

                JSONObject body = new JSONObject(message);
                if(!body.has("data")) {
                    body.put("data", new JSONObject());
                }

                String method = body.getString("method");
                JSONObject bodyData = body.getJSONObject("data");
                httpServerExchange.setStatusCode(200);

                if(method.equalsIgnoreCase("getOffer")) {

                    String offerID = bodyData.getString("offerID");
                    if(role == Role.INTERN) {

                        Main.addToLog("Getting offer with ID " + offerID);
                        JSONObject offerJson = SevDeskQuery.query("/Order/" + offerID, null, QueryMethod.GET);

                        httpServerExchange.getResponseSender().send(offerJson.toString());
                        httpServerExchange.getResponseSender().close();
                    } else {

                        if(offerID == null) {
                            httpServerExchange.setStatusCode(400);
                            httpServerExchange.getResponseSender().send("Bad Request");
                            return;
                        }

                        if(new IndividualizeUserExperienceManager().hasExecution(user.getContractNumber(), offerID)) {
                            Main.addToLog("Getting offer with ID " + offerID);
                            JSONObject offerJson = SevDeskQuery.query("/Order/" + offerID, null, QueryMethod.GET);

                            httpServerExchange.getResponseSender().send(offerJson.toString());
                            httpServerExchange.getResponseSender().close();
                        } else {
                            httpServerExchange.setStatusCode(403);
                            httpServerExchange.getResponseSender().send("Forbidden");
                        }
                    }

                } else if(method.equalsIgnoreCase("getOpenOffers")) {

                    if(role == Role.INTERN) {
                        Main.addToLog("Getting open offers");
                        JSONObject offersJson = SevDeskQuery.query("/Order?status=500&orderType=AN&createAfter=" + ZonedDateTime.now().minusDays(14), null, QueryMethod.GET);

                        System.out.println(offersJson);
                        JSONArray response = new JSONArray();
                        for (int i = 0; i < offersJson.getJSONArray("objects").length(); i++) {
                            response.put(offersJson.getJSONArray("objects").getJSONObject(i).getString("id"));
                        }


                        httpServerExchange.getResponseSender().send(new JSONObject().put("offers", response).toString());
                        httpServerExchange.getResponseSender().close();
                    } else {
                        if(new IndividualizeUserExperienceManager().hasExecution(user.getContractNumber(), "getOpenOffers")) {
                            Main.addToLog("Getting open offers");
                            JSONObject offersJson = SevDeskQuery.query("/Order?status=500&orderType=AN&createAfter=" + ZonedDateTime.now().minusDays(14), null, QueryMethod.GET);

                            System.out.println(offersJson);
                            JSONArray response = new JSONArray();
                            for (int i = 0; i < offersJson.getJSONArray("objects").length(); i++) {
                                if(!new IndividualizeUserExperienceManager().isExecution(offersJson.getJSONArray("objects").getJSONObject(i).getString("id"))) {
                                    response.put(offersJson.getJSONArray("objects").getJSONObject(i).getString("id"));
                                }
                            }

                            httpServerExchange.getResponseSender().send(new JSONObject().put("offers", response).toString());
                            httpServerExchange.getResponseSender().close();
                        } else {
                            httpServerExchange.setStatusCode(403);
                            httpServerExchange.getResponseSender().send("Forbidden");
                        }
                    }

                } else if(method.equalsIgnoreCase("setAddressOfDeliveryNote")) {

                    String offerID = bodyData.getString("offerID");
                    String address = bodyData.getString("address");
                    if(role == Role.INTERN) {

                        Main.addToLog("Setting address of delivery note with ID " + offerID + " to " + address);
                        SevDeskQuery.query("/Order/" + offerID, new JSONObject().put("address", address), QueryMethod.PUT);

                        httpServerExchange.getResponseSender().send("OK");
                        httpServerExchange.getResponseSender().close();
                    } else {

                        if(offerID == null || address == null) {
                            httpServerExchange.setStatusCode(400);
                            httpServerExchange.getResponseSender().send("Bad Request");
                            return;
                        }

                        if(new IndividualizeUserExperienceManager().hasExecution(user.getContractNumber(), offerID)) {
                            Main.addToLog("Setting address of delivery note with ID " + offerID + " to " + address);
                            SevDeskQuery.query("/Order/" + offerID, new JSONObject().put("address", address), QueryMethod.PUT);

                            httpServerExchange.getResponseSender().send("OK");
                            httpServerExchange.getResponseSender().close();
                        } else {
                            httpServerExchange.setStatusCode(403);
                            httpServerExchange.getResponseSender().send("Forbidden");
                        }
                    }

                } else if(method.equalsIgnoreCase("setOfferStatus")) {

                    String offerID = bodyData.getString("offerID");
                    int status = bodyData.getInt("status");
                    if(role == Role.INTERN) {

                        Main.addToLog("Setting status of offer with ID " + offerID + " to " + status);
                        SevDeskQuery.query("/Order/" + offerID, new JSONObject().put("status", status), QueryMethod.PUT);

                        httpServerExchange.getResponseSender().send("OK");
                        httpServerExchange.getResponseSender().close();
                    } else {

                        if(offerID == null || status == 0) {
                            httpServerExchange.setStatusCode(400);
                            httpServerExchange.getResponseSender().send("Bad Request");
                            return;
                        }

                        if(new IndividualizeUserExperienceManager().hasExecution(user.getContractNumber(), offerID)) {
                            Main.addToLog("Setting status of offer with ID " + offerID + " to " + status);
                            SevDeskQuery.query("/Order/" + offerID, new JSONObject().put("status", status), QueryMethod.PUT);

                            httpServerExchange.getResponseSender().send("OK");
                            httpServerExchange.getResponseSender().close();
                        } else {
                            httpServerExchange.setStatusCode(403);
                            httpServerExchange.getResponseSender().send("Forbidden");
                        }
                    }

                } else if(method.equalsIgnoreCase("transformOfferToConfirmation")) {

                    String offerID = bodyData.getString("offerID");
                    if (role == Role.INTERN) {

                        Main.addToLog("Transforming offer with ID " + offerID + " to confirmation");
                        JSONObject response = SevDeskQuery.query("/Order/" + offerID, new JSONObject().put("orderType", "AB"), QueryMethod.PUT);

                        httpServerExchange.getResponseSender().send(response.toString());
                        httpServerExchange.getResponseSender().close();
                    } else {

                        if (offerID == null) {
                            httpServerExchange.setStatusCode(400);
                            httpServerExchange.getResponseSender().send("Bad Request");
                            return;
                        }

                        if (new IndividualizeUserExperienceManager().hasExecution(user.getContractNumber(), offerID)) {
                            Main.addToLog("Transforming offer with ID " + offerID + " to confirmation");
                            JSONObject response = SevDeskQuery.query("/Order/" + offerID, new JSONObject().put("orderType", "AB"), QueryMethod.PUT);

                            httpServerExchange.getResponseSender().send(response.toString());
                            httpServerExchange.getResponseSender().close();
                        } else {
                            httpServerExchange.setStatusCode(403);
                            httpServerExchange.getResponseSender().send("Forbidden");
                        }
                    }

                } else if(method.equalsIgnoreCase("createDeliveryNote")) {

                    String offerID = bodyData.getString("offerID");
                    if(role == Role.INTERN) {

                        Main.addToLog("Creating delivery note from offer with ID " + offerID);
                        JSONObject deliveryNoteJson = SevDeskQuery.query("/Order/Factory/createPackingListFromOrder", new JSONObject().put("order", new JSONObject().put("id", offerID).put("objectName", "Order")), QueryMethod.POST);

                        httpServerExchange.getResponseSender().send(deliveryNoteJson.toString());
                        httpServerExchange.getResponseSender().close();
                    } else {

                        if(offerID == null) {
                            httpServerExchange.setStatusCode(400);
                            httpServerExchange.getResponseSender().send("Bad Request");
                            return;
                        }

                        if(new IndividualizeUserExperienceManager().hasExecution(user.getContractNumber(), offerID)) {
                            Main.addToLog("Creating delivery note from offer with ID " + offerID);
                            JSONObject deliveryNoteJson = SevDeskQuery.query("/Order/Factory/createPackingListFromOrder", new JSONObject().put("order", new JSONObject().put("id", offerID).put("objectName", "Order")), QueryMethod.POST);

                            httpServerExchange.getResponseSender().send(deliveryNoteJson.toString());
                            httpServerExchange.getResponseSender().close();
                        } else {
                            httpServerExchange.setStatusCode(403);
                            httpServerExchange.getResponseSender().send("Forbidden");
                        }
                    }

                } else if(method.equalsIgnoreCase("createInvoice")) {

                    String offerID = bodyData.getString("offerID");
                    if(role == Role.INTERN) {

                        Main.addToLog("Creating invoice from offer with ID " + offerID);
                        JSONObject invoiceJson = SevDeskQuery.query("/Invoice/Factory/createInvoiceFromOrder", new JSONObject().put("order", new JSONObject().put("id", offerID).put("objectName", "Order")), QueryMethod.POST);

                        httpServerExchange.getResponseSender().send(invoiceJson.toString());
                        httpServerExchange.getResponseSender().close();
                    } else {

                        if(offerID == null) {
                            httpServerExchange.setStatusCode(400);
                            httpServerExchange.getResponseSender().send("Bad Request");
                            return;
                        }

                        if(new IndividualizeUserExperienceManager().hasExecution(user.getContractNumber(), offerID)) {
                            Main.addToLog("Creating invoice from offer with ID " + offerID);
                            JSONObject invoiceJson = SevDeskQuery.query("/Invoice/Factory/createInvoiceFromOrder", new JSONObject().put("order", new JSONObject().put("id", offerID).put("objectName", "Order")), QueryMethod.POST);

                            httpServerExchange.getResponseSender().send(invoiceJson.toString());
                            httpServerExchange.getResponseSender().close();
                        } else {
                            httpServerExchange.setStatusCode(403);
                            httpServerExchange.getResponseSender().send("Forbidden");
                        }
                    }

                } else if(method.equalsIgnoreCase("getPart")) {

                    String partID = bodyData.getString("partID");

                    Main.addToLog("Getting part with ID " + partID);
                    JSONObject partJson = SevDeskQuery.query("/Part/" + partID, null, QueryMethod.GET);
                    partJson = partJson.getJSONArray("objects").getJSONObject(0);

                    httpServerExchange.getResponseSender().send(partJson.toString());
                    httpServerExchange.getResponseSender().close();

                } else if(method.equalsIgnoreCase("getOrderPositions")) {

                    String orderID = bodyData.getString("orderID");
                    if(role == Role.INTERN) {

                        Main.addToLog("Getting positions of order with ID " + orderID);
                        JSONObject orderJson = SevDeskQuery.query("/Order/" + orderID + "/getPositions", null, QueryMethod.GET);

                        httpServerExchange.getResponseSender().send(orderJson.toString());
                        httpServerExchange.getResponseSender().close();
                    } else {

                        if(orderID == null) {
                            httpServerExchange.setStatusCode(400);
                            httpServerExchange.getResponseSender().send("Bad Request");
                            return;
                        }

                        if(new IndividualizeUserExperienceManager().hasExecution(user.getContractNumber(), orderID)) {
                            Main.addToLog("Getting positions of order with ID " + orderID);
                            JSONObject orderJson = SevDeskQuery.query("/Order/" + orderID + "/getPositions", null, QueryMethod.GET);

                            httpServerExchange.getResponseSender().send(orderJson.toString());
                            httpServerExchange.getResponseSender().close();
                        } else {
                            httpServerExchange.setStatusCode(403);
                            httpServerExchange.getResponseSender().send("Forbidden");
                        }
                    }

                } else if(method.equalsIgnoreCase("getContact")) {

                    String contactID = bodyData.getString("contactID");

                    Main.addToLog("Getting contact with ID " + contactID);
                    JSONObject contactJson = SevDeskQuery.query("/Contact/" + contactID, null, QueryMethod.GET);

                    httpServerExchange.getResponseSender().send(contactJson.toString());
                    httpServerExchange.getResponseSender().close();

                } else if(method.equalsIgnoreCase("getContactID")) {

                    String contactName = bodyData.getString("contactName");

                    Main.addToLog("Getting contact ID for contact name " + contactName);
                    JSONObject contactJson = SevDeskQuery.query("/Contact?search=" + contactName, null, QueryMethod.GET);

                    httpServerExchange.getResponseSender().send(contactJson.toString());
                    httpServerExchange.getResponseSender().close();

                } else if(method.equalsIgnoreCase("printOrderID")) {

                    String orderID = bodyData.getString("orderID");
                    if(role == Role.INTERN) {

                        Main.addToLog("Printing order with ID " + orderID);
                        JSONObject orderJson = SevDeskQuery.query("/Order/" + orderID + "/getPdf", null, QueryMethod.GET);

                        httpServerExchange.getResponseSender().send(orderJson.toString());
                    } else {

                        if(orderID == null) {
                            httpServerExchange.setStatusCode(400);
                            httpServerExchange.getResponseSender().send("Bad Request");
                            return;
                        }

                        if(new IndividualizeUserExperienceManager().hasExecution(user.getContractNumber(), orderID)) {
                            Main.addToLog("Printing order with ID " + orderID);
                            JSONObject orderJson = SevDeskQuery.query("/Order/" + orderID + "/getPdf", null, QueryMethod.GET);

                            httpServerExchange.getResponseSender().send(orderJson.toString());
                        } else {
                            httpServerExchange.setStatusCode(403);
                            httpServerExchange.getResponseSender().send("Forbidden");
                        }
                    }

                } else if(method.equalsIgnoreCase("printInvoiceID")) {

                    String invoiceID = bodyData.getString("invoiceID");
                    if(role == Role.INTERN) {

                        Main.addToLog("Printing invoice with ID " + invoiceID);
                        JSONObject orderJson = SevDeskQuery.query("/Invoice/" + invoiceID + "/getPdf", null, QueryMethod.GET);

                        httpServerExchange.getResponseSender().send(orderJson.toString());
                    } else {

                        if (invoiceID == null) {
                            httpServerExchange.setStatusCode(400);
                            httpServerExchange.getResponseSender().send("Bad Request");
                            return;
                        }

                        if (new IndividualizeUserExperienceManager().hasExecution(user.getContractNumber(), invoiceID)) {
                            Main.addToLog("Printing invoice with ID " + invoiceID);
                            JSONObject orderJson = SevDeskQuery.query("/Invoice/" + invoiceID + "/getPdf", null, QueryMethod.GET);

                            httpServerExchange.getResponseSender().send(orderJson.toString());
                        } else {
                            httpServerExchange.setStatusCode(403);
                            httpServerExchange.getResponseSender().send("Forbidden");
                        }
                    }
                } else if(method.equalsIgnoreCase("generateShippingLabel")) {

                    String orderID = bodyData.getString("orderID");
                    double weight = bodyData.getDouble("weight");
                    int height = bodyData.getInt("height");
                    if(role == Role.INTERN) {

                        Main.addToLog("Generating shipping label for order with ID " + orderID);

                        JSONObject offer = SevDeskQuery.query("/Order/" + orderID, null, QueryMethod.GET);

                        Main.addToLog("Getting parcel label for offer " + offer.getString("id"));
                        String fullAddress = offer.getString("address");

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

                        JSONObject object = SevDeskQuery.query("/Contact/" + offer.getJSONObject("contact").getString("id"), null, QueryMethod.GET).getJSONObject("objects");
                        boolean isCompany = object.has("name");
                        if(isCompany) {
                            if(object.getString("name").isEmpty()) {
                                isCompany = false;
                            }
                        }

                        JSONObject fin = new JSONObject();
                        fin.put("parcel", new JSONObject()
                                .put("name", offer.getString("address").split("\n")[0])
                                .put("company_name", (isCompany ? object.getString("name") : ""))
                                .put("address", address)
                                .put("house_number", houseNumber)
                                .put("address_2", address2)
                                .put("city", city)
                                .put("country", "DE")
                                .put("postal_code", postalCode)
                                .put("weight", weight)
                                .put("total_order_value", String.valueOf(offer.getDouble("sumGross")))
                                .put("total_order_value_currency", "EUR")
                                .put("quantity", 1)
                                .put("is_return", false)
                                .put("request_label", true)
                                .put("apply_shipping_rules", false)
                                .put("request_label_async", false)
                                .put("shipment", (weight > 1001 ? new JSONObject().put("id", 3805).put("name", "DPD Shop2Home XS 0-20kg") : (height > 5 ? new JSONObject().put("id", 3805).put("name", "DPD Shop2Home XS 0-20kg") : new JSONObject().put("id", 2831).put("name", "DHL Warenpost GoGreen 0-1kg"))))
                        );

                        UserContractStatisticTracking userContractStatisticTracking = new UserContractStatisticTracking();
                        userContractStatisticTracking.addShippingCost((weight > 1001 ? 4.5 : (height > 5 ? 4.5 : 3.5)), user.getContractNumber());

                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("https://panel.sendcloud.sc/api/v2/parcels"))
                                .header("Accept", "application/json")
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Basic " + Main.getJsonFile().get("sendcloud").getString("api_key"))
                                .POST(HttpRequest.BodyPublishers.ofString(fin.toString()))
                                .build();

                        Main.addToLog("Requesting parcel label from SendCloud");

                        try {
                            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

                            HttpResponse<InputStream> response1 = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                                    .uri(URI.create(new JSONObject(response.body()).getJSONObject("parcel").getJSONObject("label").getString("label_printer")))
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Basic " + Main.getJsonFile().get("sendcloud").getString("api_key"))
                                    .GET()
                                    .build(), HttpResponse.BodyHandlers.ofInputStream());

                            Main.addToLog("Printing document with printer " + Main.getJsonFile().get("printerConfig").getString("label_printer"));

                            httpServerExchange.getResponseSender().send(Arrays.toString(Base64.getEncoder().encode(response1.body().readAllBytes())));
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {

                        if(orderID == null) {
                            httpServerExchange.setStatusCode(400);
                            httpServerExchange.getResponseSender().send("Bad Request");
                            return;
                        }

                        if(new IndividualizeUserExperienceManager().hasExecution(user.getContractNumber(), orderID)) {
                            Main.addToLog("Generating shipping label for order with ID " + orderID);

                            JSONObject offer = SevDeskQuery.query("/Order/" + orderID, null, QueryMethod.GET);

                            Main.addToLog("Getting parcel label for offer " + offer.getString("id"));
                            String fullAddress = offer.getString("address");

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

                            JSONObject object = SevDeskQuery.query("/Contact/" + offer.getJSONObject("contact").getString("id"), null, QueryMethod.GET).getJSONObject("objects");
                            boolean isCompany = object.has("name");
                            if(isCompany) {
                                if(object.getString("name").isEmpty()) {
                                    isCompany = false;
                                }
                            }

                            JSONObject fin = new JSONObject();
                            fin.put("parcel", new JSONObject()
                                    .put("name", offer.getString("address").split("\n")[0])
                                    .put("company_name", (isCompany ? object.getString("name") : ""))
                                    .put("address", address)
                                    .put("house_number", houseNumber)
                                    .put("address_2", address2)
                                    .put("city", city)
                                    .put("country", "DE")
                                    .put("postal_code", postalCode)
                                    .put("weight", weight)
                                    .put("total_order_value", String.valueOf(offer.getDouble("sumGross")))
                                    .put("total_order_value_currency", "EUR")
                                    .put("quantity", 1)
                                    .put("is_return", false)
                                    .put("request_label", true)
                                    .put("apply_shipping_rules", false)
                                    .put("request_label_async", false)
                                    .put("shipment", (weight > 1001 ? new JSONObject().put("id", 3805).put("name", "DPD Shop2Home XS 0-20kg") : (height > 5 ? new JSONObject().put("id", 3805).put("name", "DPD Shop2Home XS 0-20kg") : new JSONObject().put("id", 2831).put("name", "DHL Warenpost GoGreen 0-1kg"))))
                            );

                            UserContractStatisticTracking userContractStatisticTracking = new UserContractStatisticTracking();
                            userContractStatisticTracking.addShippingCost((weight > 1001 ? 4.5 : (height > 5 ? 4.5 : 3.5)), user.getContractNumber());


                            HttpRequest request = HttpRequest.newBuilder()
                                    .uri(URI.create("https://panel.sendcloud.sc/api/v2/parcels"))
                                    .header("Accept", "application/json")
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Basic " + Main.getJsonFile().get("sendcloud").getString("api_key"))
                                    .POST(HttpRequest.BodyPublishers.ofString(fin.toString()))
                                    .build();

                            Main.addToLog("Requesting parcel label from SendCloud");

                            try {
                                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

                                HttpResponse<InputStream> response1 = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                                        .uri(URI.create(new JSONObject(response.body()).getJSONObject("parcel").getJSONObject("label").getString("label_printer")))
                                        .header("Content-Type", "application/json")
                                        .header("Authorization", "Basic " + Main.getJsonFile().get("sendcloud").getString("api_key"))
                                        .GET()
                                        .build(), HttpResponse.BodyHandlers.ofInputStream());

                                Main.addToLog("Printing document with printer " + Main.getJsonFile().get("printerConfig").getString("label_printer"));

                                httpServerExchange.getResponseSender().send(Arrays.toString(Base64.getEncoder().encode(response1.body().readAllBytes())));
                            } catch (IOException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            httpServerExchange.setStatusCode(403);
                            httpServerExchange.getResponseSender().send("Forbidden");
                        }
                    }
                } else if(method.equalsIgnoreCase("addItemToStore")) {
                    UnitManagement unitManagement = new UnitManagement();
                    String stackName = bodyData.getString("stackName");
                    String boxId = bodyData.getString("boxId");
                    String itemId = bodyData.getString("itemId");
                    int quantity = bodyData.getInt("quantity");

                    Main.addToLog("Adding " + quantity + " of item " + itemId + " to box " + boxId + " in stack " + stackName + " in store " + username);
                    unitManagement.addItemToBox(username, stackName, boxId, itemId, quantity);

                    httpServerExchange.getResponseSender().send("OK");
                } else if(method.equalsIgnoreCase("searchForItemInStore")) {
                    UnitManagement unitManagement = new UnitManagement();

                    JSONObject response = unitManagement.searchForItemInStore(username, bodyData.getString("itemId"), bodyData.getInt("quantity"));

                    httpServerExchange.getResponseSender().send(response.toString());
                } else if(method.equalsIgnoreCase("getStoredItems")) {
                    UnitManagement unitManagement = new UnitManagement();

                    JSONObject response = unitManagement.getStoredItems(username);

                    httpServerExchange.getResponseSender().send(response.toString());
                } else if(method.equalsIgnoreCase("removeItemFromStore")) {
                    UnitManagement unitManagement = new UnitManagement();
                    String stackName = bodyData.getString("stackName");
                    String boxId = bodyData.getString("boxId");
                    String itemId = bodyData.getString("itemId");
                    int quantity = bodyData.getInt("quantity");

                    Main.addToLog("Removing " + quantity + " of item " + itemId + " from box " + boxId + " in stack " + stackName + " in store " + username);
                    boolean success = unitManagement.removeItem(username, stackName, boxId, itemId, quantity);

                    httpServerExchange.getResponseSender().send(success ? "OK" : "NOT OK");
                    httpServerExchange.setStatusCode(success ? 200 : 400);
                } else {
                    httpServerExchange.setStatusCode(400);
                    httpServerExchange.getResponseSender().send("Bad Request");
                }
            });

        } else {
            httpServerExchange.setStatusCode(404);
            httpServerExchange.getResponseSender().send("Not Found");
        }

    }

}
