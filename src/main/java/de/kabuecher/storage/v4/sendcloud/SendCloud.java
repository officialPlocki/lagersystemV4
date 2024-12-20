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

            //{"parcel":{"shipment_uuid":null,"country":{"iso_3":"DEU","iso_2":"DE","name":"Germany"},"data":{},"documents":[{"size":"a6","link":"https://panel.sendcloud.sc/api/v2/parcels/449391715/documents/label","type":"label"}],"address_2":"","type":"parcel","reference":"0","is_return":false,"shipping_method":3805,"parcel_items":[],"external_reference":null,"id":449391715,"height":null,"address_divided":{"street":"Kaiserstraße","house_number":"167"},"box_number":null,"shipment":{"name":"DPD Shop2Home XS 0-20kg","id":3805},"contract":569,"date_announced":"20-12-2024 22:36:00","customs_information":null,"weight":"5.000","telephone":"","customs_declaration":{},"to_post_number":"","company_name":"","name":"Thalia Deutschland GmbH & Co. KG","external_order_id":"449391715","insured_value":0,"status":{"id":1000,"message":"Ready to send"},"note":"","total_order_value_currency":"EUR","date_updated":"20-12-2024 22:36:01","city":"Karlsruhe","customs_shipment_type":null,"order_number":"","customs_invoice_nr":"","to_state":null,"total_insured_value":520,"tracking_number":"09447095660340","collo_nr":0,"total_order_value":"14.50","tracking_url":"https://tracking.eu-central-1-0.sendcloud.sc/forward?carrier=dpd&code=09447095660340&destination=DE&lang=de-de&source=DE&type=parcel&verification=76133&servicepoint_verification=&shipping_product_code=dpd%3Ashop2home&created_at=2024-12-20","email":"","shipping_method_checkout_name":null,"to_service_point":null,"address":"Kaiserstraße 167","date_created":"20-12-2024 22:35:59","length":null,"label":{"label_printer":"https://panel.sendcloud.sc/api/v2/labels/label_printer/449391715","normal_printer":["https://panel.sendcloud.sc/api/v2/labels/normal_printer/449391715?start_from=0","https://panel.sendcloud.sc/api/v2/labels/normal_printer/449391715?start_from=1","https://panel.sendcloud.sc/api/v2/labels/normal_printer/449391715?start_from=2","https://panel.sendcloud.sc/api/v2/labels/normal_printer/449391715?start_from=3"]},"colli_uuid":"66bf1137-44d8-4488-b012-09e90b5c634c","collo_count":1,"awb_tracking_number":null,"carrier":{"code":"dpd"},"external_shipment_id":"","width":null,"postal_code":"76133"}}

            HttpResponse<InputStream> response1 = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                    .uri(URI.create(new JSONObject(response.body()).getJSONObject("parcel").getJSONArray("documents").getJSONObject(0).getString("link")))
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
