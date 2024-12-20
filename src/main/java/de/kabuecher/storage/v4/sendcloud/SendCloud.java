package de.kabuecher.storage.v4.sendcloud;

import de.kabuecher.storage.v4.Main;
import de.kabuecher.storage.v4.sevdesk.impl.offer.Offer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.json.JSONObject;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class SendCloud {

    public void getParcelLabel(Offer offer, double weight, int height) {

        Main.addToLog("Getting parcel label for offer " + offer.getId());
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

            try {
                PDDocument document = PDDocument.load(response1.body());
                PrinterJob job = PrinterJob.getPrinterJob();

                for (PrintService printService : PrintServiceLookup.lookupPrintServices(null, null)) {
                    if(printService.getName().equals(Main.getJsonFile().get("printerConfig").getString("label_printer"))) {
                        job.setPrintService(printService);
                        break;
                    }
                }

                Paper paper = job.defaultPage().getPaper();

                PageFormat format = new PageFormat();
                format.setPaper(paper);

                Book book = new Book();
                book.append(new PDFPrintable(document, Scaling.SHRINK_TO_FIT), format, document.getNumberOfPages());
                job.setPageable(book);

                job.print();
                Main.addToLog("Document printed");
            } catch (IOException | PrinterException e) {
                throw new RuntimeException(e);
            };
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
