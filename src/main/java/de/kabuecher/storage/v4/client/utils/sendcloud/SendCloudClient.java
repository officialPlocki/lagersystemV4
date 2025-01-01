package de.kabuecher.storage.v4.client.utils.sendcloud;

import de.kabuecher.storage.v4.Main;
import de.kabuecher.storage.v4.client.sevdesk.offer.Offer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.json.JSONObject;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class SendCloudClient {

    public void getParcelLabel(Offer offer, double weight, int height) {

        Main.addToLog("Getting parcel label for offer " + offer.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://hub.kabuecher.de/api/v1"))
                .header("Content-Type", "application/json")
                .header("username", Main.username)
                .header("password", Main.passwordHash)
                .PUT(HttpRequest.BodyPublishers.ofString(new JSONObject().put("method", "generateShippingLabel").put("data", new JSONObject().put("orderID", offer.getId()).put("height", height).put("weight", weight)).toString()))
                .build();


        try {
            HttpResponse<String> response1 = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if(response1.statusCode() != 200) {
                throw new RuntimeException("Failed to get parcel label: " + response1.body());
            }

            byte[] bytes = Base64.getDecoder().decode(response1.body());

            Main.addToLog("Printing document with printer " + Main.getJsonFile().get("printerConfig").getString("label_printer"));

            try {
                PDDocument document = PDDocument.load(bytes);
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
